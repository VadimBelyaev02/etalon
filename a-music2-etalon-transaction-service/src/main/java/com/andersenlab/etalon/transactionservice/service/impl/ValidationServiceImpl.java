package com.andersenlab.etalon.transactionservice.service.impl;

import static com.andersenlab.etalon.transactionservice.repository.specifications.TransactionSpecifications.hasDetails;
import static com.andersenlab.etalon.transactionservice.repository.specifications.TransactionSpecifications.hasProcessedBetween;
import static com.andersenlab.etalon.transactionservice.repository.specifications.TransactionSpecifications.hasSenderAccount;
import static com.andersenlab.etalon.transactionservice.repository.specifications.TransactionSpecifications.hasStatus;
import static com.andersenlab.etalon.transactionservice.util.enums.TransactionStatus.APPROVED;
import static org.springframework.data.jpa.domain.Specification.where;

import com.andersenlab.etalon.transactionservice.client.CardServiceClient;
import com.andersenlab.etalon.transactionservice.dto.account.response.AccountDetailedResponseDto;
import com.andersenlab.etalon.transactionservice.dto.card.response.CardResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.StatusMessageResponseDto;
import com.andersenlab.etalon.transactionservice.entity.TransactionEntity;
import com.andersenlab.etalon.transactionservice.exception.BusinessException;
import com.andersenlab.etalon.transactionservice.repository.PaymentTypeRepository;
import com.andersenlab.etalon.transactionservice.repository.TemplateRepository;
import com.andersenlab.etalon.transactionservice.repository.TransactionRepository;
import com.andersenlab.etalon.transactionservice.repository.TransferTypeRepository;
import com.andersenlab.etalon.transactionservice.service.AccountService;
import com.andersenlab.etalon.transactionservice.service.ValidationService;
import com.andersenlab.etalon.transactionservice.util.enums.Details;
import com.andersenlab.etalon.transactionservice.util.enums.PaymentStatus;
import com.andersenlab.etalon.transactionservice.util.enums.TransferStatus;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValidationServiceImpl implements ValidationService {

  private final TemplateRepository templateRepository;
  private final TransferTypeRepository transferTypeRepository;
  private final PaymentTypeRepository paymentTypeRepository;
  private final CardServiceClient cardServiceClient;
  private final TransactionRepository transactionRepository;
  private final AccountService accountService;

  @Override
  public void validateOwnership(
      List<String> validatingAccounts, List<String> validAccounts, BusinessException exception) {
    if (validatingAccounts.stream().noneMatch(new HashSet<>(validAccounts)::contains)) {
      throw exception;
    }
  }

  @Override
  public void validateAmountMoreThanOne(BigDecimal amount) {
    validateAmount(amount);
    if (amount.compareTo(BigDecimal.ONE) < 0) {
      throw new BusinessException(
          HttpStatus.BAD_REQUEST,
          BusinessException.OPERATION_REJECTED_DUE_TO_INCORRECT_PAYMENT_AMOUNT);
    }
  }

  @Override
  public void validateAmountMoreThanZero(BigDecimal amount) {
    validateAmount(amount);
    if (amount.compareTo(BigDecimal.ZERO) < 1) {
      throw new BusinessException(
          HttpStatus.BAD_REQUEST,
          BusinessException.OPERATION_REJECTED_DUE_TO_INCORRECT_TRANSACTION_AMOUNT);
    }
  }

  private void validateAmount(BigDecimal amount) {
    if (Objects.isNull(amount)) {
      throw new BusinessException(
          HttpStatus.BAD_REQUEST, BusinessException.OPERATION_REJECTED_DUE_TO_EMPTY_AMOUNT_FIELD);
    }
    if (amount.scale() > 2) {
      throw new BusinessException(
          HttpStatus.BAD_REQUEST,
          BusinessException.OPERATION_REJECTED_BECAUSE_AMOUNT_HAS_MORE_THAN_2_DIGITS_AFTER_DOT);
    }
  }

  @Override
  public StatusMessageResponseDto validateTemplateName(String templateName, String userId) {
    return Objects.isNull(templateRepository.findByTemplateNameAndUserId(templateName, userId))
        ? new StatusMessageResponseDto(true, StatusMessageResponseDto.TEMPLATE_NAME_IS_VALID)
        : new StatusMessageResponseDto(false, BusinessException.TEMPLATE_NAME_ALREADY_BEEN_SAVED);
  }

  @Override
  public void validatePaymentStatus(PaymentStatus status) {
    if (PaymentStatus.APPROVED.equals(status)) {
      throw new BusinessException(HttpStatus.CONFLICT, BusinessException.OPERATION_CONFLICT);
    }
  }

  @Override
  public void validateTransferStatus(TransferStatus status) {
    if (TransferStatus.APPROVED.equals(status)) {
      throw new BusinessException(HttpStatus.CONFLICT, BusinessException.OPERATION_CONFLICT);
    }
  }

  @Override
  public void validateAccount(AccountDetailedResponseDto accountDetailedResponseDto) {
    if (Boolean.TRUE.equals(accountDetailedResponseDto.isBlocked())) {
      throw new BusinessException(HttpStatus.BAD_REQUEST, BusinessException.ACCOUNT_IS_BLOCKED);
    }
  }

  @Override
  public void validateProductType(Long productId) {
    if (transferTypeRepository.findById(productId).isEmpty()
        && paymentTypeRepository.findById(productId).isEmpty()) {
      throw new BusinessException(
          HttpStatus.NOT_FOUND, BusinessException.PRODUCT_TYPE_DOES_NOT_EXIST);
    }
  }

  @Override
  public void validateAccountBalance(BigDecimal amount, String accountNumber) {
    AccountDetailedResponseDto accountDetailedResponseDto =
        Optional.of(accountService.getDetailedAccountInfo(accountNumber))
            .orElseThrow(
                () ->
                    new BusinessException(
                        HttpStatus.NOT_FOUND,
                        String.format(
                            BusinessException.NOT_FOUND_ACCOUNT_BY_NUMBER, accountNumber)));
    if (accountDetailedResponseDto.balance().compareTo(amount) <= 0) {
      throw new BusinessException(
          HttpStatus.CONFLICT,
          String.format(
              BusinessException.NOT_ENOUGH_FUNDS_ON_ACCOUNT, accountDetailedResponseDto.iban()));
    }
  }

  @Override
  public void validateCardLimits(String accountNumber, BigDecimal amount) {
    CardResponseDto cardResponseDto = getCardByAccountNumber(accountNumber);
    if (cardResponseDto.transferLimit().compareTo(amount) < 0) {
      throw new BusinessException(
          HttpStatus.CONFLICT,
          String.format(
              BusinessException.LIMIT_OF_TRANSFER_AMOUNT_EXCEEDED,
              cardResponseDto.transferLimit()));
    }
    checkForDailyLimitExceeding(accountNumber, amount, cardResponseDto.dailyExpenseLimit());
  }

  private CardResponseDto getCardByAccountNumber(String accountNumber) {
    log.info("{getCardByAccountNumber} Get card by account number {}", accountNumber);
    return cardServiceClient.getAllUserCards(accountNumber).stream()
        .findFirst()
        .orElseThrow(
            () ->
                new BusinessException(
                    HttpStatus.NOT_FOUND,
                    String.format(
                        BusinessException.NO_CARD_FOUND_BY_ACCOUNT_NUMBER, accountNumber)));
  }

  private void checkForDailyLimitExceeding(
      String accountNumber, BigDecimal amount, BigDecimal dailyExpenseLimit) {
    log.info(
        "{checkForDailyLimitExceeding} Checking daily limit for account {}. Daily limit is {}",
        accountNumber,
        dailyExpenseLimit);

    BigDecimal totalExpensesPerDay = getDailyExpenses(accountNumber);
    log.info(
        "{checkForDailyLimitExceeding} Daily expenses received {} for account {}",
        totalExpensesPerDay,
        accountNumber);
    if (dailyExpenseLimit.compareTo(totalExpensesPerDay.add(amount)) < 0) {
      throw new BusinessException(
          HttpStatus.CONFLICT, BusinessException.DAILY_EXPENSES_LIMIT_EXCEEDED);
    }
  }

  private BigDecimal getDailyExpenses(String accountNumber) {
    log.info("{getDailyExpenses} Getting daily expenses for account -> {}", accountNumber);
    ZonedDateTime startOfDay =
        ZonedDateTime.now().toLocalDate().atStartOfDay(ZonedDateTime.now().getZone());
    ZonedDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);
    log.info(
        "{getDailyExpenses} Daily expenses requested for time between {} - {}",
        startOfDay,
        endOfDay);
    return transactionRepository
        .findAll(
            where(
                hasSenderAccount(accountNumber)
                    .and(hasProcessedBetween(startOfDay, endOfDay))
                    .and(hasStatus(APPROVED))
                    .and(hasDetails(Details.PAYMENT))))
        .stream()
        .map(TransactionEntity::getAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}

package com.andersenlab.etalon.loanservice.service.impl;

import com.andersenlab.etalon.loanservice.client.AccountServiceClient;
import com.andersenlab.etalon.loanservice.client.TransactionServiceClient;
import com.andersenlab.etalon.loanservice.config.TimeProvider;
import com.andersenlab.etalon.loanservice.dto.account.request.AccountCreationRequestDto;
import com.andersenlab.etalon.loanservice.dto.account.response.AccountBalanceResponseDto;
import com.andersenlab.etalon.loanservice.dto.account.response.AccountDetailedResponseDto;
import com.andersenlab.etalon.loanservice.dto.account.response.AccountResponseDto;
import com.andersenlab.etalon.loanservice.dto.calculation.LoanCalculationResult;
import com.andersenlab.etalon.loanservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.loanservice.dto.loan.request.CollectLoanRequestDto;
import com.andersenlab.etalon.loanservice.dto.loan.request.LoanPaymentRequestDto;
import com.andersenlab.etalon.loanservice.dto.loan.response.LoanDetailedResponseDto;
import com.andersenlab.etalon.loanservice.dto.loan.response.LoanResponseDto;
import com.andersenlab.etalon.loanservice.dto.transaction.request.TransactionCreateRequestDto;
import com.andersenlab.etalon.loanservice.entity.LoanEntity;
import com.andersenlab.etalon.loanservice.entity.LoanOrderEntity;
import com.andersenlab.etalon.loanservice.entity.LoanProductEntity;
import com.andersenlab.etalon.loanservice.exception.BusinessException;
import com.andersenlab.etalon.loanservice.mapper.LoanMapper;
import com.andersenlab.etalon.loanservice.repository.LoanOrderRepository;
import com.andersenlab.etalon.loanservice.repository.LoanPaymentRepository;
import com.andersenlab.etalon.loanservice.repository.LoanProductRepository;
import com.andersenlab.etalon.loanservice.repository.LoanRepository;
import com.andersenlab.etalon.loanservice.service.GeneratorService;
import com.andersenlab.etalon.loanservice.service.LoanService;
import com.andersenlab.etalon.loanservice.service.strategies.impl.LoanPaymentProcessor;
import com.andersenlab.etalon.loanservice.util.LoanUtils;
import com.andersenlab.etalon.loanservice.util.enums.AccountType;
import com.andersenlab.etalon.loanservice.util.enums.Details;
import com.andersenlab.etalon.loanservice.util.enums.LoanStatus;
import com.andersenlab.etalon.loanservice.util.enums.OrderStatus;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class LoanServiceImpl implements LoanService {

  private static final String REPLENISH_LOAN_TRANSACTION_NAME = "Payment for %s";
  private static final String CREATE_LOAN_TRANSACTION_NAME = "Receiving money from %s";

  private final LoanRepository loanRepository;
  private final LoanProductRepository loanProductRepository;
  private final LoanOrderRepository loanOrderRepository;
  private final LoanMapper loanMapper;
  private final AccountServiceClient accountServiceClient;
  private final GeneratorService generatorService;
  private final TransactionServiceClient transactionServiceClient;
  private final TimeProvider timeProvider;
  private final LoanPaymentRepository loanPaymentRepository;
  private final LoanPaymentProcessor loanPaymentProcessor;
  public static final String BANK_ACCOUNT_NUMBER = "PL05234567840000000000000000";

  @Override
  public List<LoanResponseDto> getAllLoans(String id, LoanStatus status) {

    List<LoanEntity> loansByUserId = loanRepository.findAllByUserId(id);

    return loansByUserId.stream()
        .filter(
            loanEntity ->
                (Objects.isNull(status) || loanEntity.getStatus().equals(status))
                    && !loanEntity.getStatus().equals(LoanStatus.CLOSED))
        .sorted(
            Comparator.comparing(
                    (LoanEntity loanEntity) -> loanEntity.getStatus() != LoanStatus.ACTIVE)
                .thenComparing(LoanEntity::getCreateAt))
        .map(
            entity -> {
              BigDecimal loanDebtGrossAmount =
                  LoanUtils.calculateLoanPayments(
                          LoanUtils.collectLoanCalculationInitialData(entity),
                          accountServiceClient
                              .getAccountBalanceByAccountNumber(entity.getAccountNumber())
                              .accountBalance(),
                          timeProvider.getCurrentZonedDateTime())
                      .loanDebtGrossAmount();

              BigDecimal penalty = calculatePenalty(entity);

              LoanResponseDto responseDto = loanMapper.loanEntityToLoanResponseDto(entity);
              return responseDto.toBuilder()
                  .nextPaymentAmount(loanDebtGrossAmount.add(penalty))
                  .build();
            })
        .toList();
  }

  @Override
  public LoanDetailedResponseDto getDetailedLoan(final Long loanId, final String userId) {

    LoanEntity entity =
        loanRepository
            .findByIdAndUserId(loanId, userId)
            .orElseThrow(
                () ->
                    new BusinessException(
                        HttpStatus.NOT_FOUND,
                        String.format(BusinessException.LOAN_NOT_FOUND_BY_ID, loanId)));

    final AccountBalanceResponseDto loanAccountBalanceResponseDto =
        accountServiceClient.getAccountBalanceByAccountNumber(entity.getAccountNumber());

    BigDecimal accountBalance = loanAccountBalanceResponseDto.accountBalance();

    LoanCalculationResult loanCalculationResult =
        LoanUtils.calculateLoanPayments(
            LoanUtils.collectLoanCalculationInitialData(entity),
            accountBalance,
            timeProvider.getCurrentZonedDateTime());

    BigDecimal penalty = calculatePenalty(entity);

    return loanMapper.loanEntityToLoanDetailedDto(entity).toBuilder()
        .debtNetAmount(entity.getAmount().subtract(accountBalance))
        .shouldBePayedThisMonthCommission(loanCalculationResult.monthlyCommissionAmount())
        .shouldBePayedThisMonthInterest(loanCalculationResult.loanInterestAmount())
        .shouldBePayedThisMonthNet(loanCalculationResult.loanDebtNetAmount())
        .nextPaymentAmount(loanCalculationResult.loanDebtGrossAmount().add(penalty))
        .penalty(penalty)
        .build();
  }

  @Override
  @Transactional
  public MessageResponseDto makeLoanPayment(
      final Long loanId, final LoanPaymentRequestDto requestDto) {
    LoanEntity loanEntity =
        loanRepository
            .findById(loanId)
            .orElseThrow(
                () ->
                    new BusinessException(
                        HttpStatus.NOT_FOUND,
                        String.format(BusinessException.LOAN_NOT_FOUND_BY_ID, loanId)));

    loanPaymentProcessor.processLoanPayment(loanEntity, requestDto);

    loanRepository.save(loanEntity);

    String message =
        loanEntity.getStatus() == LoanStatus.CLOSED
            ? MessageResponseDto.LOAN_CLOSED_SUCCESSFULLY
            : MessageResponseDto.PAYMENT_SUCCEEDED;

    return new MessageResponseDto(message);
  }

  @Override
  @Transactional
  public MessageResponseDto openNewLoan(final String userId, final CollectLoanRequestDto dto) {

    LoanOrderEntity loanOrder = getLoanOrder(dto, userId);
    validateLoanOrderStatus(loanOrder);

    LoanEntity loanEntity = collectLoan(userId, loanOrder, dto.accountNumber());
    loanRepository.save(loanEntity);

    loanOrder.setStatus(OrderStatus.CLOSED);
    loanOrderRepository.save(loanOrder);

    return new MessageResponseDto(MessageResponseDto.LOAN_OPENED);
  }

  public LoanEntity collectLoan(
      final String userId, final LoanOrderEntity loanOrder, final String receiverAccountNumber) {

    LoanProductEntity loanProduct =
        loanProductRepository
            .findById(loanOrder.getProduct().getId())
            .orElseThrow(
                () ->
                    new BusinessException(
                        HttpStatus.NOT_FOUND,
                        String.format(
                            BusinessException.LOAN_PRODUCT_NOT_FOUND_BY_ID,
                            loanOrder.getProduct().getId())));

    checkAccountValidity(userId, receiverAccountNumber);

    final AccountCreationRequestDto accountCreation =
        new AccountCreationRequestDto(userId, String.valueOf(AccountType.LOAN));
    final AccountResponseDto accountResponseDto =
        accountServiceClient.createAccount(accountCreation);

    transactionServiceClient.createTransaction(
        TransactionCreateRequestDto.builder()
            .accountNumberReplenished(receiverAccountNumber)
            .accountNumberWithdrawn(BANK_ACCOUNT_NUMBER)
            .amount(loanOrder.getAmount())
            .loanInterestAmount(BigDecimal.ZERO)
            .details(Details.LOAN)
            .feeAmount(BigDecimal.ZERO)
            .isFeeProvided(false)
            .transactionName(
                String.format(CREATE_LOAN_TRANSACTION_NAME, loanOrder.getProduct().getName()))
            .build());

    Long loanId = getLoanId();

    return LoanEntity.builder()
        .userId(userId)
        .contractNumber(generatorService.generateContractNumber(loanId))
        .amount(loanOrder.getAmount())
        .product(loanProduct)
        .nextPaymentDate(timeProvider.getCurrentZonedDateTime().plusMonths(1))
        .status(LoanStatus.ACTIVE)
        .accountNumber(accountResponseDto.iban())
        .build();
  }

  private void checkAccountValidity(String userId, String receiverAccountNumber) {

    AccountDetailedResponseDto accountDetailedResponseDto =
        accountServiceClient.getDetailedAccountInfo(receiverAccountNumber);
    if (!accountDetailedResponseDto.userId().equals(userId)) {
      throw new BusinessException(
          HttpStatus.BAD_REQUEST,
          BusinessException.TRANSACTION_REJECTED_DUE_TO_INVALID_ACCOUNT_NUMBER);
    }
    if (Boolean.TRUE.equals(accountDetailedResponseDto.isBlocked())) {
      throw new BusinessException(
          HttpStatus.BAD_REQUEST, BusinessException.TRANSACTION_REJECTED_DUE_TO_ACCOUNT_BLOCKING);
    }
  }

  public Long getLoanId() {
    return loanRepository.findFirstByOrderByIdDesc().map(LoanEntity::getId).orElse(null);
  }

  private void validateLoanOrderStatus(LoanOrderEntity loanOrder) {
    switch (loanOrder.getStatus()) {
      case REJECTED -> throw new BusinessException(
          HttpStatus.BAD_REQUEST,
          String.format(BusinessException.LOAN_ORDER_WITH_ID_REJECTED, loanOrder.getId()));
      case IN_REVIEW -> throw new BusinessException(
          HttpStatus.BAD_REQUEST,
          String.format(BusinessException.LOAN_ORDER_WITH_ID_IN_REVIEW, loanOrder.getId()));
      case CLOSED -> throw new BusinessException(
          HttpStatus.BAD_REQUEST,
          String.format(BusinessException.LOAN_ORDER_WITH_ID_CLOSED, loanOrder.getId()));
    }
  }

  private LoanOrderEntity getLoanOrder(CollectLoanRequestDto dto, String userId) {
    return loanOrderRepository
        .findByIdAndUserId(dto.loanOrderId(), userId)
        .orElseThrow(
            () ->
                new BusinessException(
                    HttpStatus.NOT_FOUND,
                    String.format(
                        BusinessException.LOAN_ORDER_NOT_FOUND_BY_ID, dto.loanOrderId())));
  }

  private BigDecimal calculatePenalty(LoanEntity loanEntity) {
    if (loanEntity.getStatus() != LoanStatus.DELINQUENT) {
      return BigDecimal.ZERO;
    }
    long paymentsMade = loanPaymentRepository.countByLoan(loanEntity);
    return LoanUtils.calculatePenalty(
        loanEntity, timeProvider.getCurrentZonedDateTime(), paymentsMade);
  }
}

package com.andersenlab.etalon.depositservice.service.business.impl;

import static com.andersenlab.etalon.depositservice.util.DepositUtils.calculateMonthlyIncome;

import com.andersenlab.etalon.depositservice.config.TimeProvider;
import com.andersenlab.etalon.depositservice.dto.account.response.AccountDetailedResponseDto;
import com.andersenlab.etalon.depositservice.dto.auth.request.CreateConfirmationRequestDto;
import com.andersenlab.etalon.depositservice.dto.auth.response.CreateConfirmationResponseDto;
import com.andersenlab.etalon.depositservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.depositservice.dto.deposit.request.DepositReplenishRequestDto;
import com.andersenlab.etalon.depositservice.dto.deposit.request.DepositUpdateRequestDto;
import com.andersenlab.etalon.depositservice.dto.deposit.request.DepositWithdrawRequestDto;
import com.andersenlab.etalon.depositservice.dto.deposit.request.OpenDepositRequestDto;
import com.andersenlab.etalon.depositservice.dto.deposit.response.ConfirmationOpenDepositResponseDto;
import com.andersenlab.etalon.depositservice.entity.DepositEntity;
import com.andersenlab.etalon.depositservice.entity.DepositInterestEntity;
import com.andersenlab.etalon.depositservice.entity.DepositOrderEntity;
import com.andersenlab.etalon.depositservice.entity.DepositProductEntity;
import com.andersenlab.etalon.depositservice.service.business.DepositManagementService;
import com.andersenlab.etalon.depositservice.service.business.ValidationService;
import com.andersenlab.etalon.depositservice.service.facade.DepositDaoFacade;
import com.andersenlab.etalon.depositservice.service.facade.ExternalServiceFacade;
import com.andersenlab.etalon.depositservice.util.DepositConstants;
import com.andersenlab.etalon.depositservice.util.DepositUtils;
import com.andersenlab.etalon.depositservice.util.enums.ConfirmationMethod;
import com.andersenlab.etalon.depositservice.util.enums.DepositStatus;
import com.andersenlab.etalon.depositservice.util.enums.Details;
import com.andersenlab.etalon.depositservice.util.enums.Operation;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DepositManagementServiceImpl implements DepositManagementService {
  private final DepositDaoFacade depositDaoFacade;
  private final ExternalServiceFacade externalServiceFacade;
  private final ValidationService validationService;
  private final TimeProvider timeProvider;

  @Override
  @Transactional
  public MessageResponseDto replenishDeposit(
      Long depositId, DepositReplenishRequestDto depositReplenishRequestDto, String userId) {
    DepositEntity entity = replenishDepositBalance(depositId, depositReplenishRequestDto, userId);
    depositDaoFacade.saveDeposit(entity);
    return new MessageResponseDto(MessageResponseDto.DEPOSIT_REPLENISH_SUCCESSFUL);
  }

  @Transactional
  @Override
  public MessageResponseDto withdrawDeposit(
      Long depositId, DepositWithdrawRequestDto depositWithdrawRequestDto, String userId) {
    DepositEntity entity = withdrawDepositBalance(depositId, depositWithdrawRequestDto, userId);
    depositDaoFacade.saveDeposit(entity);
    if (entity.getStatus().equals(DepositStatus.CLOSED)) {
      return new MessageResponseDto(MessageResponseDto.DEPOSIT_CLOSED_SUCCESSFULLY);
    } else {
      return new MessageResponseDto(MessageResponseDto.DEPOSIT_WITHDRAWAL_SUCCESSFUL);
    }
  }

  @Override
  @Transactional
  public ConfirmationOpenDepositResponseDto openNewDeposit(
      final String userId, final OpenDepositRequestDto dto) {
    DepositProductEntity product = depositDaoFacade.findDepositProductById(dto.depositProductId());
    validateDepositData(userId, dto, product);

    DepositOrderEntity createdDepositOrder = createDepositOrder(userId, dto, product);
    DepositOrderEntity savedDepositOrder = depositDaoFacade.saveDepositOrder(createdDepositOrder);

    CreateConfirmationResponseDto confirmationResponseDto =
        externalServiceFacade.createConfirmation(
            new CreateConfirmationRequestDto(
                savedDepositOrder.getId(), Operation.OPEN_DEPOSIT, ConfirmationMethod.EMAIL));

    return new ConfirmationOpenDepositResponseDto(confirmationResponseDto.confirmationId());
  }

  @Override
  public MessageResponseDto updateDeposit(
      Long depositId, DepositUpdateRequestDto requestDto, String userId) {
    verifyAccountsBelongsToUserAndIsNotBlocked(requestDto, userId);
    DepositEntity depositEntity = depositDaoFacade.findDepositByIdAndUserId(depositId, userId);
    validationService.verifyNoDuplicateAccounts(depositEntity, requestDto);

    updateAccountNumber(
        requestDto.interestAccountNumber(), depositEntity::setInterestAccountNumber);
    updateAccountNumber(
        requestDto.finalTransferAccountNumber(), depositEntity::setFinalTransferAccountNumber);

    depositDaoFacade.saveDeposit(depositEntity);
    return new MessageResponseDto(
        MessageResponseDto.DEPOSIT_PATCH_IS_SUCCESSFUL.formatted(depositId));
  }

  private void verifyAccountsBelongsToUserAndIsNotBlocked(
      DepositUpdateRequestDto depositUpdateRequestDto, String userId) {
    String interestAccountNumber = depositUpdateRequestDto.interestAccountNumber();
    String finalTransferAccountNumber = depositUpdateRequestDto.finalTransferAccountNumber();

    if (StringUtils.isNotEmpty(interestAccountNumber)) {
      validationService.verifyAccountBelongsToUserAndIsNotBlocked(userId, interestAccountNumber);
    }
    if (StringUtils.isNotEmpty(finalTransferAccountNumber)) {
      validationService.verifyAccountBelongsToUserAndIsNotBlocked(
          userId, finalTransferAccountNumber);
    }
  }

  private void updateAccountNumber(String accountNumber, Consumer<String> setter) {
    if (StringUtils.isNotEmpty(accountNumber)) {
      setter.accept(accountNumber);
    }
  }

  private DepositEntity replenishDepositBalance(
      Long depositId, DepositReplenishRequestDto depositReplenishRequestDto, String userId) {
    DepositEntity depositEntity = depositDaoFacade.findDepositByIdAndUserId(depositId, userId);
    AccountDetailedResponseDto withdrawalAccount =
        externalServiceFacade.getDetailedAccountInfo(
            depositReplenishRequestDto.withdrawalAccountNumber());
    BigDecimal replenishAmount = depositReplenishRequestDto.replenishAmount();
    String depositAccountNumber = depositEntity.getAccountNumber();

    validationService.validateUserAccount(userId, withdrawalAccount, depositAccountNumber);
    validationService.validateDepositEntityStatusAndMinimalOpenAmount(
        depositEntity, replenishAmount);
    validateDepositReplenishment(depositEntity, replenishAmount, withdrawalAccount);

    externalServiceFacade.createTransactionForDeposit(
        depositAccountNumber,
        replenishAmount,
        withdrawalAccount.iban(),
        String.format(
            DepositConstants.REPLENISH_DEPOSIT_TRANSACTION_TEXT,
            depositEntity.getProduct().getName()),
        Details.REPLENISH_DEPOSIT);

    depositEntity.setStatus(DepositStatus.ACTIVE);
    return depositEntity;
  }

  private void validateDepositReplenishment(
      DepositEntity depositEntity,
      BigDecimal replenishAmount,
      AccountDetailedResponseDto withdrawalAccount) {
    validationService.validateDepositEntityStatusAndMinimalOpenAmount(
        depositEntity, replenishAmount);
    BigDecimal depositBalance =
        externalServiceFacade
            .getAccountBalanceByAccountNumber(depositEntity.getAccountNumber())
            .accountBalance();
    BigDecimal maxDepositReplenishAmount = depositEntity.getProduct().getMaxDepositAmount();
    validationService.validateDepositReplenishAmounts(
        replenishAmount, depositBalance, maxDepositReplenishAmount, withdrawalAccount.balance());
  }

  private DepositEntity withdrawDepositBalance(
      Long depositId, DepositWithdrawRequestDto depositWithdrawRequestDto, String userId) {
    DepositEntity depositEntity = depositDaoFacade.findDepositByIdAndUserId(depositId, userId);
    validationService.validatePossibilityForWithdraw(depositEntity);

    AccountDetailedResponseDto destinationAccount =
        externalServiceFacade.getDetailedAccountInfo(
            depositWithdrawRequestDto.targetAccountNumber());
    validationService.validateUserAccount(
        userId, destinationAccount, depositEntity.getAccountNumber());

    BigDecimal depositBalance =
        validateAndGetDepositBalance(
            depositWithdrawRequestDto, userId, destinationAccount, depositEntity);
    calculateMonthlyIncomeIfExist(depositBalance, depositWithdrawRequestDto, depositEntity);
    createTransactionForDeposit(depositWithdrawRequestDto, depositEntity);
    return depositEntity;
  }

  private void createTransactionForDeposit(
      DepositWithdrawRequestDto depositWithdrawRequestDto, DepositEntity depositEntity) {
    externalServiceFacade.createTransactionForDeposit(
        depositWithdrawRequestDto.targetAccountNumber(),
        depositWithdrawRequestDto.withdrawAmount(),
        depositEntity.getAccountNumber(),
        String.format(
            DepositConstants.WITHDRAW_DEPOSIT_TRANSACTION_TEXT,
            depositEntity.getProduct().getName()),
        Details.WITHDRAW_DEPOSIT);
  }

  private void calculateMonthlyIncomeIfExist(
      BigDecimal depositBalance,
      DepositWithdrawRequestDto depositWithdrawRequestDto,
      DepositEntity depositEntity) {
    if (depositBalance.compareTo(depositWithdrawRequestDto.withdrawAmount()) == 0) {
      depositEntity.setStatus(DepositStatus.CLOSED);

      List<DepositInterestEntity> depositInterestEntityList =
          getDepositInterestEntities(
              List.of(depositEntity), timeProvider.getCurrentZonedDateTime());

      if (!depositInterestEntityList.isEmpty()) {
        calculateMonthlyIncome(List.of(depositEntity), depositInterestEntityList)
            .forEach(externalServiceFacade::createTransaction);
      }
    }
  }

  private BigDecimal validateAndGetDepositBalance(
      DepositWithdrawRequestDto depositWithdrawRequestDto,
      String userId,
      AccountDetailedResponseDto destinationAccount,
      DepositEntity depositEntity) {
    validationService.validateUserAccount(
        userId, destinationAccount, depositEntity.getAccountNumber());

    BigDecimal depositBalance =
        externalServiceFacade
            .getAccountBalanceByAccountNumber(depositEntity.getAccountNumber())
            .accountBalance();

    BigDecimal maxDepositWithdrawAmount =
        depositBalance.subtract(depositEntity.getProduct().getMinOpenAmount());

    validationService.validateDepositWithdrawAmounts(
        depositWithdrawRequestDto.withdrawAmount(), depositBalance, maxDepositWithdrawAmount);
    return depositBalance;
  }

  private List<DepositInterestEntity> getDepositInterestEntities(
      List<DepositEntity> depositsToPay, ZonedDateTime now) {
    return depositDaoFacade.findAllInterestsByDepositIdInAndCreateAtGreaterThanEqual(
        depositsToPay.stream().map(DepositEntity::getId).toList(),
        DepositUtils.subtractMonth(now.truncatedTo(ChronoUnit.DAYS)));
  }

  private void validateDepositData(
      String userId, OpenDepositRequestDto dto, DepositProductEntity product) {
    validationService.validateAmount(dto.depositAmount());
    validationService.checkIsValueInInterval(
        product.getMinOpenAmount(), product.getMaxDepositAmount(), dto.depositAmount());
    validationService.checkIsValueInInterval(
        product.getMinDepositPeriod(),
        product.getMaxDepositPeriod(),
        BigDecimal.valueOf(dto.depositPeriod()));
    validationService.verifyAccountBelongsToUserAndIsNotBlocked(userId, dto.sourceAccount());
    validationService.verifyAccountBelongsToUserAndIsNotBlocked(userId, dto.interestAccount());
    validationService.verifyAccountBelongsToUserAndIsNotBlocked(userId, dto.finalTransferAccount());
    validationService.checkAccountForThisAmount(dto.sourceAccount(), dto.depositAmount());
  }

  private DepositOrderEntity createDepositOrder(
      String userId, OpenDepositRequestDto dto, DepositProductEntity depositProduct) {
    return DepositOrderEntity.builder()
        .userId(userId)
        .amount(dto.depositAmount())
        .depositPeriod(dto.depositPeriod())
        .sourceAccount(dto.sourceAccount())
        .interestAccount(dto.interestAccount())
        .finalTransferAccount(dto.finalTransferAccount())
        .status(DepositStatus.CREATED)
        .product(depositProduct)
        .build();
  }
}

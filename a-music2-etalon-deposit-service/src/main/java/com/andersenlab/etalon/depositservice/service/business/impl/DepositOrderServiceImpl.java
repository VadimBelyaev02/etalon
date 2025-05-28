package com.andersenlab.etalon.depositservice.service.business.impl;

import static com.andersenlab.etalon.depositservice.util.DepositConstants.OPEN_NEW_DEPOSIT_TRANSACTION;

import com.andersenlab.etalon.depositservice.dto.account.request.AccountCreationRequestDto;
import com.andersenlab.etalon.depositservice.dto.account.response.AccountResponseDto;
import com.andersenlab.etalon.depositservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.depositservice.dto.transaction.request.TransactionCreateRequestDto;
import com.andersenlab.etalon.depositservice.dto.transaction.response.TransactionInfoResponseDto;
import com.andersenlab.etalon.depositservice.dto.transaction.response.TransactionMessageResponseDto;
import com.andersenlab.etalon.depositservice.entity.DepositEntity;
import com.andersenlab.etalon.depositservice.entity.DepositOrderEntity;
import com.andersenlab.etalon.depositservice.service.business.DepositOrderService;
import com.andersenlab.etalon.depositservice.service.dao.DepositOrderServiceDao;
import com.andersenlab.etalon.depositservice.service.dao.DepositServiceDao;
import com.andersenlab.etalon.depositservice.service.facade.ExternalServiceFacade;
import com.andersenlab.etalon.depositservice.util.Constants;
import com.andersenlab.etalon.depositservice.util.enums.AccountType;
import com.andersenlab.etalon.depositservice.util.enums.DepositStatus;
import com.andersenlab.etalon.depositservice.util.enums.Details;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DepositOrderServiceImpl implements DepositOrderService {

  private final ExternalServiceFacade externalServiceFacade;
  private final DepositOrderServiceDao depositOrderServiceDao;
  private final DepositServiceDao depositServiceDao;

  @Override
  @Transactional
  public MessageResponseDto processOpeningNewDeposit(Long depositOrderId) {
    DepositOrderEntity depositOrder = depositOrderServiceDao.findById(depositOrderId);
    depositOrder.setStatus(DepositStatus.CONFIRMED);
    depositOrderServiceDao.save(depositOrder);
    AccountResponseDto newAccountDto = createAccount(depositOrder.getUserId());
    TransactionMessageResponseDto transactionMessageResponseDto =
        externalServiceFacade.createTransaction(
            TransactionCreateRequestDto.builder()
                .accountNumberWithdrawn(depositOrder.getSourceAccount())
                .accountNumberReplenished(newAccountDto.iban())
                .amount(depositOrder.getAmount())
                .details(Details.OPEN_DEPOSIT)
                .transactionName(
                    String.format(
                        OPEN_NEW_DEPOSIT_TRANSACTION, depositOrder.getProduct().getName()))
                .feeAmount(BigDecimal.ZERO)
                .isFeeProvided(false)
                .build());
    depositOrder.setTransactionId(transactionMessageResponseDto.transactionId());
    depositOrder.setStatus(DepositStatus.IN_PROGRESS);
    depositOrderServiceDao.save(depositOrder);
    return new MessageResponseDto(MessageResponseDto.DEPOSIT_IN_PROGRESS);
  }

  @Override
  @Transactional
  public void proceedOpeningNewDeposit(Long transactionId) {
    DepositOrderEntity depositOrder =
        depositOrderServiceDao.findDepositOrderEntityByTransactionId(transactionId);
    TransactionInfoResponseDto transactionInfoResponseDto =
        externalServiceFacade.getDetailedTransaction(transactionId);

    if (transactionInfoResponseDto.status().equals(Constants.APPROVED)) {
      DepositEntity deposit =
          DepositEntity.builder()
              .duration(depositOrder.getDepositPeriod())
              .accountNumber(transactionInfoResponseDto.incomeAccountNumber())
              .status(DepositStatus.ACTIVE)
              .userId(depositOrder.getUserId())
              .product(depositOrder.getProduct())
              .interestAccountNumber(depositOrder.getInterestAccount())
              .finalTransferAccountNumber(depositOrder.getFinalTransferAccount())
              .build();
      depositServiceDao.save(deposit);
      depositOrder.setStatus(DepositStatus.CLOSED);

      log.info("{proceedOpeningNewDeposit} -> Deposit has been created successfully");
    } else if (transactionInfoResponseDto.status().equals(Constants.DECLINED)) {
      externalServiceFacade.deleteAccount(transactionInfoResponseDto.incomeAccountNumber());
      depositOrder.setStatus(DepositStatus.REJECTED);
      log.info("{proceedOpeningNewDeposit} -> Deposit was rejected");
    } else
      log.error(
          "{proceedOpeningNewDeposit} -> Incorrect transaction status: {}",
          transactionInfoResponseDto.status());
  }

  private AccountResponseDto createAccount(String userId) {
    AccountCreationRequestDto accountCreation =
        new AccountCreationRequestDto(userId, String.valueOf(AccountType.DEPOSIT));
    return externalServiceFacade.createAccount(accountCreation);
  }
}

package com.andersenlab.etalon.transactionservice.service.impl;

import com.andersenlab.etalon.transactionservice.client.CardServiceClient;
import com.andersenlab.etalon.transactionservice.client.InfoServiceClient;
import com.andersenlab.etalon.transactionservice.client.UserServiceClient;
import com.andersenlab.etalon.transactionservice.dto.account.request.AccountRequestDto;
import com.andersenlab.etalon.transactionservice.dto.account.response.AccountDetailedResponseDto;
import com.andersenlab.etalon.transactionservice.dto.account.response.AccountNumberResponseDto;
import com.andersenlab.etalon.transactionservice.dto.account.response.UserDataResponseDto;
import com.andersenlab.etalon.transactionservice.dto.auth.request.CreateConfirmationRequestDto;
import com.andersenlab.etalon.transactionservice.dto.auth.response.CreateConfirmationResponseDto;
import com.andersenlab.etalon.transactionservice.dto.common.FeeDto;
import com.andersenlab.etalon.transactionservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.transactionservice.dto.info.response.BankInfoResponseDto;
import com.andersenlab.etalon.transactionservice.dto.info.response.ExchangeRateResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.request.CreateTransferRequestDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.request.TransactionCreateRequestDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.request.TransferRequestDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.CreateNewTransferResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.CreateTransferResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.TransactionDetailedResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.TransactionMessageResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.TransferResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.TransferTypeResponseDto;
import com.andersenlab.etalon.transactionservice.entity.TemplateEntity;
import com.andersenlab.etalon.transactionservice.entity.TransferEntity;
import com.andersenlab.etalon.transactionservice.entity.TransferTypeEntity;
import com.andersenlab.etalon.transactionservice.exception.BusinessException;
import com.andersenlab.etalon.transactionservice.interceptor.AuthenticationHolder;
import com.andersenlab.etalon.transactionservice.mapper.ResponseMapper;
import com.andersenlab.etalon.transactionservice.mapper.TemplateMapper;
import com.andersenlab.etalon.transactionservice.mapper.TransferMapper;
import com.andersenlab.etalon.transactionservice.mapper.TransferTypeMapper;
import com.andersenlab.etalon.transactionservice.repository.TemplateRepository;
import com.andersenlab.etalon.transactionservice.repository.TransferRepository;
import com.andersenlab.etalon.transactionservice.repository.TransferTypeRepository;
import com.andersenlab.etalon.transactionservice.service.AccountService;
import com.andersenlab.etalon.transactionservice.service.FeeService;
import com.andersenlab.etalon.transactionservice.service.TransactionService;
import com.andersenlab.etalon.transactionservice.service.TransferService;
import com.andersenlab.etalon.transactionservice.service.ValidationService;
import com.andersenlab.etalon.transactionservice.service.strategies.conversion.impl.CurrencyConverter;
import com.andersenlab.etalon.transactionservice.util.Constants;
import com.andersenlab.etalon.transactionservice.util.async.AsyncOperations;
import com.andersenlab.etalon.transactionservice.util.enums.ConfirmationMethod;
import com.andersenlab.etalon.transactionservice.util.enums.Details;
import com.andersenlab.etalon.transactionservice.util.enums.Operation;
import com.andersenlab.etalon.transactionservice.util.enums.TemplateType;
import com.andersenlab.etalon.transactionservice.util.enums.TransactionStatus;
import com.andersenlab.etalon.transactionservice.util.enums.TransferStatus;
import com.andersenlab.etalon.transactionservice.util.enums.TransferType;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferServiceImpl implements TransferService {
  private final ValidationService validationService;
  private final TransactionService transactionService;
  private final TransferMapper transferMapper;
  private final TransferTypeMapper transferTypeMapper;
  private final TransferRepository transferRepository;
  private final TransferTypeRepository transferTypeRepository;
  private final AccountService accountService;
  private final FeeService feeService;
  private final ResponseMapper responseMapper;
  private final CardServiceClient cardServiceClient;
  private final InfoServiceClient infoServiceClient;
  private final TemplateMapper templateMapper;
  private final TemplateRepository templateRepository;
  private final UserServiceClient userServiceClient;
  private final AsyncOperations asyncOperations;
  private final AuthenticationHolder authenticationHolder;
  private final CurrencyConverter currencyConverter;

  public static final String TRANSFER_TO_ACCOUNT = "Transfer to account";
  public static final String TRANSFER_FROM_ACCOUNT = "Transfer from account";

  private static final Map<TransferStatus, String> TRANSFER_RESPONSE_DTO_STATUS_EXCEPTION_MAP =
      Map.of(
          TransferStatus.DECLINED, BusinessException.TRANSFER_IS_DECLINED,
          TransferStatus.PROCESSING, BusinessException.TRANSFER_IS_PROCESSING,
          TransferStatus.CREATED, BusinessException.TRANSFER_IS_CREATED);

  private final Map<TransferStatus, Predicate<TransferStatus>> validTransitions =
      Map.of(
          TransferStatus.CREATED,
              newStatus ->
                  newStatus == TransferStatus.USER_CONFIRMED
                      || newStatus == TransferStatus.DECLINED,
          TransferStatus.USER_CONFIRMED,
              newStatus ->
                  newStatus == TransferStatus.CODE_CONFIRMED
                      || newStatus == TransferStatus.DECLINED,
          TransferStatus.CODE_CONFIRMED,
              newStatus ->
                  newStatus == TransferStatus.PROCESSING || newStatus == TransferStatus.DECLINED,
          TransferStatus.PROCESSING,
              newStatus ->
                  newStatus == TransferStatus.APPROVED || newStatus == TransferStatus.DECLINED);

  @Override
  public CreateNewTransferResponseDto createTransfer(
      CreateTransferRequestDto transferRequestDto, String userId, boolean isTransient) {
    List<String> userAccountNumbers = accountService.getUserAccountNumbers(userId);
    validateTransferData(transferRequestDto, userAccountNumbers);
    CreateNewTransferResponseDto calculatedTransfer = calculateTransfer(transferRequestDto, userId);
    if (isTransient) {
      return calculatedTransfer;
    }
    TransferEntity transfer =
        transferMapper.toTransferEntity(
            userId, calculatedTransfer, TransferStatus.CREATED, transferRequestDto.description());
    transfer.setIsFeeProvided(
        areAccountsDifferentUser(transfer.getDestination(), userAccountNumbers));
    TransferEntity savedTransfer = transferRepository.save(transfer);
    log.info(
        "{createTransfer}-> Transfer with id-{} to be changed on status -> {}",
        savedTransfer.getId(),
        savedTransfer.getStatus());

    return responseMapper.toResponseFromEntity(savedTransfer);
  }

  private CreateNewTransferResponseDto calculateTransfer(
      CreateTransferRequestDto transferRequestDto, String userId) {
    checkIfSenderEqualsBeneficiary(transferRequestDto);

    var beneficiaryAccountFuture =
        asyncOperations.runAsync(
            () -> accountService.getDetailedAccountInfo(transferRequestDto.beneficiary()));
    var senderAccountFuture =
        asyncOperations.runAsync(
            () -> accountService.getDetailedAccountInfo(transferRequestDto.sender()));
    var userAccountNumbersFuture =
        asyncOperations.runAsync(() -> accountService.getUserAccountNumbers(userId));
    var exchangeRatesFuture =
        asyncOperations.runAsync(
            () -> infoServiceClient.getExchangeRates(Constants.GET_DEFAULT_CURRENCY_QUERY_PARAMS));

    AccountDetailedResponseDto beneficiaryAccount = beneficiaryAccountFuture.join();
    AccountDetailedResponseDto senderAccount = senderAccountFuture.join();
    List<String> userAccountNumbers = userAccountNumbersFuture.join();
    List<ExchangeRateResponseDto> exchangeRates = exchangeRatesFuture.join();

    BigDecimal standardRate =
        currencyConverter.calculateStandardRate(
            senderAccount.currency(), beneficiaryAccount.currency(), exchangeRates);

    var bankInfoFuture =
        asyncOperations.runAsync(() -> infoServiceClient.getBankInfo(beneficiaryAccount.iban()));
    BankInfoResponseDto bankInfo = bankInfoFuture.join();

    FeeDto fee =
        feeService.calculateTransferFee(
            transferRequestDto.amount(),
            bankInfo.isForeignBank(),
            areAccountsDifferentUser(transferRequestDto.beneficiary(), userAccountNumbers));

    BigDecimal totalAmount = transferRequestDto.amount().add(fee.amount());

    return CreateNewTransferResponseDto.builder()
        .sender(transferRequestDto.sender())
        .beneficiary(transferRequestDto.beneficiary())
        .amount(transferRequestDto.amount())
        .fee(fee.amount())
        .feeRate(fee.rate())
        .standardRate(standardRate)
        .totalAmount(totalAmount)
        .description(transferRequestDto.description())
        .build();
  }

  public TransferTypeEntity getTransferTypeById(Long transferTypeId) {
    return transferTypeRepository
        .findById(transferTypeId)
        .orElseThrow(
            () ->
                new BusinessException(
                    HttpStatus.NOT_FOUND,
                    String.format(
                        BusinessException.TRANSFER_TYPE_NOT_FOUND_BY_ID, transferTypeId)));
  }

  public TransferRequestDto mapCardNumberToAccountNumber(TransferRequestDto transferRequestDto) {
    AccountRequestDto sourceAccount =
        cardServiceClient.getUserCardByNumber(transferRequestDto.source());
    AccountRequestDto destinationAccount =
        cardServiceClient.getUserCardByNumber(transferRequestDto.destination());
    if (sourceAccount.isBlocked() || destinationAccount.isBlocked()) {
      throw new BusinessException(HttpStatus.BAD_REQUEST, BusinessException.CARD_IS_BLOCKED);
    }
    if (sourceAccount.status().equals("EXPIRED") || destinationAccount.status().equals("EXPIRED")) {
      throw new BusinessException(HttpStatus.BAD_REQUEST, BusinessException.CARD_IS_EXPIRED);
    }
    return transferRequestDto.toBuilder()
        .source(sourceAccount.accountNumber())
        .destination(destinationAccount.accountNumber())
        .build();
  }

  private boolean areAccountsSameUser(String destination, List<String> userAccountNumbers) {
    return new HashSet<>(userAccountNumbers).contains(destination);
  }

  private boolean areAccountsDifferentUser(String destination, List<String> userAccountNumbers) {
    return !areAccountsSameUser(destination, userAccountNumbers);
  }

  @Override
  public MessageResponseDto processConfirmedTransfer(Long transferId) {
    TransferEntity transfer = getTransferById(transferId);

    validationService.validateTransferStatus(transfer.getStatus());

    AccountDetailedResponseDto accountWithdrawn =
        accountService.getDetailedAccountInfo(transfer.getSource());

    AccountDetailedResponseDto accountReplenished =
        accountService.getDetailedAccountInfo(transfer.getDestination());

    validationService.validateAccountBalance(transfer.getAmount(), accountWithdrawn.iban());
    validationService.validateCardLimits(accountWithdrawn.iban(), transfer.getAmount());

    updateTransferStatus(transferId, TransferStatus.PROCESSING);

    TransactionMessageResponseDto transactionMessageResponseDto =
        transactionService.createTransaction(
            TransactionCreateRequestDto.builder()
                .amount(transfer.getAmount())
                .currency(accountWithdrawn.currency())
                .details(Details.TRANSFER)
                .accountNumberReplenished(accountReplenished.iban())
                .accountNumberWithdrawn(accountWithdrawn.iban())
                .isFeeProvided(transfer.getIsFeeProvided())
                .feeAmount(transfer.getFee())
                .standardRate(transfer.getStandardRate())
                .transactionName(
                    buildTransactionName(transfer.getUserId(), accountWithdrawn.userId()))
                .build());

    if (Objects.nonNull(transactionMessageResponseDto)
        && transactionMessageResponseDto.status().equals(TransactionStatus.CREATED.name())) {
      transfer.setTransactionId(transactionMessageResponseDto.transactionId());
      if (Boolean.TRUE.equals(transfer.getIsTemplate())) {
        saveTemplate(transfer);
      }
      transferRepository.save(transfer);
      return new MessageResponseDto(MessageResponseDto.OPERATION_IS_PROCESSING);
    } else {
      return new MessageResponseDto(MessageResponseDto.OPERATION_IS_FAILED);
    }
  }

  @Override
  public void deleteTransfer(long transferId) {
    String userId = authenticationHolder.getUserId();
    TransferEntity transfer = getTransferById(transferId);
    if (!transfer.getUserId().equals(userId)) {
      throw new BusinessException(
          HttpStatus.FORBIDDEN, BusinessException.THIS_TRANSFER_DOES_NOT_BELONG_TO_THIS_USER);
    }
    if (!transfer.getStatus().equals(TransferStatus.PROCESSING)) {
      throw new BusinessException(
          HttpStatus.CONFLICT,
          BusinessException.NOT_ALLOW_TO_DELETE_TRANSFER_WHICH_IS_APPROVED_AND_PROCESSING);
    }
    transferRepository.deleteById(transferId);
  }

  private void saveTemplate(TransferEntity transfer) {
    TemplateEntity templateEntity = templateMapper.transferToTemplate(transfer);
    templateEntity.setTemplateType(TemplateType.TRANSFER);
    templateRepository.save(templateEntity);
  }

  private String buildTransactionName(String transferUserId, String accountWithdrawnUserId) {
    return new StringJoiner(StringUtils.SPACE)
        .add(
            transferUserId.equals(accountWithdrawnUserId)
                ? TRANSFER_FROM_ACCOUNT
                : TRANSFER_TO_ACCOUNT)
        .toString();
  }

  private void validateTransferData(
      CreateTransferRequestDto transferRequestDto, List<String> userAccountNumbers) {
    validationService.validateAccount(
        accountService.getDetailedAccountInfo(transferRequestDto.sender()));
    validationService.validateAccount(
        accountService.getDetailedAccountInfo(transferRequestDto.beneficiary()));
    validationService.validateAmountMoreThanOne(transferRequestDto.amount());
    validationService.validateOwnership(
        List.of(transferRequestDto.sender()),
        userAccountNumbers,
        new BusinessException(
            HttpStatus.BAD_REQUEST, BusinessException.OPERATION_REJECTED_DUE_TO_SECURITY));
  }

  @Override
  public List<TransferTypeResponseDto> getAllTransferTypes(String userId) {
    return transferTypeMapper.toListDto(getAllTransferTypesEntities(userId));
  }

  @Override
  public TransferResponseDto getTransferByIdAndUserId(long transferId, String userId) {
    TransferEntity transferEntity = getTransferById(transferId);

    validateTransferStatus(transferId, transferEntity);

    validationService.validateOwnership(
        accountService.getUserAccountNumbers(userId),
        accountService.getUserAccountNumbers(transferEntity.getUserId()),
        new BusinessException(
            HttpStatus.BAD_REQUEST, BusinessException.OPERATION_REJECTED_DUE_TO_SECURITY));

    TransactionDetailedResponseDto transactionDetailedResponseDto =
        transactionService.getDetailedTransaction(userId, transferEntity.getTransactionId());

    AccountDetailedResponseDto beneficiaryAccount =
        accountService.getDetailedAccountInfo(transferEntity.getDestination());
    UserDataResponseDto beneficiaryUser =
        userServiceClient.getUserData(beneficiaryAccount.userId());

    AccountDetailedResponseDto senderAccount =
        accountService.getDetailedAccountInfo(transferEntity.getSource());
    UserDataResponseDto senderUser = userServiceClient.getUserData(senderAccount.userId());

    BankInfoResponseDto bankInfoResponseDto =
        infoServiceClient.getBankInfo(beneficiaryAccount.iban());

    TransferTypeResponseDto transferTypeResponseDto =
        Optional.ofNullable(transferEntity.getTransferTypeId())
            .map(this::getTransferTypeById)
            .map(transferTypeMapper::toDto)
            .orElse(null);

    return transferMapper.toTransferResponseDto(
        transferEntity,
        transferTypeResponseDto,
        transactionDetailedResponseDto,
        senderUser,
        beneficiaryUser,
        bankInfoResponseDto);
  }

  private void validateTransferStatus(long transferId, TransferEntity transferEntity) {
    String exceptionMessage =
        TRANSFER_RESPONSE_DTO_STATUS_EXCEPTION_MAP.get(transferEntity.getStatus());
    if (Objects.nonNull(exceptionMessage)) {
      throw new BusinessException(HttpStatus.BAD_REQUEST, exceptionMessage.formatted(transferId));
    }
  }

  public TransferEntity getTransferById(long transferId) {
    return transferRepository
        .findById(transferId)
        .orElseThrow(
            () ->
                new BusinessException(
                    HttpStatus.NOT_FOUND,
                    BusinessException.TRANSFER_NOT_FOUND_BY_ID.formatted(transferId)));
  }

  @Override
  public CreateTransferResponseDto createConfirmation(long transferId, ConfirmationMethod method) {

    updateTransferStatus(transferId, TransferStatus.USER_CONFIRMED);

    CreateConfirmationResponseDto createConfirmationResponseDto =
        infoServiceClient.createConfirmation(
            new CreateConfirmationRequestDto(transferId, Operation.CREATE_TRANSFER, method));

    return responseMapper.toResponseFromConfirmationCode(createConfirmationResponseDto);
  }

  private List<AccountNumberResponseDto> getAllAccountNumbers(String userId) {
    return accountService.getAllAccountNumbers(userId);
  }

  private List<TransferTypeEntity> getAllTransferTypesEntities(String userId) {
    return getAllAccountNumbers(userId).size() <= 1
        ? transferTypeRepository.findAll().stream()
            .filter(
                transferTypeEntity ->
                    !TransferType.TO_MY_ACCOUNT.equals(transferTypeEntity.getTransferType()))
            .toList()
        : transferTypeRepository.findAll();
  }

  private void checkIfSenderEqualsBeneficiary(CreateTransferRequestDto transferRequestDto) {
    if (transferRequestDto.sender().equals(transferRequestDto.beneficiary())) {
      throw new BusinessException(HttpStatus.BAD_REQUEST, BusinessException.SAME_ACCOUNT_NUMBER);
    }
  }

  @Override
  public void updateTransferStatus(Long transferId, TransferStatus newStatus) {
    TransferEntity transfer = getTransferById(transferId);
    TransferStatus currentStatus = transfer.getStatus();
    if (!isValidStatusTransition(currentStatus, newStatus)) {
      log.warn(
          "Invalid status transition from {} to {} for transfer id-{}",
          currentStatus,
          newStatus,
          transferId);
      return;
    }

    transfer.setStatus(newStatus);
    transferRepository.save(transfer);
    log.info(
        "{updatePaymentStatus}-> Transfer with id-{} status changed from {} to {}",
        transferId,
        currentStatus,
        newStatus);
  }

  public boolean isValidStatusTransition(TransferStatus currentStatus, TransferStatus newStatus) {
    return validTransitions.getOrDefault(currentStatus, status -> false).test(newStatus);
  }
}

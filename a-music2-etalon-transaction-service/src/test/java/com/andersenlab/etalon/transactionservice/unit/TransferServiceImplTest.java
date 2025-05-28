package com.andersenlab.etalon.transactionservice.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.andersenlab.etalon.transactionservice.MockData;
import com.andersenlab.etalon.transactionservice.client.CardServiceClient;
import com.andersenlab.etalon.transactionservice.client.InfoServiceClient;
import com.andersenlab.etalon.transactionservice.dto.account.request.AccountRequestDto;
import com.andersenlab.etalon.transactionservice.dto.account.response.AccountDetailedResponseDto;
import com.andersenlab.etalon.transactionservice.dto.common.FeeDto;
import com.andersenlab.etalon.transactionservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.transactionservice.dto.info.response.BankInfoResponseDto;
import com.andersenlab.etalon.transactionservice.dto.info.response.ExchangeRateResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.request.CreateTransferRequestDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.request.TransactionCreateRequestDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.request.TransferRequestDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.CreateNewTransferResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.TransactionMessageResponseDto;
import com.andersenlab.etalon.transactionservice.entity.TransferEntity;
import com.andersenlab.etalon.transactionservice.entity.TransferTypeEntity;
import com.andersenlab.etalon.transactionservice.exception.BusinessException;
import com.andersenlab.etalon.transactionservice.mapper.ResponseMapper;
import com.andersenlab.etalon.transactionservice.mapper.TemplateMapper;
import com.andersenlab.etalon.transactionservice.mapper.TransferMapper;
import com.andersenlab.etalon.transactionservice.repository.TemplateRepository;
import com.andersenlab.etalon.transactionservice.repository.TransferRepository;
import com.andersenlab.etalon.transactionservice.service.AccountService;
import com.andersenlab.etalon.transactionservice.service.FeeService;
import com.andersenlab.etalon.transactionservice.service.impl.TransactionServiceImpl;
import com.andersenlab.etalon.transactionservice.service.impl.TransferServiceImpl;
import com.andersenlab.etalon.transactionservice.service.impl.ValidationServiceImpl;
import com.andersenlab.etalon.transactionservice.service.strategies.conversion.impl.CurrencyConverter;
import com.andersenlab.etalon.transactionservice.util.async.AsyncOperations;
import com.andersenlab.etalon.transactionservice.util.enums.TransactionStatus;
import com.andersenlab.etalon.transactionservice.util.enums.TransferStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class TransferServiceImplTest {
  private static final String USER_ID = "1";
  private static final Long TRANSFER_ID = 1L;

  private TransferEntity transferEntity;
  private TransferRequestDto transferRequestDto;
  private CreateTransferRequestDto createTransferRequestDto;
  private TransactionMessageResponseDto validTransactionMessageResponseDto;
  private TransactionMessageResponseDto invalidTransactionMessageResponseDto;
  private AccountDetailedResponseDto accountWithdrawnDetailedResponseDto;
  private AccountDetailedResponseDto accountReplenishedDetailedResponseDto;
  private CreateNewTransferResponseDto createNewTransferResponseDto;
  private TransferTypeEntity transferTypeEntity;
  private AccountRequestDto accountRequestDto;
  private BankInfoResponseDto bankInfoResponseDto;
  private List<ExchangeRateResponseDto> exchangeRates;
  private FeeDto feeDto;
  @Mock private TransferRepository transferRepository;
  @Mock private TransactionServiceImpl transactionService;
  @Mock private ValidationServiceImpl validationService;
  @Mock private TemplateRepository templateRepository;
  @Mock private AsyncOperations asyncOperations;
  @Mock private CurrencyConverter currencyConverter;
  @Spy private TransferMapper transferMapper = Mappers.getMapper(TransferMapper.class);
  @Spy private ResponseMapper responseMapper = Mappers.getMapper(ResponseMapper.class);
  @Spy protected TemplateMapper templateMapper = Mappers.getMapper(TemplateMapper.class);
  @Mock private AccountService accountService;
  @Mock private FeeService feeService;
  @Mock private CardServiceClient cardServiceClient;
  @Mock private InfoServiceClient infoServiceClient;
  @InjectMocks private TransferServiceImpl transferService;

  @BeforeEach
  void setUp() {
    transferEntity = MockData.getValidTransferEntity();
    transferRequestDto = MockData.getValidTransferRequestDto();
    createTransferRequestDto = MockData.getValidCreateTransferRequestDto();
    validTransactionMessageResponseDto = MockData.getValidTransactionMessageResponseDto();
    invalidTransactionMessageResponseDto = MockData.getInvalidTransactionMessageResponseDto();
    accountWithdrawnDetailedResponseDto = MockData.getValidAccountWithdrawnDetailedResponseDto();
    accountReplenishedDetailedResponseDto =
        MockData.getValidAccountReplenishedDetailedResponseDto();
    accountRequestDto = MockData.getValidAccountRequestDto();
    createNewTransferResponseDto = MockData.getValidCreateNewTransferResponse();
    bankInfoResponseDto = MockData.getValidBankInfoResponseDto();
    exchangeRates = MockData.getValidExchangeRateResponseDtoList();
    feeDto = MockData.getValidFeeDto();
  }

  @Test
  void whenCreateTransferNotToUserAccount_thenTransferSuccess() {
    // given
    CreateNewTransferResponseDto expectedResponse = createNewTransferResponseDto;
    transferTypeEntity = MockData.getValidTransferToAnotherAccountTypeEntity();
    when(transferRepository.save(any(TransferEntity.class))).thenReturn(transferEntity);
    when(accountService.getDetailedAccountInfo(transferEntity.getSource()))
        .thenReturn(accountWithdrawnDetailedResponseDto);
    when(accountService.getDetailedAccountInfo(transferEntity.getDestination()))
        .thenReturn(accountReplenishedDetailedResponseDto);
    when(asyncOperations.runAsync(any(Supplier.class)))
        .thenReturn(CompletableFuture.completedFuture(accountReplenishedDetailedResponseDto))
        .thenReturn(CompletableFuture.completedFuture(accountWithdrawnDetailedResponseDto))
        .thenReturn(CompletableFuture.completedFuture(MockData.getValidAccountNumbersDtoList()))
        .thenReturn(CompletableFuture.completedFuture(exchangeRates))
        .thenReturn(CompletableFuture.completedFuture(bankInfoResponseDto));
    when(transferMapper.toTransferEntity(
            any(String.class),
            any(CreateNewTransferResponseDto.class),
            any(TransferStatus.class),
            any(String.class)))
        .thenReturn(transferEntity);
    when(feeService.calculateTransferFee(any(BigDecimal.class), anyBoolean(), anyBoolean()))
        .thenReturn(feeDto);
    // when
    CreateNewTransferResponseDto actualResponse =
        transferService.createTransfer(createTransferRequestDto, USER_ID, false);
    verify(transferRepository).save(any(TransferEntity.class));
    verify(accountService).getDetailedAccountInfo(transferEntity.getSource());
    verify(accountService).getDetailedAccountInfo(transferEntity.getDestination());
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void whenCreateTransferV2NotToUserAccount_thenTransferSuccess() {
    // given
    CreateNewTransferResponseDto expectedResponse = createNewTransferResponseDto;
    transferTypeEntity = MockData.getValidTransferToAnotherAccountTypeEntity();

    when(asyncOperations.runAsync(any(Supplier.class)))
        .thenReturn(CompletableFuture.completedFuture(accountReplenishedDetailedResponseDto))
        .thenReturn(CompletableFuture.completedFuture(accountWithdrawnDetailedResponseDto))
        .thenReturn(CompletableFuture.completedFuture(MockData.getValidAccountNumbersDtoList()))
        .thenReturn(
            CompletableFuture.completedFuture(MockData.getValidExchangeRateResponseDtoList()))
        .thenReturn(CompletableFuture.completedFuture(bankInfoResponseDto));
    when(transferMapper.toTransferEntity(
            anyString(),
            any(CreateNewTransferResponseDto.class),
            any(TransferStatus.class),
            anyString()))
        .thenReturn(transferEntity);
    when(feeService.calculateTransferFee(any(BigDecimal.class), anyBoolean(), anyBoolean()))
        .thenReturn(feeDto);
    when(transferRepository.save(any(TransferEntity.class))).thenReturn(transferEntity);

    // when
    CreateNewTransferResponseDto actualResponse =
        transferService.createTransfer(createTransferRequestDto, USER_ID, false);

    // then
    verify(transferMapper)
        .toTransferEntity(
            anyString(),
            any(CreateNewTransferResponseDto.class),
            any(TransferStatus.class),
            anyString());
    verify(transferRepository).save(any(TransferEntity.class));
    verify(accountService, times(1)).getDetailedAccountInfo(transferEntity.getSource());
    verify(accountService, times(1)).getDetailedAccountInfo(transferEntity.getDestination());

    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void whenCreateTransferBetweenUserAccounts_thenTransferInProgress() {
    // given
    MessageResponseDto expectedResponse =
        new MessageResponseDto(MessageResponseDto.OPERATION_IS_PROCESSING);

    when(transferRepository.findById(anyLong())).thenReturn(Optional.ofNullable(transferEntity));
    when(accountService.getDetailedAccountInfo(transferEntity.getSource()))
        .thenReturn(accountWithdrawnDetailedResponseDto);
    when(accountService.getDetailedAccountInfo(transferEntity.getDestination()))
        .thenReturn(accountReplenishedDetailedResponseDto);
    when(transactionService.createTransaction(any(TransactionCreateRequestDto.class)))
        .thenReturn(
            validTransactionMessageResponseDto.toBuilder()
                .status(TransactionStatus.CREATED.name())
                .build());
    // when
    MessageResponseDto actualResponse = transferService.processConfirmedTransfer(TRANSFER_ID);
    // then
    verify(transactionService).createTransaction(any(TransactionCreateRequestDto.class));
    verify(templateRepository).save(any());

    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void whenCreateTransferBetweenSameUserCards_thenTransferSuccess() {
    // given
    transferRequestDto = MockData.getValidTransferToCardRequestDto();
    transferTypeEntity = MockData.getValidTransferToCardTypeEntity();
    List<String> userAccountNumbers = MockData.getValidAccountNumbersDtoList();
    CreateNewTransferResponseDto expectedResponse = createNewTransferResponseDto;
    when(accountService.getUserAccountNumbers(anyString())).thenReturn(userAccountNumbers);
    when(accountService.getDetailedAccountInfo(anyString()))
        .thenReturn(accountWithdrawnDetailedResponseDto);
    when(transferMapper.toTransferEntity(
            anyString(),
            any(CreateNewTransferResponseDto.class),
            any(TransferStatus.class),
            anyString()))
        .thenReturn(transferEntity);
    when(feeService.calculateTransferFee(any(BigDecimal.class), anyBoolean(), anyBoolean()))
        .thenReturn(feeDto);
    when(transferRepository.save(any(TransferEntity.class))).thenReturn(transferEntity);
    when(asyncOperations.runAsync(any(Supplier.class)))
        .thenReturn(CompletableFuture.completedFuture(accountReplenishedDetailedResponseDto))
        .thenReturn(CompletableFuture.completedFuture(accountWithdrawnDetailedResponseDto))
        .thenReturn(
            CompletableFuture.completedFuture(MockData.getValidExchangeRateResponseDtoList()))
        .thenReturn(CompletableFuture.completedFuture(MockData.getValidAccountNumbersDtoList()))
        .thenReturn(CompletableFuture.completedFuture(bankInfoResponseDto));
    // when
    CreateNewTransferResponseDto actualResponse =
        transferService.createTransfer(createTransferRequestDto, USER_ID, false);

    // then
    verify(transferMapper)
        .toTransferEntity(
            anyString(),
            any(CreateNewTransferResponseDto.class),
            any(TransferStatus.class),
            anyString());
    verify(transferRepository, times(1)).save(any(TransferEntity.class));
    verify(accountService, times(1)).getDetailedAccountInfo(accountRequestDto.accountNumber());
    verify(accountService, times(1)).getUserAccountNumbers(anyString());
    verify(validationService, times(2)).validateAccount(any(AccountDetailedResponseDto.class));
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void whenCreateTransferBetweenDifferentUserCards_thenTransferSuccess() {
    // given
    transferRequestDto = MockData.getValidTransferToCardRequestDto();
    transferTypeEntity = MockData.getValidTransferToCardTypeEntity();
    List<String> userAccountNumbers = MockData.getValidAccountNumbersDtoList();
    CreateNewTransferResponseDto expectedResponse = createNewTransferResponseDto;
    when(accountService.getUserAccountNumbers(anyString())).thenReturn(userAccountNumbers);
    when(accountService.getDetailedAccountInfo(anyString()))
        .thenReturn(accountWithdrawnDetailedResponseDto);
    when(transferMapper.toTransferEntity(
            anyString(),
            any(CreateNewTransferResponseDto.class),
            any(TransferStatus.class),
            anyString()))
        .thenReturn(transferEntity);
    when(feeService.calculateTransferFee(any(BigDecimal.class), anyBoolean(), anyBoolean()))
        .thenReturn(feeDto);
    when(transferRepository.save(any(TransferEntity.class))).thenReturn(transferEntity);
    when(asyncOperations.runAsync(any(Supplier.class)))
        .thenReturn(CompletableFuture.completedFuture(accountReplenishedDetailedResponseDto))
        .thenReturn(CompletableFuture.completedFuture(accountWithdrawnDetailedResponseDto))
        .thenReturn(CompletableFuture.completedFuture(MockData.getValidAccountNumbersDtoList()))
        .thenReturn(
            CompletableFuture.completedFuture(MockData.getValidExchangeRateResponseDtoList()))
        .thenReturn(CompletableFuture.completedFuture(bankInfoResponseDto));
    // when
    CreateNewTransferResponseDto actualResponse =
        transferService.createTransfer(createTransferRequestDto, USER_ID, false);

    // then
    verify(transferMapper)
        .toTransferEntity(
            anyString(),
            any(CreateNewTransferResponseDto.class),
            any(TransferStatus.class),
            anyString());
    verify(transferRepository).save(any(TransferEntity.class));
    verify(accountService).getDetailedAccountInfo(accountRequestDto.accountNumber());
    verify(accountService).getUserAccountNumbers(anyString());
    verify(transferRepository).save(any());
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void whenCreateTransferWhenAmountScaleMoreTwo_shouldFail() {
    transferTypeEntity = MockData.getValidTransferToAnotherAccountTypeEntity();
    CreateTransferRequestDto transferRequestWithAmountScaleMoreTwo =
        MockData.getValidCreateTransferRequestDto().toBuilder()
            .amount(BigDecimal.valueOf(10.56789))
            .build();
    doThrow(
            new BusinessException(
                HttpStatus.BAD_REQUEST,
                BusinessException
                    .OPERATION_REJECTED_BECAUSE_AMOUNT_HAS_MORE_THAN_2_DIGITS_AFTER_DOT))
        .when(validationService)
        .validateAmountMoreThanOne(BigDecimal.valueOf(10.56789));
    // then
    assertThrows(
        BusinessException.class,
        () ->
            transferService.createTransfer(transferRequestWithAmountScaleMoreTwo, USER_ID, false));
  }

  @Test
  void whenCreateTransferWhenAmountNull_shouldFail() {
    transferTypeEntity = MockData.getValidTransferToAnotherAccountTypeEntity();
    CreateTransferRequestDto transferRequestWithAmountNull =
        MockData.getValidCreateTransferRequestDto().toBuilder().amount(null).build();
    doThrow(
            new BusinessException(
                HttpStatus.BAD_REQUEST,
                BusinessException.OPERATION_REJECTED_DUE_TO_EMPTY_AMOUNT_FIELD))
        .when(validationService)
        .validateAmountMoreThanOne(null);

    // then
    assertThrows(
        BusinessException.class,
        () -> transferService.createTransfer(transferRequestWithAmountNull, USER_ID, false));
  }

  @Test
  void whenCreateTransferFromNotUsersAccount_shouldFail() {
    transferTypeEntity = MockData.getValidTransferToAnotherAccountTypeEntity();
    doThrow(
            new BusinessException(
                HttpStatus.BAD_REQUEST, BusinessException.OPERATION_REJECTED_DUE_TO_SECURITY))
        .when(validationService)
        .validateOwnership(any(), any(), any());
    // then
    assertThrows(
        BusinessException.class,
        () -> transferService.createTransfer(createTransferRequestDto, USER_ID, false));
  }

  @Test
  void whenProcessCreatingTransfer_thenFailBecauseStatusDeclined() {
    // given
    MessageResponseDto expectedResponse =
        new MessageResponseDto(MessageResponseDto.OPERATION_IS_FAILED);

    when(transferRepository.findById(anyLong())).thenReturn(Optional.ofNullable(transferEntity));
    when(transactionService.createTransaction(any(TransactionCreateRequestDto.class)))
        .thenReturn(invalidTransactionMessageResponseDto);
    when(accountService.getDetailedAccountInfo(transferEntity.getSource()))
        .thenReturn(accountWithdrawnDetailedResponseDto);
    when(accountService.getDetailedAccountInfo(transferEntity.getDestination()))
        .thenReturn(accountReplenishedDetailedResponseDto);
    // when
    MessageResponseDto actualResponse = transferService.processConfirmedTransfer(TRANSFER_ID);
    // then
    verify(transactionService).createTransaction(any(TransactionCreateRequestDto.class));
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void whenProcessCreatingTransfer_thenFailBecauseTransactionResponseNull() {
    // given
    MessageResponseDto expectedResponse =
        new MessageResponseDto(MessageResponseDto.OPERATION_IS_FAILED);

    when(transferRepository.findById(anyLong())).thenReturn(Optional.ofNullable(transferEntity));
    when(transactionService.createTransaction(any(TransactionCreateRequestDto.class)))
        .thenReturn(null);
    when(accountService.getDetailedAccountInfo(transferEntity.getSource()))
        .thenReturn(accountWithdrawnDetailedResponseDto);
    when(accountService.getDetailedAccountInfo(transferEntity.getDestination()))
        .thenReturn(accountReplenishedDetailedResponseDto);
    // when
    MessageResponseDto actualResponse = transferService.processConfirmedTransfer(TRANSFER_ID);
    // then
    verify(transactionService).createTransaction(any(TransactionCreateRequestDto.class));
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void
      whenProcessCreatingTransfer__thenExecuteTransactionAndEventsSaveTemplateReturnMessageResponseDto() {
    // given
    MessageResponseDto expectedResponse =
        new MessageResponseDto(MessageResponseDto.OPERATION_IS_PROCESSING);

    when(transferRepository.findById(anyLong())).thenReturn(Optional.ofNullable(transferEntity));
    when(accountService.getDetailedAccountInfo(transferEntity.getSource()))
        .thenReturn(accountWithdrawnDetailedResponseDto);
    when(accountService.getDetailedAccountInfo(transferEntity.getDestination()))
        .thenReturn(accountReplenishedDetailedResponseDto);
    when(transactionService.createTransaction(any(TransactionCreateRequestDto.class)))
        .thenReturn(
            validTransactionMessageResponseDto.toBuilder()
                .status(TransferStatus.CREATED.name())
                .build());
    // when
    MessageResponseDto actualResponse = transferService.processConfirmedTransfer(TRANSFER_ID);
    // then
    verify(transactionService).createTransaction(any(TransactionCreateRequestDto.class));
    verify(templateRepository).save(any());

    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void whenProcessCreatingTransferV2_thenFailBecauseTransactionResponseNull() {
    // given
    MessageResponseDto expectedResponse =
        new MessageResponseDto(MessageResponseDto.OPERATION_IS_FAILED);

    when(transferRepository.findById(anyLong())).thenReturn(Optional.ofNullable(transferEntity));
    when(transactionService.createTransaction(any(TransactionCreateRequestDto.class)))
        .thenReturn(null);
    when(accountService.getDetailedAccountInfo(transferEntity.getSource()))
        .thenReturn(accountWithdrawnDetailedResponseDto);
    when(accountService.getDetailedAccountInfo(transferEntity.getDestination()))
        .thenReturn(accountReplenishedDetailedResponseDto);
    // when
    MessageResponseDto actualResponse = transferService.processConfirmedTransfer(TRANSFER_ID);
    // then
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void
      whenProcessCreatingTransferV2__thenExecuteTransactionAndEventsSaveTemplateReturnMessageResponseDto() {
    // given
    MessageResponseDto expectedResponse =
        new MessageResponseDto(MessageResponseDto.OPERATION_IS_PROCESSING);

    when(templateRepository.save(any())).thenReturn(MockData.getValidTemplateEntityForTransfers());
    when(transferRepository.findById(anyLong())).thenReturn(Optional.ofNullable(transferEntity));
    when(accountService.getDetailedAccountInfo(transferEntity.getSource()))
        .thenReturn(accountWithdrawnDetailedResponseDto);
    when(accountService.getDetailedAccountInfo(transferEntity.getDestination()))
        .thenReturn(accountReplenishedDetailedResponseDto);
    when(transactionService.createTransaction(any(TransactionCreateRequestDto.class)))
        .thenReturn(validTransactionMessageResponseDto);
    // when
    MessageResponseDto actualResponse = transferService.processConfirmedTransfer(TRANSFER_ID);
    // then
    verify(transactionService).createTransaction(any(TransactionCreateRequestDto.class));
    verify(transferRepository).save(any(TransferEntity.class));
    verify(templateRepository).save(any());

    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void whenProcessCreatingTransferV2_thenIsProcessing() {
    // given
    MessageResponseDto expectedResponse =
        new MessageResponseDto(MessageResponseDto.OPERATION_IS_PROCESSING);

    when(transferRepository.findById(anyLong())).thenReturn(Optional.ofNullable(transferEntity));
    when(transactionService.createTransaction(any(TransactionCreateRequestDto.class)))
        .thenReturn(validTransactionMessageResponseDto);
    when(accountService.getDetailedAccountInfo(transferEntity.getSource()))
        .thenReturn(accountWithdrawnDetailedResponseDto);
    when(accountService.getDetailedAccountInfo(transferEntity.getDestination()))
        .thenReturn(accountReplenishedDetailedResponseDto);

    // when
    MessageResponseDto actualResponse = transferService.processConfirmedTransfer(TRANSFER_ID);

    // then
    verify(transactionService).createTransaction(any(TransactionCreateRequestDto.class));
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void whenMapCardNumbersToAccountNumbersForDifferentUserCards_shouldSuccess() {
    // given
    TransferRequestDto transferToCardRequestDto = MockData.getValidTransferToCardRequestDto();
    AccountRequestDto sourceAccount = new AccountRequestDto("1", false, "ACTIVE");
    AccountRequestDto destinationAccount = new AccountRequestDto("2", false, "ACTIVE");
    TransferRequestDto transferToAccountRequestDto =
        transferToCardRequestDto.toBuilder()
            .source(sourceAccount.accountNumber())
            .destination(destinationAccount.accountNumber())
            .build();
    when(cardServiceClient.getUserCardByNumber(transferToCardRequestDto.source()))
        .thenReturn(sourceAccount);
    when(cardServiceClient.getUserCardByNumber(transferToCardRequestDto.destination()))
        .thenReturn(destinationAccount);
    // when
    TransferRequestDto result =
        transferService.mapCardNumberToAccountNumber(transferToCardRequestDto);
    // then
    assertEquals(result, transferToAccountRequestDto);
    verify(cardServiceClient, times(2)).getUserCardByNumber(anyString());
  }

  @Test
  void whenMapCardNumbersToAccountNumbersForSameUserCards_shouldSuccess() {
    // given
    TransferRequestDto transferToCardRequestDto = MockData.getValidTransferToCardRequestDto();
    AccountRequestDto sourceAccount = new AccountRequestDto("1", false, "ACTIVE");
    AccountRequestDto destinationAccount = new AccountRequestDto("1", false, "ACTIVE");
    TransferRequestDto transferToAccountRequestDto =
        transferToCardRequestDto.toBuilder()
            .source(sourceAccount.accountNumber())
            .destination(destinationAccount.accountNumber())
            .build();
    when(cardServiceClient.getUserCardByNumber(transferToCardRequestDto.source()))
        .thenReturn(sourceAccount);
    when(cardServiceClient.getUserCardByNumber(transferToCardRequestDto.destination()))
        .thenReturn(destinationAccount);
    // when
    TransferRequestDto result =
        transferService.mapCardNumberToAccountNumber(transferToCardRequestDto);
    // then
    assertEquals(result, transferToAccountRequestDto);
    verify(cardServiceClient, times(2)).getUserCardByNumber(anyString());
  }

  @Test
  void whenCreateTransferFromCardToCard_thenFailBecauseAccountIsBlocked() {
    // given
    TransferRequestDto transferToCardRequestDto =
        transferRequestDto.toBuilder().transferTypeId(2L).build();
    AccountRequestDto sourceAccount = new AccountRequestDto("1", true, "BLOCKED");
    AccountRequestDto destinationAccount = new AccountRequestDto("2", false, "ACTIVE");
    when(cardServiceClient.getUserCardByNumber(transferRequestDto.source()))
        .thenReturn(sourceAccount);
    when(cardServiceClient.getUserCardByNumber(transferRequestDto.destination()))
        .thenReturn(destinationAccount);
    // when, then
    assertThrows(
        BusinessException.class,
        () -> transferService.mapCardNumberToAccountNumber(transferToCardRequestDto));
  }

  @Test
  void whenCreateTransferFromCardToCard_thenFailBecauseAccountIsExpired() {
    // given
    TransferRequestDto transferToCardRequestDto =
        transferRequestDto.toBuilder().transferTypeId(2L).build();
    AccountRequestDto sourceAccount = new AccountRequestDto("1", false, "EXPIRED");
    AccountRequestDto destinationAccount = new AccountRequestDto("2", false, "ACTIVE");
    when(cardServiceClient.getUserCardByNumber(transferRequestDto.source()))
        .thenReturn(sourceAccount);
    when(cardServiceClient.getUserCardByNumber(transferRequestDto.destination()))
        .thenReturn(destinationAccount);

    // when, then
    assertThrows(
        BusinessException.class,
        () -> transferService.mapCardNumberToAccountNumber(transferToCardRequestDto));
  }
}

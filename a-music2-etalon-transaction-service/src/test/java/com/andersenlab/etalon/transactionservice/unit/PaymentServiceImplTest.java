package com.andersenlab.etalon.transactionservice.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.andersenlab.etalon.transactionservice.MockData;
import com.andersenlab.etalon.transactionservice.client.InfoServiceClient;
import com.andersenlab.etalon.transactionservice.dto.account.response.AccountDetailedResponseDto;
import com.andersenlab.etalon.transactionservice.dto.auth.request.CreateConfirmationRequestDto;
import com.andersenlab.etalon.transactionservice.dto.auth.response.CreateConfirmationResponseDto;
import com.andersenlab.etalon.transactionservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.request.PaymentRequestDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.request.TransactionCreateRequestDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.CreatePaymentResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.FinePaymentTypeResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.PaymentTypeResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.StatusMessageResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.TaxPaymentTypeResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.TransactionMessageResponseDto;
import com.andersenlab.etalon.transactionservice.entity.PaymentEntity;
import com.andersenlab.etalon.transactionservice.entity.PaymentTypeEntity;
import com.andersenlab.etalon.transactionservice.exception.BusinessException;
import com.andersenlab.etalon.transactionservice.mapper.PaymentMapper;
import com.andersenlab.etalon.transactionservice.mapper.TemplateMapper;
import com.andersenlab.etalon.transactionservice.repository.PaymentRepository;
import com.andersenlab.etalon.transactionservice.repository.PaymentTypeRepository;
import com.andersenlab.etalon.transactionservice.repository.TemplateRepository;
import com.andersenlab.etalon.transactionservice.service.AccountService;
import com.andersenlab.etalon.transactionservice.service.impl.PaymentServiceImpl;
import com.andersenlab.etalon.transactionservice.service.impl.TransactionServiceImpl;
import com.andersenlab.etalon.transactionservice.service.impl.ValidationServiceImpl;
import com.andersenlab.etalon.transactionservice.util.enums.PaymentStatus;
import com.andersenlab.etalon.transactionservice.util.enums.TransactionStatus;
import com.andersenlab.etalon.transactionservice.util.filter.PaymentFilter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {
  private static final String USER_ID = "1L";
  private static final Long PAYMENT_ID = 1L;
  private static final Long CONFIRMATION_ID = 1L;

  private PaymentTypeResponseDto paymentTypeResponseDto;
  private TaxPaymentTypeResponseDto taxPaymentTypeResponseDto;
  private FinePaymentTypeResponseDto finePaymentTypeResponseDto;
  private PaymentTypeEntity paymentType;
  private PaymentEntity paymentEntity;
  private PaymentRequestDto paymentRequestDto;
  private TransactionMessageResponseDto validTransactionMessageResponseDto;
  private TransactionMessageResponseDto invalidTransactionMessageResponseDto;
  private AccountDetailedResponseDto accountWithdrawnDetailedResponseDto;

  private PaymentTypeEntity taxPaymentType;
  private PaymentTypeEntity finePaymentType;
  @Spy protected TemplateMapper templateMapper = Mappers.getMapper(TemplateMapper.class);
  @Mock private PaymentRepository paymentRepository;
  @Mock private TemplateRepository templateRepository;
  @Mock private PaymentTypeRepository paymentTypeRepository;
  @Mock private TransactionServiceImpl transactionService;
  @Mock private ValidationServiceImpl validationService;
  @Spy private PaymentMapper paymentMapper = Mappers.getMapper(PaymentMapper.class);
  @Mock private AccountService accountService;
  @Mock private InfoServiceClient infoServiceClient;
  @InjectMocks private PaymentServiceImpl paymentService;

  @BeforeEach
  void setUp() {
    paymentType = MockData.getValidPaymentTypeEntity();
    taxPaymentType = MockData.getValidTaxPaymentTypeEntity();
    finePaymentType = MockData.getValidFinePaymentTypeEntity();
    paymentTypeResponseDto = MockData.getValidPaymentTypeDto();
    taxPaymentTypeResponseDto = MockData.getValidTaxPaymentTypeDto();
    finePaymentTypeResponseDto = MockData.getValidFinePaymentTypeDto();
    paymentEntity = MockData.getValidPaymentEntity();
    paymentRequestDto = MockData.getValidPaymentRequestDto();
    validTransactionMessageResponseDto = MockData.getValidTransactionMessageResponseDto();
    invalidTransactionMessageResponseDto = MockData.getInvalidTransactionMessageResponseDto();
    accountWithdrawnDetailedResponseDto = MockData.getValidAccountWithdrawnDetailedResponseDto();
  }

  @Test
  void getAllPaymentsTypeTest() {
    when(paymentTypeRepository.findAll(ArgumentMatchers.any(Example.class)))
        .thenReturn(List.of(paymentType, taxPaymentType, finePaymentType));

    Assertions.assertEquals(
        paymentTypeResponseDto, paymentService.getAllPaymentTypes(new PaymentFilter(null)).get(0));
    Assertions.assertEquals(
        taxPaymentTypeResponseDto,
        paymentService.getAllPaymentTypes(new PaymentFilter(null)).get(1));
    Assertions.assertEquals(
        finePaymentTypeResponseDto,
        paymentService.getAllPaymentTypes(new PaymentFilter(null)).get(2));
  }

  @Test
  void whenCreatePayment__thenPaymentSavedAndReturnConfirmationResponse() {
    // given
    CreatePaymentResponseDto expectedResponse = new CreatePaymentResponseDto(1L);
    CreateConfirmationResponseDto createConfirmationResponseDto =
        new CreateConfirmationResponseDto(CONFIRMATION_ID);

    when(paymentMapper.toPaymentEntity(
            ArgumentMatchers.anyString(),
            ArgumentMatchers.any(PaymentRequestDto.class),
            ArgumentMatchers.any(PaymentStatus.class)))
        .thenReturn(paymentEntity);

    when(infoServiceClient.createConfirmation(
            ArgumentMatchers.any(CreateConfirmationRequestDto.class)))
        .thenReturn(createConfirmationResponseDto);
    when(validationService.validateTemplateName(paymentRequestDto.templateName(), USER_ID))
        .thenReturn(
            new StatusMessageResponseDto(true, StatusMessageResponseDto.TEMPLATE_NAME_IS_VALID));
    when(accountService.getUserAccountNumbers(ArgumentMatchers.any()))
        .thenReturn(List.of(accountWithdrawnDetailedResponseDto.iban()));
    when(paymentRepository.findById(ArgumentMatchers.anyLong()))
        .thenReturn(Optional.of(paymentEntity));
    when(paymentRepository.save(ArgumentMatchers.any(PaymentEntity.class)))
        .thenReturn(paymentEntity);
    // when
    CreatePaymentResponseDto actualResponse =
        paymentService.createPayment(paymentRequestDto, USER_ID);
    // then
    verify(paymentMapper)
        .toPaymentEntity(
            ArgumentMatchers.anyString(),
            ArgumentMatchers.any(PaymentRequestDto.class),
            ArgumentMatchers.any(PaymentStatus.class));
    verify(paymentRepository, times(2)).save(ArgumentMatchers.any(PaymentEntity.class));
    verify(validationService)
        .validateTemplateName(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    verify(infoServiceClient)
        .createConfirmation(ArgumentMatchers.any(CreateConfirmationRequestDto.class));

    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void whenCreatePaymentWhenTemplateAlreadySaved_shouldFail() {
    when(validationService.validateTemplateName(paymentRequestDto.templateName(), USER_ID))
        .thenReturn(
            new StatusMessageResponseDto(
                false, BusinessException.TEMPLATE_NAME_ALREADY_BEEN_SAVED));
    // then
    assertThrows(
        BusinessException.class, () -> paymentService.createPayment(paymentRequestDto, USER_ID));
  }

  @Test
  void whenCreatePaymentWhenAmountScaleMoreTwo_shouldFail() {
    PaymentRequestDto paymentRequestWithAmountScaleMoreTwo =
        MockData.createValidPaymentRequestDto().toBuilder()
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
        () -> paymentService.createPayment(paymentRequestWithAmountScaleMoreTwo, USER_ID));
  }

  @Test
  void whenCreatePaymentWhenAmountNull_shouldFail() {
    PaymentRequestDto paymentRequestWithAmountNull =
        MockData.createValidPaymentRequestDto().toBuilder().amount(null).build();
    doThrow(
            new BusinessException(
                HttpStatus.BAD_REQUEST,
                BusinessException.OPERATION_REJECTED_DUE_TO_EMPTY_AMOUNT_FIELD))
        .when(validationService)
        .validateAmountMoreThanOne(ArgumentMatchers.any());

    // then
    assertThrows(
        BusinessException.class,
        () -> paymentService.createPayment(paymentRequestWithAmountNull, USER_ID));
  }

  @Test
  void
      whenProcessCreatingPayment__thenExecuteTransactionAndEventsSaveTemplateReturnMessageResponseDto() {
    // given
    MessageResponseDto expectedResponse =
        new MessageResponseDto(MessageResponseDto.OPERATION_IS_PROCESSING);

    when(templateRepository.save(ArgumentMatchers.any()))
        .thenReturn(MockData.getValidTemplateEntityForPayments());
    when(paymentRepository.findById(ArgumentMatchers.anyLong()))
        .thenReturn(Optional.ofNullable(paymentEntity));
    when(paymentTypeRepository.findAll(ArgumentMatchers.any(Example.class)))
        .thenReturn(List.of(paymentType));
    when(transactionService.createTransaction(
            ArgumentMatchers.any(TransactionCreateRequestDto.class)))
        .thenReturn(
            validTransactionMessageResponseDto.toBuilder()
                .status(TransactionStatus.CREATED.name())
                .build());
    when(accountService.getDetailedAccountInfo(paymentEntity.getAccountNumberWithdrawn()))
        .thenReturn(accountWithdrawnDetailedResponseDto);
    // when
    MessageResponseDto actualResponse = paymentService.processCreatingPayment(PAYMENT_ID);
    // then
    verify(transactionService)
        .createTransaction(ArgumentMatchers.any(TransactionCreateRequestDto.class));
    verify(paymentRepository).save(ArgumentMatchers.any(PaymentEntity.class));
    verify(templateRepository).save(ArgumentMatchers.any());

    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void whenProcessCreatingPayment__thenFailBecauseStatusDeclined() {
    // given
    MessageResponseDto expectedResponse =
        new MessageResponseDto(MessageResponseDto.OPERATION_IS_FAILED);

    when(paymentRepository.findById(ArgumentMatchers.anyLong()))
        .thenReturn(Optional.ofNullable(paymentEntity));
    when(paymentTypeRepository.findAll(ArgumentMatchers.any(Example.class)))
        .thenReturn(List.of(paymentType));
    when(transactionService.createTransaction(
            ArgumentMatchers.any(TransactionCreateRequestDto.class)))
        .thenReturn(invalidTransactionMessageResponseDto);
    when(accountService.getDetailedAccountInfo(paymentEntity.getAccountNumberWithdrawn()))
        .thenReturn(accountWithdrawnDetailedResponseDto);
    // when
    MessageResponseDto actualResponse = paymentService.processCreatingPayment(PAYMENT_ID);
    // then
    verify(transactionService)
        .createTransaction(ArgumentMatchers.any(TransactionCreateRequestDto.class));
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void whenProcessCreatingPayment__thenFailBecauseTransactionResponseNull() {
    // given
    MessageResponseDto expectedResponse =
        new MessageResponseDto(MessageResponseDto.OPERATION_IS_FAILED);

    when(paymentRepository.findById(ArgumentMatchers.anyLong()))
        .thenReturn(Optional.ofNullable(paymentEntity));
    when(paymentTypeRepository.findAll(ArgumentMatchers.any(Example.class)))
        .thenReturn(List.of(paymentType));
    when(transactionService.createTransaction(
            ArgumentMatchers.any(TransactionCreateRequestDto.class)))
        .thenReturn(null);
    when(accountService.getDetailedAccountInfo(paymentEntity.getAccountNumberWithdrawn()))
        .thenReturn(accountWithdrawnDetailedResponseDto);
    // when
    MessageResponseDto actualResponse = paymentService.processCreatingPayment(PAYMENT_ID);
    // then
    verify(transactionService)
        .createTransaction(ArgumentMatchers.any(TransactionCreateRequestDto.class));
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void
      whenProcessCreatingPaymentWithTemplate__thenExecuteTransactionAndEventsAndReturnMessageResponseDto() {
    // given
    MessageResponseDto expectedResponse =
        new MessageResponseDto(MessageResponseDto.OPERATION_IS_PROCESSING);

    when(paymentRepository.findById(ArgumentMatchers.anyLong()))
        .thenReturn(Optional.ofNullable(paymentEntity));
    when(paymentTypeRepository.findAll(ArgumentMatchers.any(Example.class)))
        .thenReturn(List.of(paymentType));
    when(transactionService.createTransaction(
            ArgumentMatchers.any(TransactionCreateRequestDto.class)))
        .thenReturn(
            validTransactionMessageResponseDto.toBuilder()
                .status(TransactionStatus.CREATED.name())
                .build());
    when(accountService.getDetailedAccountInfo(paymentEntity.getAccountNumberWithdrawn()))
        .thenReturn(accountWithdrawnDetailedResponseDto);
    // when
    MessageResponseDto actualResponse = paymentService.processCreatingPayment(PAYMENT_ID);
    // then
    verify(transactionService)
        .createTransaction(ArgumentMatchers.any(TransactionCreateRequestDto.class));
    verify(paymentRepository).save(ArgumentMatchers.any(PaymentEntity.class));
    assertEquals(expectedResponse, actualResponse);
  }
}

package com.andersenlab.etalon.transactionservice.service.impl;

import com.andersenlab.etalon.transactionservice.client.InfoServiceClient;
import com.andersenlab.etalon.transactionservice.dto.auth.request.CreateConfirmationRequestDto;
import com.andersenlab.etalon.transactionservice.dto.auth.response.CreateConfirmationResponseDto;
import com.andersenlab.etalon.transactionservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.request.PaymentRequestDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.request.TransactionCreateRequestDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.CreatePaymentResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.PaymentTypeResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.TransactionMessageResponseDto;
import com.andersenlab.etalon.transactionservice.entity.PaymentEntity;
import com.andersenlab.etalon.transactionservice.entity.PaymentTypeEntity;
import com.andersenlab.etalon.transactionservice.entity.TemplateEntity;
import com.andersenlab.etalon.transactionservice.exception.BusinessException;
import com.andersenlab.etalon.transactionservice.mapper.PaymentMapper;
import com.andersenlab.etalon.transactionservice.mapper.TemplateMapper;
import com.andersenlab.etalon.transactionservice.repository.PaymentRepository;
import com.andersenlab.etalon.transactionservice.repository.PaymentTypeRepository;
import com.andersenlab.etalon.transactionservice.repository.TemplateRepository;
import com.andersenlab.etalon.transactionservice.service.AccountService;
import com.andersenlab.etalon.transactionservice.service.PaymentService;
import com.andersenlab.etalon.transactionservice.service.TransactionService;
import com.andersenlab.etalon.transactionservice.service.ValidationService;
import com.andersenlab.etalon.transactionservice.util.OperationUtils;
import com.andersenlab.etalon.transactionservice.util.enums.ConfirmationMethod;
import com.andersenlab.etalon.transactionservice.util.enums.CurrencyName;
import com.andersenlab.etalon.transactionservice.util.enums.Details;
import com.andersenlab.etalon.transactionservice.util.enums.Operation;
import com.andersenlab.etalon.transactionservice.util.enums.PaymentStatus;
import com.andersenlab.etalon.transactionservice.util.enums.PaymentType;
import com.andersenlab.etalon.transactionservice.util.enums.TemplateType;
import com.andersenlab.etalon.transactionservice.util.enums.TransactionStatus;
import com.andersenlab.etalon.transactionservice.util.filter.PaymentFilter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
  private final TransactionService transactionService;
  private final ValidationService validationService;
  private final PaymentRepository paymentRepository;
  private final PaymentTypeRepository paymentTypeRepository;
  private final TemplateRepository templateRepository;
  private final AccountService accountService;
  private final PaymentMapper paymentMapper;
  private final TemplateMapper templateMapper;

  public static final String PAYMENT_FOR = "Payment for";
  public static final String TOP_UP = "Top-up";
  public static final Boolean IS_FEE_PROVIDED = true;
  private final InfoServiceClient infoServiceClient;
  private final Map<PaymentStatus, Predicate<PaymentStatus>> validPaymentTransitions =
      Map.of(
          PaymentStatus.CREATED,
              newStatus ->
                  newStatus == PaymentStatus.USER_CONFIRMED || newStatus == PaymentStatus.DECLINED,
          PaymentStatus.USER_CONFIRMED,
              newStatus ->
                  newStatus == PaymentStatus.CODE_CONFIRMED || newStatus == PaymentStatus.DECLINED,
          PaymentStatus.CODE_CONFIRMED,
              newStatus ->
                  newStatus == PaymentStatus.PROCESSING || newStatus == PaymentStatus.DECLINED,
          PaymentStatus.PROCESSING,
              newStatus ->
                  newStatus == PaymentStatus.APPROVED || newStatus == PaymentStatus.DECLINED);

  @Override
  @Transactional
  public CreatePaymentResponseDto createPayment(
      PaymentRequestDto paymentRequestDto, String userId) {

    validatePaymentData(paymentRequestDto, userId);

    PaymentEntity payment =
        paymentMapper.toPaymentEntity(userId, paymentRequestDto, PaymentStatus.CREATED);
    PaymentEntity savedPayment = paymentRepository.save(payment);

    log.info(
        "{createPayment} - > Payment with id-{} to be changed on status -> {}",
        payment.getId(),
        payment.getStatus());

    updatePaymentStatus(payment.getId(), PaymentStatus.USER_CONFIRMED);

    CreateConfirmationResponseDto createConfirmationResponseDto =
        infoServiceClient.createConfirmation(
            new CreateConfirmationRequestDto(
                savedPayment.getId(), Operation.CREATE_PAYMENT, ConfirmationMethod.EMAIL));

    return new CreatePaymentResponseDto(createConfirmationResponseDto.confirmationId());
  }

  @Override
  @Transactional
  public MessageResponseDto processCreatingPayment(Long paymentId) {

    PaymentEntity payment = getPaymentById(paymentId);

    validationService.validatePaymentStatus(payment.getStatus());

    updatePaymentStatus(paymentId, PaymentStatus.PROCESSING);

    PaymentTypeResponseDto paymentTypeResponseDto = getPaymentType(payment.getPaymentProductId());

    BigDecimal paymentCommission = OperationUtils.calculatePaymentCommission(payment.getAmount());
    String accountNumberWithdrawn = payment.getAccountNumberWithdrawn();
    CurrencyName transactionCurrency =
        accountService.getDetailedAccountInfo(accountNumberWithdrawn).currency();
    TransactionMessageResponseDto transactionMessageResponseDto =
        transactionService.createTransaction(
            TransactionCreateRequestDto.builder()
                .amount(payment.getAmount())
                .currency(transactionCurrency)
                .details(Details.PAYMENT)
                .accountNumberReplenished(paymentTypeResponseDto.getIban())
                .accountNumberWithdrawn(accountNumberWithdrawn)
                .feeAmount(paymentCommission)
                .isFeeProvided(IS_FEE_PROVIDED)
                .transactionName(buildTransactionName(paymentTypeResponseDto))
                .build());

    if (Objects.nonNull(transactionMessageResponseDto)
        && TransactionStatus.CREATED.name().equals(transactionMessageResponseDto.status())) {
      payment.setTransactionId(transactionMessageResponseDto.transactionId());
      if (Boolean.TRUE.equals(payment.getIsTemplate())) {
        saveTemplate(payment);
      }
      paymentRepository.save(payment);
      return new MessageResponseDto(MessageResponseDto.OPERATION_IS_PROCESSING);
    } else {
      return new MessageResponseDto(MessageResponseDto.OPERATION_IS_FAILED);
    }
  }

  @Override
  public PaymentTypeResponseDto getPaymentTypeById(Long paymentTypeId) {
    return getPaymentType(paymentTypeId);
  }

  public PaymentEntity getPaymentById(long paymentId) {
    return paymentRepository
        .findById(paymentId)
        .orElseThrow(
            () ->
                new BusinessException(
                    HttpStatus.NOT_FOUND,
                    String.format(BusinessException.PAYMENT_NOT_FOUND_BY_ID, paymentId)));
  }

  @Override
  public void updatePaymentStatus(Long paymentId, PaymentStatus newStatus) {
    PaymentEntity payment = getPaymentById(paymentId);
    PaymentStatus currentStatus = payment.getStatus();

    if (!isValidStatusTransition(currentStatus, newStatus)) {
      log.warn(
          "Invalid status transition from {} to {} for payment id-{}",
          currentStatus,
          newStatus,
          paymentId);
      return;
    }

    payment.setStatus(newStatus);
    paymentRepository.save(payment);
    log.info(
        "{updatePaymentStatus}-> Payment with id-{} status changed from {} to {}",
        paymentId,
        currentStatus,
        newStatus);
  }

  public boolean isValidStatusTransition(PaymentStatus currentStatus, PaymentStatus newStatus) {
    return validPaymentTransitions.getOrDefault(currentStatus, status -> false).test(newStatus);
  }

  private void validatePaymentData(PaymentRequestDto paymentRequestDto, String userId) {
    validationService.validateAccount(
        accountService.getDetailedAccountInfo(paymentRequestDto.accountNumberWithdrawn()));
    if (Boolean.TRUE.equals(paymentRequestDto.isTemplate())
        && Boolean.FALSE.equals(
            validationService
                .validateTemplateName(paymentRequestDto.templateName(), userId)
                .status())) {
      throw new BusinessException(
          HttpStatus.BAD_REQUEST, BusinessException.TEMPLATE_NAME_ALREADY_BEEN_SAVED);
    }
    validationService.validateAmountMoreThanOne(paymentRequestDto.amount());
    validationService.validateOwnership(
        List.of(paymentRequestDto.accountNumberWithdrawn()),
        accountService.getUserAccountNumbers(userId),
        new BusinessException(
            HttpStatus.BAD_REQUEST, BusinessException.OPERATION_REJECTED_DUE_TO_SECURITY));
  }

  private String buildTransactionName(PaymentTypeResponseDto paymentTypeResponseDto) {
    return new StringJoiner(StringUtils.SPACE)
        .add(
            paymentTypeResponseDto.getType().equals(PaymentType.WALLET.name())
                ? TOP_UP
                : PAYMENT_FOR)
        .add(paymentTypeResponseDto.getName())
        .add(
            paymentTypeResponseDto.getType().equals(PaymentType.WALLET.name())
                ? paymentTypeResponseDto.getType()
                : StringUtils.EMPTY)
        .toString();
  }

  private void saveTemplate(PaymentEntity payment) {
    TemplateEntity templateEntity = templateMapper.paymentToTemplate(payment);
    templateEntity.setTemplateType(TemplateType.PAYMENT);
    templateRepository.save(templateEntity);
  }

  private PaymentTypeResponseDto getPaymentType(Long paymentProductId) {
    return getAllPaymentTypes(PaymentFilter.builder().build()).stream()
        .filter(p -> p.getId().equals(paymentProductId))
        .findFirst()
        .orElseThrow(
            () ->
                new BusinessException(
                    HttpStatus.NOT_FOUND,
                    String.format(BusinessException.NO_PAYMENT_TYPE_FOUND, paymentProductId)));
  }

  @Cacheable(value = "payment_types_response")
  @Override
  public List<PaymentTypeResponseDto> getAllPaymentTypes(PaymentFilter filter) {
    PaymentType type = Optional.ofNullable(filter.getType()).map(PaymentType::valueOf).orElse(null);
    return paymentMapper.paymentTypeEntitiesToDto(
        paymentTypeRepository.findAll(
            Example.of(
                PaymentTypeEntity.builder().type(type).build(), ExampleMatcher.matchingAll())));
  }
}

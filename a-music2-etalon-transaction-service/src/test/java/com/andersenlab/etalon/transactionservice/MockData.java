package com.andersenlab.etalon.transactionservice;

import com.andersenlab.etalon.transactionservice.dto.account.request.AccountRequestDto;
import com.andersenlab.etalon.transactionservice.dto.account.response.AccountDetailedResponseDto;
import com.andersenlab.etalon.transactionservice.dto.card.response.CardResponseDto;
import com.andersenlab.etalon.transactionservice.dto.common.FeeDto;
import com.andersenlab.etalon.transactionservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.transactionservice.dto.info.response.BankDetailResponseDto;
import com.andersenlab.etalon.transactionservice.dto.info.response.BankInfoResponseDto;
import com.andersenlab.etalon.transactionservice.dto.info.response.ExchangeRateResponseDto;
import com.andersenlab.etalon.transactionservice.dto.template.request.TemplatePatchRequestDto;
import com.andersenlab.etalon.transactionservice.dto.template.response.TemplateInfoResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.request.CreateTransferRequestDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.request.PaymentRequestDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.request.TransactionCreateRequestDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.request.TransferRequestDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.CreateNewTransferResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.EventResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.FinePaymentTypeResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.PaymentTypeResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.TaxPaymentTypeResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.TransactionDetailedResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.TransactionMessageResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.TransferResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.TransferTypeResponseDto;
import com.andersenlab.etalon.transactionservice.entity.EventEntity;
import com.andersenlab.etalon.transactionservice.entity.FineEntity;
import com.andersenlab.etalon.transactionservice.entity.PaymentEntity;
import com.andersenlab.etalon.transactionservice.entity.PaymentTypeEntity;
import com.andersenlab.etalon.transactionservice.entity.TaxDepartmentEntity;
import com.andersenlab.etalon.transactionservice.entity.TaxEntity;
import com.andersenlab.etalon.transactionservice.entity.TemplateEntity;
import com.andersenlab.etalon.transactionservice.entity.TransactionEntity;
import com.andersenlab.etalon.transactionservice.entity.TransferEntity;
import com.andersenlab.etalon.transactionservice.entity.TransferTypeEntity;
import com.andersenlab.etalon.transactionservice.util.enums.CardStatus;
import com.andersenlab.etalon.transactionservice.util.enums.CurrencyName;
import com.andersenlab.etalon.transactionservice.util.enums.Details;
import com.andersenlab.etalon.transactionservice.util.enums.EventStatus;
import com.andersenlab.etalon.transactionservice.util.enums.EventType;
import com.andersenlab.etalon.transactionservice.util.enums.FineType;
import com.andersenlab.etalon.transactionservice.util.enums.PaymentStatus;
import com.andersenlab.etalon.transactionservice.util.enums.PaymentType;
import com.andersenlab.etalon.transactionservice.util.enums.TaxType;
import com.andersenlab.etalon.transactionservice.util.enums.TemplateType;
import com.andersenlab.etalon.transactionservice.util.enums.TransactionStatus;
import com.andersenlab.etalon.transactionservice.util.enums.TransferStatus;
import com.andersenlab.etalon.transactionservice.util.enums.TransferType;
import com.andersenlab.etalon.transactionservice.util.enums.Type;
import com.andersenlab.etalon.transactionservice.util.filter.TransactionFilter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class MockData {
  public static CardResponseDto getValidCardResponseDto() {
    return CardResponseDto.builder()
        .id(1L)
        .accountNumber("PL48234567840000000000000011")
        .availableCurrencies(List.of(CurrencyName.EUR, CurrencyName.PLN))
        .cardholderName("Test User")
        .status(CardStatus.ACTIVE)
        .balance(new BigDecimal(500))
        .number("5634693028650394")
        .build();
  }

  public static List<EventEntity> getValidEventEntityDtoList() {
    return List.of(getValidEventEntity());
  }

  public static FeeDto getValidFeeDto() {
    return FeeDto.builder().amount(BigDecimal.valueOf(0.01)).build();
  }

  public static Page<TransactionEntity> getValidTransactionEntityPage(Pageable pageable) {
    return new PageImpl<>(
        List.of(getValidTransactionEntity(), getValidTransactionEntity2()), pageable, 2);
  }

  public static TransactionEntity getValidTransactionEntity() {
    return TransactionEntity.builder()
        .transactionName("transactionName")
        .id(1L)
        .createAt(ZonedDateTime.parse("2023-09-05T11:00:00Z"))
        .receiverAccount("PL48234567840000000000000012")
        .senderAccount("PL48234567840000000000000011")
        .details(Details.PAYMENT)
        .status(TransactionStatus.APPROVED)
        .processedAt(ZonedDateTime.parse("2023-09-05T11:20:00Z"))
        .amount(BigDecimal.valueOf(23.25))
        .currency(CurrencyName.PLN)
        .eventEntityList(List.of(getValidUnboundEventEntity()))
        .build();
  }

  public static TransactionEntity getValidTransactionEntity2() {
    return TransactionEntity.builder()
        .transactionName("transactionName")
        .id(1L)
        .createAt(ZonedDateTime.parse("2023-09-18T11:00:00Z"))
        .receiverAccount("PL48234567840000000000000012")
        .senderAccount("PL48234567840000000000000011")
        .details(Details.PAYMENT)
        .status(TransactionStatus.APPROVED)
        .processedAt(ZonedDateTime.parse("2023-09-18T11:20:00Z"))
        .amount(BigDecimal.valueOf(1000))
        .currency(CurrencyName.PLN)
        .eventEntityList(
            List.of(getValidUnboundEventEntity(), getForeignAccountUnboundEventEntity()))
        .build();
  }

  public static EventEntity getValidEventEntity() {
    return EventEntity.builder()
        .id(1L)
        .createAt(ZonedDateTime.parse("2023-09-05T11:00:00Z"))
        .transactionEntity(getValidTransactionEntity())
        .accountNumber("PL48234567840000000000000011")
        .type(Type.INCOME)
        .amount(BigDecimal.TEN)
        .status(EventStatus.APPROVED)
        .eventType(EventType.BODY)
        .build();
  }

  public static EventEntity getValidUnboundEventEntity() {
    return EventEntity.builder()
        .id(1L)
        .createAt(ZonedDateTime.parse("2023-09-05T11:00:00Z"))
        .accountNumber("PL48234567840000000000000011")
        .type(Type.INCOME)
        .amount(BigDecimal.TEN)
        .status(EventStatus.APPROVED)
        .eventType(EventType.BODY)
        .build();
  }

  public static EventEntity getForeignAccountUnboundEventEntity() {
    return EventEntity.builder()
        .id(1L)
        .createAt(ZonedDateTime.parse("2023-09-05T11:00:00Z"))
        .accountNumber("PL48234567840000000000000013")
        .type(Type.INCOME)
        .amount(BigDecimal.TEN)
        .status(EventStatus.APPROVED)
        .eventType(EventType.BODY)
        .build();
  }

  public static TaxEntity getValidTaxEntity() {
    return TaxEntity.builder()
        .id(9L)
        .taxDepartmentEntity(getValidTaxDepartmentEntity())
        .taxType(TaxType.PROPERTY_TAX)
        .recipientName("Taxes PL")
        .build();
  }

  public static FineEntity getValidFineEntity() {
    return FineEntity.builder()
        .id(11L)
        .fineType(FineType.CAR_FINES)
        .recipientName("First Tax Office in Opole")
        .build();
  }

  public static TaxDepartmentEntity getValidTaxDepartmentEntity() {
    return TaxDepartmentEntity.builder().id(1L).name("Warsaw Tax Office").build();
  }

  public static PaymentTypeResponseDto getValidPaymentTypeDto() {
    return PaymentTypeResponseDto.builder()
        .id(1L)
        .subType("DEFAULT")
        .name("Electricity PL")
        .iban("PL60109010140000071219812871")
        .type(String.valueOf(PaymentType.UTILITIES))
        .fee(BigDecimal.valueOf(0.3))
        .build();
  }

  public static TaxPaymentTypeResponseDto getValidTaxPaymentTypeDto() {
    return TaxPaymentTypeResponseDto.builder()
        .id(11L)
        .subType("TAX")
        .name("Warsaw department Property tax")
        .iban("PL56101000712223140244000000")
        .type(String.valueOf(PaymentType.TAXES_AND_FINES))
        .recipientName("Taxes PL")
        .taxType(String.valueOf(TaxType.PROPERTY_TAX))
        .taxDepartmentName("Warsaw Tax Office")
        .fee(BigDecimal.valueOf(0.3))
        .build();
  }

  public static FinePaymentTypeResponseDto getValidFinePaymentTypeDto() {
    return FinePaymentTypeResponseDto.builder()
        .id(13L)
        .subType("FINE")
        .name("Speeding fine")
        .iban("PL47101000550201609009990000")
        .type(String.valueOf(PaymentType.TAXES_AND_FINES))
        .fineType(String.valueOf(FineType.CAR_FINES))
        .recipientName("First Tax Office in Opole")
        .fee(BigDecimal.valueOf(0.3))
        .build();
  }

  public static PaymentTypeEntity getValidPaymentTypeEntity() {
    return PaymentTypeEntity.builder()
        .id(1L)
        .name("Electricity PL")
        .iban("PL60109010140000071219812871")
        .type(PaymentType.UTILITIES)
        .build();
  }

  public static PaymentTypeEntity getValidTaxPaymentTypeEntity() {
    return PaymentTypeEntity.builder()
        .id(9L)
        .name("Property tax")
        .iban("PL56101000712223140244000000")
        .type(PaymentType.TAXES)
        .taxEntity(getValidTaxEntity())
        .build();
  }

  public static PaymentTypeEntity getValidFinePaymentTypeEntity() {
    return PaymentTypeEntity.builder()
        .id(11L)
        .name("Speeding fine")
        .iban("PL47101000550201609009990000")
        .type(PaymentType.FINES)
        .fineEntity(getValidFineEntity())
        .build();
  }

  public static List<String> getValidAccountNumbersDtoList() {
    return List.of("PL48234567840000000000000011", "PL48234567840000000000000012");
  }

  public static TransactionFilter getValidTransactionFilter() {
    return TransactionFilter.builder()
        .startDate("2023-09-05T11:00:00Z")
        .endDate("2023-09-05T11:00:00Z")
        .orderBy("DESC")
        .sortBy("createAt")
        .type(Type.INCOME.name())
        .withEvents(true)
        .transactionGroup("payment")
        .transactionStatus("approved")
        .amountFrom(new BigDecimal(0))
        .amountTo(new BigDecimal(1000))
        .build();
  }

  public static TransactionFilter getValidTransactionFilter2() {
    return TransactionFilter.builder()
        .startDate("2023-09-05T11:00:00Z")
        .endDate("2023-09-05T11:00:00Z")
        .orderBy("ASC")
        .sortBy("createAt")
        .type(Type.INCOME.name())
        .withEvents(true)
        .build();
  }

  public static List<EventResponseDto> getValidEventResponseDtoList() {
    return List.of(
        EventResponseDto.builder()
            .id(1L)
            .type("INCOME")
            .amount(BigDecimal.TEN)
            .status("APPROVED")
            .createAt("2023-09-05T11:00:00Z")
            .name("transactionName")
            .accountNumber("PL48234567840000000000000011")
            .eventType("BODY")
            .build());
  }

  public static TransactionCreateRequestDto getValidTransactionCreateRequestDto() {
    return TransactionCreateRequestDto.builder()
        .transactionName("transactionName")
        .details(Details.PAYMENT)
        .isFeeProvided(false)
        .accountNumberWithdrawn("PL48234567840000000000000011")
        .amount(BigDecimal.valueOf(10.00).setScale(2, RoundingMode.UP))
        .accountNumberReplenished("PL48234567840000000000000012")
        .feeAmount(null)
        .build();
  }

  public static TransactionCreateRequestDto getValidTransactionCreateRequestDtoWithNoCard() {
    return TransactionCreateRequestDto.builder()
        .transactionName("transactionName")
        .details(Details.PAYMENT)
        .isFeeProvided(false)
        .accountNumberWithdrawn("PL48234567840000000000000011")
        .amount(BigDecimal.TEN)
        .accountNumberReplenished("PL48234567840000000000000888")
        .feeAmount(null)
        .build();
  }

  public static PaymentRequestDto getValidPaymentRequestDto() {
    return PaymentRequestDto.builder()
        .accountNumberWithdrawn("PL48234567840000000000000011")
        .amount(BigDecimal.TEN)
        .paymentProductId(1L)
        .isTemplate(true)
        .comment("comment")
        .destination("PL59109010140000071219812872")
        .templateName("Gas")
        .transactionName("transaction")
        .build();
  }

  public static TemplateEntity getValidTemplateEntityForPayments() {
    return TemplateEntity.builder()
        .templateId(1L)
        .templateName("Test template")
        .description("comment")
        .amount(BigDecimal.TEN)
        .productId(1L)
        .userId("user")
        .templateType(TemplateType.PAYMENT)
        .destination("+48121234567")
        .build();
  }

  public static TemplateEntity getValidTemplateEntityForTransfers() {
    return TemplateEntity.builder()
        .templateId(1L)
        .templateName("Test template")
        .description("comment")
        .amount(BigDecimal.TEN)
        .productId(1L)
        .userId("user")
        .templateType(TemplateType.TRANSFER)
        .source("PL82234567840000000000000020")
        .destination("PL81234567840000000000000021")
        .build();
  }

  public static TemplateInfoResponseDto getValidTemplateInfoResponseDtoForPayments() {
    return TemplateInfoResponseDto.builder()
        .templateId(1L)
        .templateName("Test template")
        .description("comment")
        .amount(BigDecimal.TEN)
        .productId(1L)
        .templateType(TemplateType.PAYMENT)
        .destination("+48121234567")
        .build();
  }

  public static TemplatePatchRequestDto getValidTemplateWithSameNamePatchRequestDto() {
    return TemplatePatchRequestDto.builder()
        .templateName("name1")
        .description("updated comment")
        .amount(BigDecimal.valueOf(21.5))
        .source("PL82234567840000000000000020")
        .destination("PL81234567840000000000000021")
        .build();
  }

  public static TemplatePatchRequestDto getValidTemplatePatchRequestDto() {
    return TemplatePatchRequestDto.builder()
        .templateName("updated template")
        .description("updated comment")
        .amount(BigDecimal.valueOf(21.5))
        .source("PL82234567840000000000000020")
        .destination("PL81234567840000000000000021")
        .build();
  }

  public static TransactionCreateRequestDto getValidTransactionCreateRequestDtoWithFee() {
    return TransactionCreateRequestDto.builder()
        .transactionName("transactionName")
        .details(Details.PAYMENT)
        .isFeeProvided(true)
        .accountNumberWithdrawn("PL48234567840000000000000011")
        .amount(BigDecimal.TEN)
        .accountNumberReplenished("PL48234567840000000000000012")
        .feeAmount(BigDecimal.ONE)
        .build();
  }

  public static TransactionDetailedResponseDto getValidTransactionDetailedResponseDto() {
    return TransactionDetailedResponseDto.builder()
        .transactionName("transactionName")
        .incomeAccountNumber("PL48234567840000000000000011")
        .outcomeAccountNumber("PL48234567840000000000000012")
        .transactionAmount(BigDecimal.TEN)
        .id(1L)
        .type(Type.INCOME)
        .transactionTime("11:20:00")
        .transactionDate("05.09.2023")
        .status(TransactionStatus.APPROVED)
        .eventType(EventType.BODY)
        .build();
  }

  public static AccountDetailedResponseDto getValidAccountDetailedResponseDto() {
    return AccountDetailedResponseDto.builder()
        .balance(BigDecimal.TEN)
        .userId("1L")
        .iban("PL48234567840000000000000011")
        .id(1L)
        .isBlocked(false)
        .build();
  }

  public static TransactionEntity getValidTransactionEntityWithStatusDeclined() {
    return TransactionEntity.builder()
        .transactionName("transactionName")
        .id(1L)
        .createAt(ZonedDateTime.parse("2021-05-01T05:30:00Z"))
        .details(Details.OPEN_DEPOSIT)
        .status(TransactionStatus.DECLINED)
        .processedAt(ZonedDateTime.parse("2021-05-01T05:40:00Z"))
        .build();
  }

  public static EventEntity getValidEventEntityWithDeclinedTransactionStatus() {
    return EventEntity.builder()
        .id(1L)
        .createAt(ZonedDateTime.parse("2021-05-01T05:30:00Z"))
        .transactionEntity(getValidTransactionEntityWithStatusDeclined())
        .accountNumber("PL48234567840000000000000011")
        .type(Type.INCOME)
        .amount(BigDecimal.TEN)
        .status(EventStatus.DECLINED)
        .eventType(EventType.BODY)
        .build();
  }

  public static TransactionCreateRequestDto getValidTransactionCreateRequestDtoWithLoanInterest() {
    return TransactionCreateRequestDto.builder()
        .transactionName("transactionName")
        .details(Details.PAYMENT)
        .isFeeProvided(false)
        .accountNumberWithdrawn("PL48234567840000000000000011")
        .amount(BigDecimal.TEN)
        .accountNumberReplenished("PL48234567840000000000000012")
        .feeAmount(BigDecimal.ZERO)
        .loanInterestAmount(BigDecimal.ONE)
        .build();
  }

  public static PaymentEntity getValidPaymentEntity() {

    return PaymentEntity.builder()
        .id(1L)
        .paymentProductId(1L)
        .comment("Comment")
        .userId("user")
        .amount(BigDecimal.valueOf(1000))
        .accountNumberWithdrawn("PL48234567840000000000000248")
        .isTemplate(true)
        .templateName("Temp 2")
        .status(PaymentStatus.CREATED)
        .destination("STR0123456789")
        .build();
  }

  public static PaymentRequestDto createValidPaymentRequestDto() {
    return PaymentRequestDto.builder()
        .accountNumberWithdrawn("PL48234567840000000000000011")
        .amount(BigDecimal.ONE)
        .paymentProductId(1L)
        .isTemplate(false)
        .comment("comment")
        .templateName("Gas")
        .destination("STR0123456789")
        .build();
  }

  public static TransactionMessageResponseDto getValidTransactionMessageResponseDto() {
    return TransactionMessageResponseDto.builder()
        .messageResponseDto(
            new MessageResponseDto(String.format(MessageResponseDto.TRANSACTION_IS_PROCESSING, 1)))
        .status(TransactionStatus.CREATED.name())
        .transactionId(1L)
        .build();
  }

  public static TransactionMessageResponseDto getInvalidTransactionMessageResponseDto() {
    return TransactionMessageResponseDto.builder()
        .messageResponseDto(
            new MessageResponseDto(String.format(MessageResponseDto.TRANSACTION_FAILED, 1)))
        .status(TransactionStatus.DECLINED.name())
        .transactionId(1L)
        .build();
  }

  public static TransferResponseDto getValidTransferResponseDto() {
    return TransferResponseDto.builder()
        .id(2L)
        .description("comment")
        .totalAmount(BigDecimal.valueOf(10.00).setScale(2, RoundingMode.UP))
        .amount(BigDecimal.valueOf(9.00).setScale(2, RoundingMode.UP))
        .fee(BigDecimal.valueOf(1.00).setScale(2, RoundingMode.UP))
        .beneficiaryAccountNumber("PL48234567840000000000000031")
        .senderAccountNumber("PL48234567840000000000000011")
        .beneficiaryFullName("Martha K.")
        .senderFullName("Yan Kowalski")
        .beneficiaryBank("Etalon Bank")
        .status(TransferStatus.APPROVED)
        .createAt(ZonedDateTime.parse("2023-11-23T17:47:24.613519Z"))
        .updateAt(ZonedDateTime.parse("2023-11-23T17:47:36.276218Z"))
        .build();
  }

  public static TransferEntity getValidTransferEntity() {
    return TransferEntity.builder()
        .id(1L)
        .comment("Comment")
        .userId("1L")
        .amount(BigDecimal.valueOf(1000))
        .source("PL48234567840000000000000011")
        .destination("PL48234567840000000000000012")
        .status(TransferStatus.CREATED)
        .transactionId(3L)
        .transferTypeId(1L)
        .isTemplate(true)
        .templateName("To another account")
        .isFeeProvided(false)
        .fee(BigDecimal.valueOf(10))
        .feeRate(BigDecimal.valueOf(0.01))
        .standardRate(BigDecimal.valueOf(4.123))
        .build();
  }

  public static TransferRequestDto getValidTransferRequestDto() {
    return TransferRequestDto.builder()
        .comment("Comment")
        .amount(BigDecimal.valueOf(1000))
        .source("PL48234567840000000000000011")
        .destination("PL48234567840000000000000012")
        .transferTypeId(1L)
        .isTemplate(true)
        .templateName("To another account")
        .build();
  }

  public static CreateTransferRequestDto getValidCreateTransferRequestDto() {
    return CreateTransferRequestDto.builder()
        .description("Comment")
        .amount(BigDecimal.valueOf(1000))
        .sender("PL48234567840000000000000011")
        .beneficiary("PL48234567840000000000000012")
        .build();
  }

  public static TransferRequestDto getValidTransferToCardRequestDto() {
    return TransferRequestDto.builder()
        .comment("Transfer from card")
        .amount(BigDecimal.valueOf(1000))
        .source("4246700000001090")
        .destination("4246700000001025")
        .transferTypeId(2L)
        .isTemplate(true)
        .templateName("Transfer to another card")
        .build();
  }

  public static AccountRequestDto getValidAccountRequestDto() {
    return AccountRequestDto.builder()
        .accountNumber("PL48234567840000000000000011")
        .isBlocked(false)
        .status("ACTIVE")
        .build();
  }

  public static AccountDetailedResponseDto getValidAccountWithdrawnDetailedResponseDto() {
    return AccountDetailedResponseDto.builder()
        .id(1L)
        .iban("PL48234567840000000000000011")
        .userId("1L")
        .balance(BigDecimal.valueOf(1000))
        .currency(CurrencyName.PLN)
        .isBlocked(false)
        .build();
  }

  public static AccountDetailedResponseDto getValidAccountReplenishedDetailedResponseDto() {
    return AccountDetailedResponseDto.builder()
        .id(2L)
        .iban("PL48234567840000000000000021")
        .userId("not same user")
        .balance(BigDecimal.valueOf(1000))
        .currency(CurrencyName.PLN)
        .isBlocked(false)
        .build();
  }

  public static TransferTypeResponseDto getValidTransferTypeToAnotherAccountResponseDto() {
    return TransferTypeResponseDto.builder()
        .id(1L)
        .name("Transfer money between accounts")
        .transferType("TO_ANOTHER_ACCOUNT")
        .build();
  }

  public static TransferTypeResponseDto getValidTransferTypeToCardResponseDto() {
    return TransferTypeResponseDto.builder()
        .id(2L)
        .name("Card – to – card transfers")
        .transferType("TO_CARD")
        .build();
  }

  public static TransferTypeResponseDto getValidTransferTypeToMyAccountResponseDto() {
    return TransferTypeResponseDto.builder()
        .id(3L)
        .name("Transfer money between my accounts")
        .transferType("TO_MY_ACCOUNT")
        .build();
  }

  public static TransferTypeEntity getValidTransferToAnotherAccountTypeEntity() {
    return TransferTypeEntity.builder()
        .id(1L)
        .name("Transfer money between accounts")
        .transferType(TransferType.TO_ANOTHER_ACCOUNT)
        .build();
  }

  public static TransferTypeEntity getValidTransferToCardTypeEntity() {
    return TransferTypeEntity.builder()
        .id(2L)
        .name("Card – to – card transfers")
        .transferType(TransferType.TO_CARD)
        .build();
  }

  public static TransferTypeEntity getValidTransferToMyAccountTypeEntity() {
    return TransferTypeEntity.builder()
        .id(3L)
        .name("Transfer money between my accounts")
        .transferType(TransferType.TO_MY_ACCOUNT)
        .build();
  }

  public static CreateNewTransferResponseDto getValidCreateNewTransferResponse() {
    return CreateNewTransferResponseDto.builder()
        .transferId(1L)
        .sender("PL48234567840000000000000011")
        .beneficiary("PL48234567840000000000000012")
        .amount(BigDecimal.valueOf(1000))
        .feeRate(BigDecimal.valueOf(0.01))
        .fee(BigDecimal.valueOf(10))
        .standardRate(BigDecimal.valueOf(4.123))
        .totalAmount(BigDecimal.valueOf(1010))
        .description("Comment")
        .build();
  }

  public static List<ExchangeRateResponseDto> getValidExchangeRateResponseDtoList() {
    return List.of(
        new ExchangeRateResponseDto(
            CurrencyName.USD, null, BigDecimal.valueOf(3.9237), BigDecimal.valueOf(4.0838), null),
        new ExchangeRateResponseDto(
            CurrencyName.EUR, null, BigDecimal.valueOf(4.2384), BigDecimal.valueOf(4.4113), null));
  }

  public static EventEntity.EventEntityBuilder getFilledEventEntityBuilder() {
    return EventEntity.builder()
        .id(1L)
        .status(EventStatus.APPROVED)
        .type(Type.INCOME)
        .amount(new BigDecimal(12))
        .accountNumber("PL48234567840000000000000011")
        .transactionEntity(getFilledTransactionEntityBuilder().build())
        .eventType(EventType.BODY);
  }

  public static TransactionEntity.TransactionEntityBuilder getFilledTransactionEntityBuilder() {
    return TransactionEntity.builder()
        .id(2L)
        .status(TransactionStatus.APPROVED)
        .details(Details.TRANSFER)
        .transactionName("transactionName")
        .createAt(ZonedDateTime.now())
        .processedAt(ZonedDateTime.now());
  }

  public static PaymentTypeEntity.PaymentTypeEntityBuilder getFilledPaymentTypeEntityBuilder() {
    return PaymentTypeEntity.builder()
        .type(PaymentType.FINES)
        .fineEntity(getFilledFineEntityBuilder().build())
        .taxEntity(getFilledTaxEntityBuilder().build())
        .name("name")
        .iban("iban")
        .id(1L);
  }

  public static TaxEntity.TaxEntityBuilder getFilledTaxEntityBuilder() {
    return TaxEntity.builder()
        .taxDepartmentEntity(new TaxDepartmentEntity())
        .taxType(TaxType.INCOME_TAX)
        .recipientName("name")
        .id(2L);
  }

  public static FineEntity.FineEntityBuilder getFilledFineEntityBuilder() {
    return FineEntity.builder().fineType(FineType.CAR_FINES).id(1L).recipientName("name");
  }

  public static BankInfoResponseDto getValidBankInfoResponseDto() {
    return BankInfoResponseDto.builder()
        .isForeignBank(true)
        .fullName("Alior Bank S.A.")
        .callName("Alior Bank")
        .bankDetails(List.of(new BankDetailResponseDto("519365", "019365", CurrencyName.PLN)))
        .isForeignBank(true)
        .build();
  }
}

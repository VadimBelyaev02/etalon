package com.andersenlab.etalon.infoservice;

import static com.andersenlab.etalon.infoservice.util.Constants.EMAIL_FROM;
import static com.andersenlab.etalon.infoservice.util.Constants.EMAIL_TO;

import com.andersenlab.etalon.infoservice.dto.common.TransferReceiptContext;
import com.andersenlab.etalon.infoservice.dto.email.SendRawEmailRequestDto;
import com.andersenlab.etalon.infoservice.dto.request.BaseEmailRequestDto;
import com.andersenlab.etalon.infoservice.dto.request.ConfirmationEmailRequestDto;
import com.andersenlab.etalon.infoservice.dto.request.ConfirmationRequestDto;
import com.andersenlab.etalon.infoservice.dto.request.ConfirmationStatusRequestDto;
import com.andersenlab.etalon.infoservice.dto.request.CreateConfirmationRequestDto;
import com.andersenlab.etalon.infoservice.dto.request.TransactionStatementPdfRequestDto;
import com.andersenlab.etalon.infoservice.dto.response.AccountDetailedResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.BankDetailResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.BankInfoResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.CountryCodesResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.DateTimeResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.EventResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.ExchangeRateResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.TransactionDetailedResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.TransferResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.UserDataResponseDto;
import com.andersenlab.etalon.infoservice.dto.sqs.CreateConfirmationMessage;
import com.andersenlab.etalon.infoservice.entity.AtmEntity;
import com.andersenlab.etalon.infoservice.entity.BankAddressTranslationsEntity;
import com.andersenlab.etalon.infoservice.entity.BankBranchEntity;
import com.andersenlab.etalon.infoservice.entity.BankContactsEntity;
import com.andersenlab.etalon.infoservice.entity.BankDetailsEntity;
import com.andersenlab.etalon.infoservice.entity.BankInfoEntity;
import com.andersenlab.etalon.infoservice.entity.ConfirmationEntity;
import com.andersenlab.etalon.infoservice.util.Constants;
import com.andersenlab.etalon.infoservice.util.enums.ConfirmationMethod;
import com.andersenlab.etalon.infoservice.util.enums.ConfirmationStatus;
import com.andersenlab.etalon.infoservice.util.enums.Currency;
import com.andersenlab.etalon.infoservice.util.enums.CurrencyName;
import com.andersenlab.etalon.infoservice.util.enums.EmailType;
import com.andersenlab.etalon.infoservice.util.enums.Operation;
import com.andersenlab.etalon.infoservice.util.enums.TransactionStatus;
import com.andersenlab.etalon.infoservice.util.enums.TransferStatus;
import com.andersenlab.etalon.infoservice.util.enums.Type;
import com.andersenlab.etalon.infoservice.util.filter.TransactionFilter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockData {

  public static ConfirmationEntity getConfirmationEntityWithConfirmedStatus() {
    return ConfirmationEntity.builder()
        .id(1L)
        .confirmationCode(123456)
        .operation(Operation.EMAIL_MODIFICATION)
        .status(ConfirmationStatus.CONFIRMED)
        .targetId(5L)
        .confirmationMethod(ConfirmationMethod.EMAIL)
        .invalidAttempts(0)
        .blockedAt(null)
        .createAt(ZonedDateTime.now(ZoneId.of("UTC")))
        .updateAt(ZonedDateTime.now(ZoneId.of("UTC")))
        .build();
  }

  public static ConfirmationEntity getConfirmationEntityWithRejectedStatus() {
    return ConfirmationEntity.builder()
        .id(1L)
        .confirmationCode(123456)
        .operation(Operation.EMAIL_MODIFICATION)
        .status(ConfirmationStatus.REJECTED)
        .targetId(5L)
        .confirmationMethod(ConfirmationMethod.EMAIL)
        .invalidAttempts(0)
        .blockedAt(null)
        .createAt(ZonedDateTime.now(ZoneId.of("UTC")))
        .updateAt(ZonedDateTime.now(ZoneId.of("UTC")))
        .build();
  }

  public static ConfirmationEntity getExpiredConfirmationEntity() {
    return ConfirmationEntity.builder()
        .id(1L)
        .confirmationCode(123456)
        .operation(Operation.EMAIL_MODIFICATION)
        .status(ConfirmationStatus.CREATED)
        .targetId(5L)
        .confirmationMethod(ConfirmationMethod.EMAIL)
        .invalidAttempts(0)
        .blockedAt(null)
        .createAt(ZonedDateTime.now().minusMinutes(15))
        .updateAt(ZonedDateTime.now().minusMinutes(15))
        .build();
  }

  public static ConfirmationEntity getConfirmationEntityWithBlockedStatus() {
    return ConfirmationEntity.builder()
        .id(1L)
        .confirmationCode(123456)
        .operation(Operation.EMAIL_MODIFICATION)
        .status(ConfirmationStatus.BLOCKED)
        .targetId(5L)
        .confirmationMethod(ConfirmationMethod.EMAIL)
        .invalidAttempts(3)
        .blockedAt(ZonedDateTime.now(ZoneId.of("UTC")))
        .createAt(ZonedDateTime.now(ZoneId.of("UTC")))
        .updateAt(ZonedDateTime.now(ZoneId.of("UTC")))
        .build();
  }

  public static ConfirmationEntity getConfirmationEntityWithLastAttempt() {
    return ConfirmationEntity.builder()
        .id(1L)
        .confirmationCode(123456)
        .operation(Operation.EMAIL_MODIFICATION)
        .status(ConfirmationStatus.CREATED)
        .targetId(5L)
        .confirmationMethod(ConfirmationMethod.EMAIL)
        .invalidAttempts(2)
        .blockedAt(null)
        .createAt(ZonedDateTime.now(ZoneId.of("UTC")))
        .updateAt(ZonedDateTime.now(ZoneId.of("UTC")))
        .build();
  }

  public static ConfirmationEntity getValidConfirmationEntity() {
    return ConfirmationEntity.builder()
        .id(1L)
        .confirmationCode(123456)
        .operation(Operation.EMAIL_MODIFICATION)
        .status(ConfirmationStatus.CREATED)
        .targetId(5L)
        .confirmationMethod(ConfirmationMethod.EMAIL)
        .invalidAttempts(0)
        .createAt(ZonedDateTime.now())
        .updateAt(ZonedDateTime.now())
        .build();
  }

  public static BankInfoEntity getValidBankInfoEntity() {
    return BankInfoEntity.builder()
        .id(4L)
        .callName("mBank")
        .fullName("mBank S.A.")
        .bankDetails(
            List.of(
                BankDetailsEntity.builder()
                    .bin("415046")
                    .bankCode("315046")
                    .currency(Currency.PLN)
                    .build(),
                BankDetailsEntity.builder()
                    .bin("554895")
                    .bankCode("354895")
                    .currency(Currency.PLN)
                    .build()))
        .build();
  }

  public static BankInfoResponseDto getValidBankInfoResponseDto() {
    return BankInfoResponseDto.builder()
        .isForeignBank(true)
        .callName("mBank")
        .fullName("mBank S.A.")
        .bankDetails(
            List.of(
                BankDetailResponseDto.builder()
                    .bin("415046")
                    .bankCode("315046")
                    .currency(Currency.PLN)
                    .build(),
                BankDetailResponseDto.builder()
                    .bin("554895")
                    .bankCode("354895")
                    .currency(Currency.PLN)
                    .build()))
        .build();
  }

  public static AtmEntity getValidAtmEntity() {
    return AtmEntity.builder()
        .id(7L)
        .atmName("ATM 303")
        .atmOperationModes(List.of())
        .city("Gdansk")
        .address("Osiedle Teatralne 12")
        .latitude("54.36972467413277")
        .longitude("18.662791082066043")
        .build();
  }

  public static BankBranchEntity getValidBankBranchEntity() {
    return BankBranchEntity.builder()
        .id(7L)
        .bankBranchName("Bank Branch (VII)")
        .bankBranchOperationModes(List.of())
        .city("Gdansk")
        .address("CH Alfa Centrum, ul. Kołobrzeska 41b")
        .latitude("54.404509724163105")
        .longitude("18.588073885763823")
        .build();
  }

  public static BankContactsEntity getValidBankContactsEntity() {
    BankContactsEntity bankContacts =
        BankContactsEntity.builder()
            .id(1L)
            .bankName("Bank Etalon")
            .nip("800-000-56-78")
            .regon("922042241")
            .swiftCode("WBKXXLPP")
            .email("info@etalonbank.pl")
            .phoneNumber("+48228110000")
            .build();

    BankAddressTranslationsEntity polishTranslation =
        BankAddressTranslationsEntity.builder()
            .id(1L)
            .locale("pl")
            .value("ul. Jana Pawła, 10, 00-854")
            .bankContacts(bankContacts)
            .build();

    bankContacts.setBankAddressTranslations(List.of(polishTranslation));

    return bankContacts;
  }

  public static List<ExchangeRateResponseDto> getValidListExchangeRates() {
    return List.of(
        new ExchangeRateResponseDto(
            CurrencyName.USD,
            CurrencyName.USD.getId(),
            BigDecimal.valueOf(0.2205),
            BigDecimal.valueOf(0.2295),
            ZonedDateTime.now(ZoneId.of("UTC"))),
        new ExchangeRateResponseDto(
            CurrencyName.EUR,
            CurrencyName.EUR.getId(),
            BigDecimal.valueOf(0.2450).setScale(4, RoundingMode.CEILING),
            BigDecimal.valueOf(0.2550).setScale(4, RoundingMode.CEILING),
            ZonedDateTime.now(ZoneId.of("UTC"))),
        new ExchangeRateResponseDto(
            CurrencyName.GBP,
            CurrencyName.GBP.getId(),
            BigDecimal.valueOf(0.2450).setScale(4, RoundingMode.CEILING),
            BigDecimal.valueOf(0.2550).setScale(4, RoundingMode.CEILING),
            ZonedDateTime.now(ZoneId.of("UTC"))),
        new ExchangeRateResponseDto(
            CurrencyName.CHF,
            CurrencyName.CHF.getId(),
            BigDecimal.valueOf(0.2328),
            BigDecimal.valueOf(0.2423),
            ZonedDateTime.now(ZoneId.of("UTC"))));
  }

  public static List<ExchangeRateResponseDto> getValidListExchangeRatesUsdAsBaseCurrency() {
    return List.of(
        new ExchangeRateResponseDto(
            CurrencyName.PLN,
            CurrencyName.PLN.getId(),
            BigDecimal.valueOf(3.8460),
            BigDecimal.valueOf(4.0030),
            ZonedDateTime.now(ZoneId.of("UTC"))),
        new ExchangeRateResponseDto(
            CurrencyName.EUR,
            CurrencyName.EUR.getId(),
            BigDecimal.valueOf(0.9039).setScale(4, RoundingMode.CEILING),
            BigDecimal.valueOf(0.9407).setScale(4, RoundingMode.CEILING),
            ZonedDateTime.now(ZoneId.of("UTC"))),
        new ExchangeRateResponseDto(
            CurrencyName.GBP,
            CurrencyName.GBP.getId(),
            BigDecimal.valueOf(0.7689).setScale(4, RoundingMode.CEILING),
            BigDecimal.valueOf(0.8003).setScale(4, RoundingMode.CEILING),
            ZonedDateTime.now(ZoneId.of("UTC"))),
        new ExchangeRateResponseDto(
            CurrencyName.CHF,
            CurrencyName.CHF.getId(),
            BigDecimal.valueOf(0.8968),
            BigDecimal.valueOf(0.9434),
            ZonedDateTime.now(ZoneId.of("UTC"))));
  }

  public static DateTimeResponseDto getCurrentDate() {
    return new DateTimeResponseDto(
        ZonedDateTime.now(ZoneId.of("UTC")).truncatedTo(ChronoUnit.MINUTES));
  }

  public static TransactionStatementPdfRequestDto getValidTransactionStatementPdf() {
    return TransactionStatementPdfRequestDto.builder()
        .firstName("Bob")
        .lastName("Martin")
        .transactionDate("04.10.2023")
        .transactionTime("08:19:15")
        .transactionName("open_new_deposit")
        .outcomeAccountNumber("PL46234567840000000000000056")
        .incomeAccountNumber("PL27234567840000000000000172")
        .transactionAmount(BigDecimal.valueOf(999.7))
        .type(Type.INCOME)
        .status(TransactionStatus.APPROVED)
        .build();
  }

  public static UserDataResponseDto getValidUserDataResponseDto() {
    return UserDataResponseDto.builder()
        .id("1")
        .email("trans@gmail.com")
        .firstName("Bob")
        .lastName("Martin")
        .pesel("123345564766578")
        .createAt(ZonedDateTime.now())
        .updateAt(ZonedDateTime.now())
        .build();
  }

  public static TransactionDetailedResponseDto getValidOutcomeTransactionDetailedResponseDto() {
    return TransactionDetailedResponseDto.builder()
        .id(2L)
        .transactionDate("04.10.2023")
        .transactionTime("08:20:00")
        .transactionName("Top up long-deposit")
        .outcomeAccountNumber("PL46234567840000000000000003")
        .incomeAccountNumber("PL27234567840000000000000002")
        .transactionAmount(BigDecimal.valueOf(234))
        .type(Type.OUTCOME)
        .status(TransactionStatus.APPROVED)
        .currency(CurrencyName.PLN)
        .build();
  }

  public static TransactionDetailedResponseDto getValidIncomeTransactionDetailedResponseDto() {
    return TransactionDetailedResponseDto.builder()
        .id(1L)
        .transactionDate("04.10.2023")
        .transactionTime("08:19:15")
        .transactionName("open_new_deposit")
        .outcomeAccountNumber("PL46234567840000000000000056")
        .incomeAccountNumber("PL27234567840000000000000172")
        .transactionAmount(BigDecimal.valueOf(999.7))
        .type(Type.INCOME)
        .status(TransactionStatus.APPROVED)
        .currency(CurrencyName.PLN)
        .build();
  }

  public static TransactionDetailedResponseDto getValidFirstTransactionDetailedResponseDto() {
    return TransactionDetailedResponseDto.builder()
        .id(2L)
        .transactionDate("11.09.2024")
        .transactionTime("06:00:00")
        .transactionName("Payment for Warsaw department Property tax")
        .outcomeAccountNumber("PL48234567840000000000000014")
        .incomeAccountNumber("PL48234567840000000000000013")
        .transactionAmount(BigDecimal.valueOf(-1300))
        .type(Type.OUTCOME)
        .status(TransactionStatus.APPROVED)
        .currency(CurrencyName.PLN)
        .build();
  }

  public static TransactionDetailedResponseDto getValidSecondTransactionDetailedResponseDto() {
    return TransactionDetailedResponseDto.builder()
        .id(2L)
        .transactionDate("11.09.2024")
        .transactionTime("06:00:00")
        .transactionName("Payment for Krakow department Income tax")
        .outcomeAccountNumber("PL48234567840000000000000014")
        .incomeAccountNumber("PL48234567840000000000000013")
        .transactionAmount(BigDecimal.valueOf(-1100))
        .type(Type.OUTCOME)
        .status(TransactionStatus.DECLINED)
        .currency(CurrencyName.PLN)
        .build();
  }

  public static TransactionFilter getValidTransactionFilter() {
    return TransactionFilter.builder()
        .startDate("2023-10-19T11:00:00Z")
        .endDate("2023-10-19T11:00:00Z")
        .orderBy("ASC")
        .sortBy("createAt")
        .type(Type.INCOME.name())
        .build();
  }

  public static List<EventResponseDto> getValidEventResponseDtoList() {
    return List.of(
        EventResponseDto.builder()
            .id(1L)
            .type("INCOME")
            .amount(BigDecimal.TEN)
            .status("APPROVED")
            .createAt("2023-10-19T11:00:00Z")
            .name("transaction")
            .accountNumber("PL1")
            .build());
  }

  public static TransferReceiptContext getTransferReceiptContextDto() {
    return new TransferReceiptContext(
        "1",
        "Martha Kowalska",
        "Martha K.",
        "PL58109010140000071219812873",
        "PL57109010140000071219812874",
        ZonedDateTime.parse("2024-02-26T17:47:04.868853Z"),
        "Etalon Bank",
        "Money Transfer",
        BigDecimal.valueOf(1200.00).setScale(2, RoundingMode.UP),
        CurrencyName.EUR);
  }

  public static TransferResponseDto getValidTransferResponseDto() {
    return TransferResponseDto.builder()
        .id(1L)
        .description("Money Transfer")
        .totalAmount(BigDecimal.valueOf(1200.00).setScale(2, RoundingMode.UP))
        .amount(BigDecimal.valueOf(1200.00).setScale(2, RoundingMode.UP))
        .fee(BigDecimal.valueOf(0))
        .senderAccountNumber("PL58109010140000071219812873")
        .beneficiaryAccountNumber("PL57109010140000071219812874")
        .beneficiaryFullName("Martha K.")
        .senderFullName("Martha Kowalska")
        .beneficiaryBank("Etalon Bank")
        .status(TransferStatus.PROCESSING)
        .createAt(ZonedDateTime.parse("2024-02-26T17:47:04.868853Z"))
        .updateAt(ZonedDateTime.parse("2024-02-26T17:47:04.868853Z"))
        .currency(CurrencyName.EUR)
        .build();
  }

  public static ConfirmationRequestDto getValidConfirmationRequestDto() {
    return new ConfirmationRequestDto("123456");
  }

  public static CreateConfirmationRequestDto getValidConfirmationRequestRequestDto() {
    return new CreateConfirmationRequestDto(123L, Operation.OPEN_DEPOSIT, ConfirmationMethod.EMAIL);
  }

  public static ConfirmationStatusRequestDto getRegistrationStatusBlockedRequestDto() {
    return ConfirmationStatusRequestDto.builder().targetId(5L).isRegistration(true).build();
  }

  public static AccountDetailedResponseDto getValidAccountDetailedResponseDto() {
    return AccountDetailedResponseDto.builder()
        .id(1L)
        .iban("")
        .userId("userId")
        .balance(new BigDecimal(100))
        .isBlocked(false)
        .currency(Currency.USD)
        .build();
  }

  public static final ConfirmationEmailRequestDto createEmailRequest() {
    ConfirmationEmailRequestDto emailRequest = new ConfirmationEmailRequestDto();
    emailRequest.setToEmail(EMAIL_TO);
    emailRequest.setType(EmailType.CONFIRMATION);
    emailRequest.setVerificationCode("Your test confirmation code is: 1234");
    return emailRequest;
  }

  public static final SendRawEmailRequestDto createExpectedMessage() {
    return SendRawEmailRequestDto.builder()
        .to(EMAIL_TO)
        .subject("Confirmation code")
        .text("Your test confirmation code is: 12345")
        .from(EMAIL_FROM)
        .build();
  }

  public static CountryCodesResponseDto getCountryCodesResponseDtoPl() {
    return CountryCodesResponseDto.builder()
        .id(1L)
        .countryName("Kanada")
        .phoneCode("1")
        .imageUrl("Canada.svg")
        .build();
  }

  public static CountryCodesResponseDto getCountryCodesResponseDtoDefault() {
    return CountryCodesResponseDto.builder()
        .id(131L)
        .countryName("Poland")
        .phoneCode("48")
        .imageUrl("Poland.svg")
        .build();
  }

  public static Map<String, String> createConfirmationHeaders() {
    Map<String, String> headers = new HashMap<>();
    headers.put(Constants.AUTHENTICATED_USER_ID, "32be2810-eb77-49f7-9b9e-667a8fe1be07");
    return headers;
  }

  public static CreateConfirmationMessage createConfirmationMessage() {
    return new CreateConfirmationMessage(1L);
  }

  public static BaseEmailRequestDto baseEmailRequestDto() {
    BaseEmailRequestDto baseEmailRequestDto = new BaseEmailRequestDto();
    baseEmailRequestDto.setToEmail(EMAIL_TO);
    baseEmailRequestDto.setSubject("Confirmation code");
    baseEmailRequestDto.setType(EmailType.CONFIRMATION);
    return baseEmailRequestDto;
  }
}

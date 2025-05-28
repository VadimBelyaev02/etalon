package com.andersenlab.etalon.cardservice;

import com.andersenlab.etalon.cardservice.dto.account.request.AccountCreationRequestDto;
import com.andersenlab.etalon.cardservice.dto.account.response.AccountDetailedResponseDto;
import com.andersenlab.etalon.cardservice.dto.account.response.AccountResponseDto;
import com.andersenlab.etalon.cardservice.dto.card.request.CardCreationRequestDto;
import com.andersenlab.etalon.cardservice.dto.card.request.CardDetailsRequestDto;
import com.andersenlab.etalon.cardservice.dto.card.request.ChangeCardStatusRequestDto;
import com.andersenlab.etalon.cardservice.dto.card.request.RequestFilterDto;
import com.andersenlab.etalon.cardservice.dto.card.response.CardBlockingReasonResponseDto;
import com.andersenlab.etalon.cardservice.dto.card.response.CardDetailedResponseDto;
import com.andersenlab.etalon.cardservice.dto.card.response.CardProductResponseDto;
import com.andersenlab.etalon.cardservice.dto.card.response.CardResponseDto;
import com.andersenlab.etalon.cardservice.dto.card.response.ShortCardInfoDto;
import com.andersenlab.etalon.cardservice.dto.info.response.BankBranchesResponseDto;
import com.andersenlab.etalon.cardservice.dto.user.response.UserDataResponseDto;
import com.andersenlab.etalon.cardservice.entity.CardEntity;
import com.andersenlab.etalon.cardservice.entity.CardProductEntity;
import com.andersenlab.etalon.cardservice.entity.CurrencyEntity;
import com.andersenlab.etalon.cardservice.entity.CurrencyLimitEntity;
import com.andersenlab.etalon.cardservice.util.enums.CardBlockingReason;
import com.andersenlab.etalon.cardservice.util.enums.CardStatus;
import com.andersenlab.etalon.cardservice.util.enums.Currency;
import com.andersenlab.etalon.cardservice.util.enums.Issuer;
import com.andersenlab.etalon.cardservice.util.enums.ProductType;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MockData {

  public static CardEntity getValidCardEntity() {
    return CardEntity.builder()
        .id(1L)
        .userId("user")
        .expirationDate(ZonedDateTime.parse("2026-06-16T14:26:52Z"))
        .number("4246700000000019")
        .isBlocked(false)
        .blockingReason(null)
        .status(CardStatus.ACTIVE)
        .accountNumber("PL04234567840000000000000001")
        .cardholderName("ROBERT SMITH")
        .cvv(123)
        .withdrawLimit(BigDecimal.valueOf(2000.5))
        .transferLimit(BigDecimal.valueOf(1000))
        .dailyExpenseLimit(BigDecimal.valueOf(1000))
        .product(getValidCardProductEntity())
        .bankBranchId(1L)
        .build();
  }

  public static CardEntity getValidCardCreatedEntity() {
    return CardEntity.builder()
        .id(1L)
        .userId("user")
        .expirationDate(ZonedDateTime.parse("2024-06-16T14:26:52Z"))
        .number("4246700000000019")
        .isBlocked(false)
        .blockingReason(null)
        .status(CardStatus.ACTIVE)
        .accountNumber("PL04234567840000000000000001")
        .cardholderName("ROBERT SMITH")
        .cvv(123)
        .withdrawLimit(BigDecimal.valueOf(2000.5))
        .transferLimit(BigDecimal.valueOf(1000))
        .dailyExpenseLimit(BigDecimal.valueOf(1000))
        .product(getValidCardProductEntity())
        .bankBranchId(1L)
        .build();
  }

  public static CardProductEntity getValidCardProductEntity() {
    return CardProductEntity.builder()
        .id(4L)
        .productType(ProductType.DEBIT)
        .name("Enjoy")
        .issuer(Issuer.VISA)
        .availableCurrencies(
            List.of(
                CurrencyEntity.builder()
                    .id(1L)
                    .currencyCode(Currency.PLN)
                    .currencyName("Polish Zlotych")
                    .build()))
        .validity(2)
        .issuanceFee(BigDecimal.valueOf(0))
        .maintenanceFee(BigDecimal.valueOf(200))
        .apr(null)
        .cashback(BigDecimal.valueOf(0))
        .build();
  }

  public static CardResponseDto getValidCardResponseDto() {
    return CardResponseDto.builder()
        .id(1L)
        .expirationDate(ZonedDateTime.parse("2026-06-16T14:26:52Z"))
        .issuer(Issuer.VISA)
        .productType(ProductType.DEBIT)
        .productName("Enjoy")
        .number("4246700000000019")
        .accounts(Map.of("PL04234567840000000000000001", Currency.PLN))
        .balance(BigDecimal.valueOf(2.14))
        .currency(Currency.PLN)
        .isBlocked(false)
        .status(CardStatus.ACTIVE)
        .cardholderName("ROBERT SMITH")
        .accountNumber("PL04234567840000000000000001")
        .withdrawLimit(BigDecimal.valueOf(2000.5))
        .transferLimit(BigDecimal.valueOf(1000))
        .dailyExpenseLimit(BigDecimal.valueOf(1000))
        .build();
  }

  public static CardDetailedResponseDto getValidCardDetailedResponseDto() {
    return CardDetailedResponseDto.builder()
        .id(1L)
        .expirationDate(ZonedDateTime.parse("2026-06-16T14:26:52Z"))
        .number("4246700000000019")
        .productName("Enjoy")
        .productType(ProductType.DEBIT)
        .accountNumber("PL04234567840000000000000001")
        .accounts(Map.of("PL04234567840000000000000001", Currency.PLN))
        .balance(BigDecimal.ZERO)
        .currency(Currency.PLN)
        .issuer(Issuer.VISA)
        .isBlocked(false)
        .status(CardStatus.ACTIVE)
        .cardholderName("ROBERT SMITH")
        .cvv(123)
        .withdrawLimit(BigDecimal.valueOf(2000.5))
        .transferLimit(BigDecimal.valueOf(1000))
        .dailyExpenseLimit(BigDecimal.valueOf(1000))
        .bankBranchId(1L)
        .build();
  }

  public static ShortCardInfoDto getValidShortCardInfoDto() {
    return ShortCardInfoDto.builder()
        .id(1L)
        .cardProductName("Enjoy")
        .maskedCardNumber("*0019")
        .build();
  }

  public static CardProductResponseDto getValidCardProductResponseDto() {
    return CardProductResponseDto.builder()
        .id(4L)
        .productType(ProductType.DEBIT)
        .name("Enjoy")
        .issuer(Issuer.VISA)
        .availableCurrencies(List.of(Currency.PLN))
        .validity(2)
        .issuanceFee(BigDecimal.valueOf(0))
        .maintenanceFee(BigDecimal.valueOf(200))
        .apr(null)
        .cashback(BigDecimal.valueOf(0))
        .build();
  }

  public static List<CardBlockingReasonResponseDto> getValidListCardBlockingReasonResponseDto() {
    List<CardBlockingReasonResponseDto> reasons = new ArrayList<>();

    Arrays.stream(CardBlockingReason.values())
        .toList()
        .forEach(
            reasonInfo ->
                reasons.add(
                    CardBlockingReasonResponseDto.builder()
                        .id(reasonInfo.getId())
                        .reason(reasonInfo.name())
                        .description(reasonInfo.getReason())
                        .build()));
    return reasons;
  }

  public static AccountResponseDto getValidAccountResponseDto() {
    return AccountResponseDto.builder()
        .id(1L)
        .iban("PL04234567840000000000000001")
        .balance(BigDecimal.ZERO)
        .currency(Currency.PLN)
        .expirationDate(ZonedDateTime.now())
        .isBlocked(false)
        .status(CardStatus.ACTIVE)
        .build();
  }

  public static UserDataResponseDto getValidUserDataResponseDto() {
    return UserDataResponseDto.builder().id("user").firstName("ROBERT").lastName("SMITH").build();
  }

  public static BankBranchesResponseDto getValidBankBranchesResponseDto() {
    return BankBranchesResponseDto.builder()
        .id(1L)
        .name("Bank Branch (I)")
        .city("Warsaw")
        .address("Broniewskiego 56A")
        .build();
  }

  public static RequestFilterDto getValidRequestFilterDto() {
    return RequestFilterDto.builder()
        .issuer(Issuer.VISA)
        .productType(ProductType.DEBIT)
        .status(CardStatus.ACTIVE)
        .build();
  }

  public static ChangeCardStatusRequestDto getValidBlockChangeCardStatusRequestDto() {
    return ChangeCardStatusRequestDto.builder()
        .id(1L)
        .blockingReason(CardBlockingReason.DAMAGED.name())
        .build();
  }

  public static CardDetailsRequestDto getValidCardDetailsRequestDto() {
    return CardDetailsRequestDto.builder()
        .withdrawLimit(BigDecimal.valueOf(2000.5))
        .transferLimit(BigDecimal.valueOf(1000))
        .dailyExpenseLimit(BigDecimal.valueOf(1000))
        .build();
  }

  public static CardCreationRequestDto getValidCardCreationRequestDto() {
    return CardCreationRequestDto.builder()
        .cardProductId(1L)
        .bankBranchId(1L)
        .currencies(List.of(Currency.PLN))
        .build();
  }

  public static AccountCreationRequestDto getValidAccountCreationRequestDto() {
    return AccountCreationRequestDto.builder()
        .userId("user")
        .type("CARD")
        .currency(Currency.PLN)
        .build();
  }

  public static AccountDetailedResponseDto getValidAccountDetailedResponseDto() {
    return AccountDetailedResponseDto.builder()
        .iban("PL04234567840000000000000001")
        .currency(Currency.PLN)
        .balance(BigDecimal.ZERO)
        .build();
  }

  public static CurrencyLimitEntity getDefaultCurrencyLimitEntity(Currency currency) {
    return switch (currency) {
      case USD, EUR -> new CurrencyLimitEntity(
          currency, BigDecimal.valueOf(1000), BigDecimal.valueOf(500), BigDecimal.valueOf(500));
      case PLN -> new CurrencyLimitEntity(
          currency, BigDecimal.valueOf(2000.5), BigDecimal.valueOf(2000), BigDecimal.valueOf(1000));
    };
  }
}

package com.andersenlab.etalon.accountservice;

import com.andersenlab.etalon.accountservice.dto.account.request.AccountCreationRequestDto;
import com.andersenlab.etalon.accountservice.dto.account.request.AccountRequestDto;
import com.andersenlab.etalon.accountservice.dto.account.response.AccountDetailedResponseDto;
import com.andersenlab.etalon.accountservice.dto.account.response.AccountResponseDto;
import com.andersenlab.etalon.accountservice.dto.user.response.UserDataResponseDto;
import com.andersenlab.etalon.accountservice.entity.AccountEntity;
import com.andersenlab.etalon.accountservice.util.enums.AccountStatus;
import com.andersenlab.etalon.accountservice.util.enums.Currency;
import com.andersenlab.etalon.accountservice.util.enums.Type;
import java.math.BigDecimal;

public class MockData {

  public static AccountResponseDto getValidAccountResponseDto() {
    return AccountResponseDto.builder()
        .id(1L)
        .iban("PL19234567848944295640629627")
        .balance(BigDecimal.ZERO)
        .currency(Currency.PLN)
        .isBlocked(false)
        .status(AccountStatus.ACTIVE)
        .accountType(Type.CARD)
        .build();
  }

  public static AccountEntity getValidAccountEntity() {
    return new AccountEntity()
        .toBuilder()
            .id(2L)
            .userId("user")
            .iban("PL04234567840000000000000001")
            .balance(BigDecimal.ZERO)
            .currency(Currency.PLN)
            .isBlocked(false)
            .status(AccountStatus.ACTIVE)
            .accountType(Type.CARD)
            .build();
  }

  public static UserDataResponseDto getValidUserDataResponseDto() {
    return new UserDataResponseDto("user", "Mark", "Kowalski");
  }

  public static AccountEntity getValidAccountEntityWithPositiveBalance() {
    return new AccountEntity()
        .toBuilder()
            .id(2L)
            .userId("user")
            .iban("PL04234567840000000000000003")
            .balance(BigDecimal.valueOf(5000.00))
            .currency(Currency.PLN)
            .isBlocked(false)
            .status(AccountStatus.ACTIVE)
            .accountType(Type.CARD)
            .build();
  }

  public static AccountDetailedResponseDto getValidAccountDetailedResponseDto() {
    return AccountDetailedResponseDto.builder()
        .id(2L)
        .userId("user")
        .iban("PL04234567840000000000000001")
        .isBlocked(false)
        .balance(BigDecimal.valueOf(0))
        .build();
  }

  public static AccountRequestDto getValidAccountRequestDto() {
    return AccountRequestDto.builder().balance(BigDecimal.valueOf(5000.55)).build();
  }

  public static AccountCreationRequestDto getValidAccountCreationRequestDto() {
    return AccountCreationRequestDto.builder().userId("user").type("CARD").build();
  }

  public static AccountCreationRequestDto getValidAccountCreationWithCurrencyRequestDto() {
    return AccountCreationRequestDto.builder()
        .userId("user")
        .type("CARD")
        .currency(Currency.PLN)
        .build();
  }
}

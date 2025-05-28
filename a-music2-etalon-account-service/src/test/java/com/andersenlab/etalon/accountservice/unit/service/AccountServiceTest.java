package com.andersenlab.etalon.accountservice.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.andersenlab.etalon.accountservice.MockData;
import com.andersenlab.etalon.accountservice.dto.account.request.AccountCreationRequestDto;
import com.andersenlab.etalon.accountservice.dto.account.request.AccountReplenishByAccountNumberRequestDto;
import com.andersenlab.etalon.accountservice.dto.account.request.AccountRequestDto;
import com.andersenlab.etalon.accountservice.dto.account.request.AccountWithdrawByAccountNumberRequestDto;
import com.andersenlab.etalon.accountservice.dto.account.response.AccountBalanceResponseDto;
import com.andersenlab.etalon.accountservice.dto.account.response.AccountCurrencyResponseDto;
import com.andersenlab.etalon.accountservice.dto.account.response.AccountDetailedResponseDto;
import com.andersenlab.etalon.accountservice.dto.account.response.AccountResponseDto;
import com.andersenlab.etalon.accountservice.dto.user.response.UserDataResponseDto;
import com.andersenlab.etalon.accountservice.entity.AccountEntity;
import com.andersenlab.etalon.accountservice.exception.BusinessExceptionQ;
import com.andersenlab.etalon.accountservice.mapper.AccountMapper;
import com.andersenlab.etalon.accountservice.repository.AccountRepository;
import com.andersenlab.etalon.accountservice.service.GeneratorService;
import com.andersenlab.etalon.accountservice.service.impl.AccountServiceImpl;
import com.andersenlab.etalon.accountservice.util.enums.AccountStatus;
import com.andersenlab.etalon.accountservice.util.enums.Currency;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
  private static final Long ACCOUNT_ID = 2L;
  private static final Long INVALID_ACCOUNT_ID = -1L;

  public static final String ACCOUNT_NUMBER_ZERO_BALANCE = "PL04234567840000000000000001";
  public static final String ACCOUNT_NUMBER_WITH_POSITIVE_BALANCE = "PL04234567840000000000000003";
  public static final String INVALID_ACCOUNT_NUMBER = "PL04234567840000000000000009";
  public static final String USER_ID = "user";

  private AccountRequestDto accountRequestDto;
  private AccountEntity accountEntity;
  private AccountResponseDto accountResponseDto;
  private AccountCreationRequestDto accountCreationRequestDto;
  private AccountCreationRequestDto accountCreationWithCurrencyRequestDto;
  private AccountDetailedResponseDto accountDetailedResponseDto;
  private AccountEntity accountWithPositiveBalance;

  @Mock private AccountRepository accountRepository;
  @Mock private GeneratorService generator;
  @Spy private AccountMapper mapper = Mappers.getMapper(AccountMapper.class);
  @InjectMocks private AccountServiceImpl underTest;

  @BeforeEach
  void setUp() {
    accountEntity = MockData.getValidAccountEntity();
    accountRequestDto = MockData.getValidAccountRequestDto();
    accountResponseDto = MockData.getValidAccountResponseDto();
    accountCreationRequestDto = MockData.getValidAccountCreationRequestDto();
    accountCreationWithCurrencyRequestDto =
        MockData.getValidAccountCreationWithCurrencyRequestDto();
    accountDetailedResponseDto = MockData.getValidAccountDetailedResponseDto();
    accountWithPositiveBalance = MockData.getValidAccountEntityWithPositiveBalance();
  }

  @Test
  void whenCreateNewAccount_thenSuccess() {
    // given
    when(accountRepository.findFirstByOrderByIdDesc()).thenReturn(Optional.empty());
    when(accountRepository.save(any(AccountEntity.class))).thenReturn(accountEntity);

    // when
    final AccountResponseDto result = underTest.createAccount(accountCreationRequestDto);

    // then
    assertEquals(accountResponseDto.balance(), result.balance());
    assertEquals(accountResponseDto.currency(), result.currency());
    assertEquals(accountResponseDto.isBlocked(), result.isBlocked());
    assertEquals(accountResponseDto.status(), result.status());
    assertEquals(accountResponseDto.accountType(), result.accountType());
  }

  @Test
  void whenCreateNewAccountWithNotNullAccountCreationCurrency_thenSuccess() {
    // given
    when(accountRepository.findFirstByOrderByIdDesc()).thenReturn(Optional.empty());
    when(accountRepository.save(any(AccountEntity.class))).thenReturn(accountEntity);

    // when
    final AccountResponseDto result =
        underTest.createAccount(accountCreationWithCurrencyRequestDto);

    // then
    assertEquals(accountResponseDto.balance(), result.balance());
    assertEquals(accountResponseDto.currency(), result.currency());
    assertEquals(accountResponseDto.isBlocked(), result.isBlocked());
    assertEquals(accountResponseDto.status(), result.status());
    assertEquals(accountResponseDto.accountType(), result.accountType());
    assertEquals(accountResponseDto.currency(), result.currency());
  }

  @Test
  void whenUpdateAccount_shouldSuccess() {
    // given
    when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.ofNullable(accountEntity));
    when(accountRepository.save(any(AccountEntity.class))).thenReturn(accountEntity);

    // when/then
    Assertions.assertDoesNotThrow(() -> underTest.updateAccount(ACCOUNT_ID, accountRequestDto));
    verify(accountRepository, times(1)).findById(ACCOUNT_ID);
    verify(accountRepository, times(1)).save(any(AccountEntity.class));
  }

  @Test
  void whenUpdateAccount_shouldFail() {
    when(accountRepository.findById(INVALID_ACCOUNT_ID)).thenReturn(Optional.empty());

    assertThrows(
        BusinessExceptionQ.class,
        () -> underTest.updateAccount(INVALID_ACCOUNT_ID, accountRequestDto));
  }

  @Test
  void whenGetDetailedAccountInfoWithValidAccountNumber_shouldSuccess() {
    // given
    when(accountRepository.findAccountEntityByIbanAndUserId(ACCOUNT_NUMBER_ZERO_BALANCE, USER_ID))
        .thenReturn(Optional.ofNullable(accountEntity));

    // when
    AccountDetailedResponseDto actualResult =
        underTest.getDetailedAccountInfo(ACCOUNT_NUMBER_ZERO_BALANCE, USER_ID);

    // then
    assertNotNull(actualResult);
    assertEquals(accountDetailedResponseDto.userId(), actualResult.userId());
    assertEquals(accountDetailedResponseDto.isBlocked(), actualResult.isBlocked());
    assertEquals(accountDetailedResponseDto.iban(), actualResult.iban());
  }

  @Test
  void whenGetDetailedAccountInfoWithInvalidAccountNumber_shouldFail() {
    when(accountRepository.findAccountEntityByIbanAndUserId(INVALID_ACCOUNT_NUMBER, USER_ID))
        .thenReturn(Optional.empty());

    assertThrows(
        BusinessExceptionQ.class,
        () -> underTest.getDetailedAccountInfo(INVALID_ACCOUNT_NUMBER, USER_ID));
  }

  @Test
  void whenGetAccountBalance_shouldSuccess() {
    // given
    BigDecimal expectedBalance = accountWithPositiveBalance.getBalance();
    when(accountRepository.findAccountEntityByIbanAndUserId(
            ACCOUNT_NUMBER_WITH_POSITIVE_BALANCE, USER_ID))
        .thenReturn(Optional.ofNullable(accountWithPositiveBalance));

    // then
    AccountBalanceResponseDto accountBalanceResponseDto =
        underTest.getAccountBalance(ACCOUNT_NUMBER_WITH_POSITIVE_BALANCE, USER_ID);
    assertEquals(expectedBalance, accountBalanceResponseDto.accountBalance());
  }

  @Test
  void whenGetAccountBalance_shouldFail() {
    when(accountRepository.findAccountEntityByIbanAndUserId(INVALID_ACCOUNT_NUMBER, USER_ID))
        .thenReturn(Optional.empty());

    assertThrows(
        BusinessExceptionQ.class,
        () -> underTest.getAccountBalance(INVALID_ACCOUNT_NUMBER, USER_ID));
  }

  @Test
  void whenReplenishAccountBalanceById_shouldSuccess() {
    // given
    AccountReplenishByAccountNumberRequestDto accountReplenishByIdRequestDto =
        new AccountReplenishByAccountNumberRequestDto(BigDecimal.valueOf(5000.55));
    when(accountRepository.findAccountEntityByIban(ACCOUNT_NUMBER_ZERO_BALANCE))
        .thenReturn(Optional.ofNullable(accountEntity));
    when(accountRepository.save(accountEntity)).thenReturn(accountEntity);

    // when/then
    Assertions.assertDoesNotThrow(
        () ->
            underTest.replenishAccountBalance(
                ACCOUNT_NUMBER_ZERO_BALANCE, accountReplenishByIdRequestDto));
    verify(accountRepository, times(1)).findAccountEntityByIban(ACCOUNT_NUMBER_ZERO_BALANCE);
    verify(accountRepository, times(1)).save(accountEntity);
  }

  @Test
  void whenReplenishAccountBalanceById_shouldThrow() {
    // given
    when(accountRepository.findAccountEntityByIban(INVALID_ACCOUNT_NUMBER))
        .thenReturn(Optional.empty());
    // when/then
    var requestDto = new AccountReplenishByAccountNumberRequestDto(BigDecimal.TEN);
    assertThrows(
        BusinessExceptionQ.class,
        () -> underTest.replenishAccountBalance(INVALID_ACCOUNT_NUMBER, requestDto));
  }

  @Test
  void whenWithdrawAccountBalanceById_shouldSuccess() {
    // given
    AccountWithdrawByAccountNumberRequestDto accountWithdrawByIdRequestDto =
        new AccountWithdrawByAccountNumberRequestDto(BigDecimal.valueOf(500.0));
    when(accountRepository.findAccountEntityByIban(ACCOUNT_NUMBER_WITH_POSITIVE_BALANCE))
        .thenReturn(Optional.ofNullable(accountWithPositiveBalance));
    when(accountRepository.save(accountWithPositiveBalance)).thenReturn(accountWithPositiveBalance);
    // when
    Assertions.assertDoesNotThrow(
        () ->
            underTest.withdrawAccountBalance(
                ACCOUNT_NUMBER_WITH_POSITIVE_BALANCE, accountWithdrawByIdRequestDto));
    verify(accountRepository, times(1))
        .findAccountEntityByIban(ACCOUNT_NUMBER_WITH_POSITIVE_BALANCE);
    verify(accountRepository, times(1)).save(accountWithPositiveBalance);
  }

  @Test
  void whenWithdrawAccountBalanceByIdWithInvalidAccountId_shouldThrow() {
    when(accountRepository.findAccountEntityByIban(INVALID_ACCOUNT_NUMBER))
        .thenReturn(Optional.empty());

    var requestDto = new AccountWithdrawByAccountNumberRequestDto(BigDecimal.TEN);
    assertThrows(
        BusinessExceptionQ.class,
        () -> underTest.withdrawAccountBalance(INVALID_ACCOUNT_NUMBER, requestDto));
  }

  @Test
  void whenWithdrawAccountBalanceByIdWithIncorrectAmount_shouldThrow() {
    when(accountRepository.findAccountEntityByIban(ACCOUNT_NUMBER_ZERO_BALANCE))
        .thenReturn(Optional.ofNullable(accountEntity));
    BigDecimal ten = BigDecimal.TEN;
    var requestDto = new AccountWithdrawByAccountNumberRequestDto(ten);

    assertThrows(
        BusinessExceptionQ.class,
        () -> underTest.withdrawAccountBalance(ACCOUNT_NUMBER_ZERO_BALANCE, requestDto));
  }

  @Test
  void whenGetActiveAccounts_shouldSuccess() {
    // given
    List<AccountEntity> accountEntities = List.of(MockData.getValidAccountEntity());
    // when
    List<AccountCurrencyResponseDto> activeAccounts = underTest.getActiveAccounts(accountEntities);
    // then
    assertEquals(accountEntities.size(), activeAccounts.size());
    assertEquals(ACCOUNT_NUMBER_ZERO_BALANCE, activeAccounts.get(0).accountNumber());
    assertEquals(Currency.PLN, activeAccounts.get(0).currency());
  }

  @Test
  void whenGetActiveAccounts_shouldThrowBusinessException() {
    // given
    List<AccountEntity> accountEntities =
        List.of(MockData.getValidAccountEntity().toBuilder().status(AccountStatus.EXPIRED).build());
    // when, then
    assertThrows(BusinessExceptionQ.class, () -> underTest.getActiveAccounts(accountEntities));
  }

  @Test
  void whenGetUserFullName_shouldSuccess() {
    // given
    UserDataResponseDto userData = MockData.getValidUserDataResponseDto();
    // when
    String fullName = userData.getFullName();
    // then
    assertEquals(fullName, String.format("%s %s", userData.firstName(), userData.lastName()));
  }
}

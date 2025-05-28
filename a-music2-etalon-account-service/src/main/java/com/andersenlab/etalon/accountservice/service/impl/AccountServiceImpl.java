package com.andersenlab.etalon.accountservice.service.impl;

import com.andersenlab.etalon.accountservice.client.CardServiceClient;
import com.andersenlab.etalon.accountservice.client.UserServiceClient;
import com.andersenlab.etalon.accountservice.dto.account.request.AccountCreationRequestDto;
import com.andersenlab.etalon.accountservice.dto.account.request.AccountReplenishByAccountNumberRequestDto;
import com.andersenlab.etalon.accountservice.dto.account.request.AccountRequestDto;
import com.andersenlab.etalon.accountservice.dto.account.request.AccountWithdrawByAccountNumberRequestDto;
import com.andersenlab.etalon.accountservice.dto.account.request.RequestOptionDto;
import com.andersenlab.etalon.accountservice.dto.account.response.AccountBalanceResponseDto;
import com.andersenlab.etalon.accountservice.dto.account.response.AccountCurrencyResponseDto;
import com.andersenlab.etalon.accountservice.dto.account.response.AccountDetailedResponseDto;
import com.andersenlab.etalon.accountservice.dto.account.response.AccountInfoResponseDto;
import com.andersenlab.etalon.accountservice.dto.account.response.AccountInterestResponseDto;
import com.andersenlab.etalon.accountservice.dto.account.response.AccountNumberResponseDto;
import com.andersenlab.etalon.accountservice.dto.account.response.AccountResponseDto;
import com.andersenlab.etalon.accountservice.dto.card.response.CardDetailedResponseDto;
import com.andersenlab.etalon.accountservice.dto.user.response.UserDataResponseDto;
import com.andersenlab.etalon.accountservice.entity.AccountEntity;
import com.andersenlab.etalon.accountservice.exception.BusinessExceptionQ;
import com.andersenlab.etalon.accountservice.mapper.AccountMapper;
import com.andersenlab.etalon.accountservice.repository.AccountRepository;
import com.andersenlab.etalon.accountservice.service.AccountService;
import com.andersenlab.etalon.accountservice.service.GeneratorService;
import com.andersenlab.etalon.accountservice.util.PatchUtils;
import com.andersenlab.etalon.accountservice.util.enums.AccountStatus;
import com.andersenlab.etalon.accountservice.util.enums.Currency;
import com.andersenlab.etalon.accountservice.util.enums.Type;
import com.andersenlab.etalon.exceptionhandler.exception.BusinessException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

  private final AccountRepository accountRepository;
  private final GeneratorService generator;
  private final AccountMapper mapper;
  private final UserServiceClient userServiceClient;
  private final CardServiceClient cardServiceClient;

  @Override
  public AccountResponseDto createAccount(AccountCreationRequestDto accountCreation) {

    Long accountId = getAccountId();
    AccountEntity newAccount =
        new AccountEntity()
            .toBuilder()
                .userId(accountCreation.userId())
                .iban(generator.generateIban(accountId))
                .balance(BigDecimal.ZERO)
                .isBlocked(false)
                .status(AccountStatus.ACTIVE)
                .accountType(Type.valueOf(accountCreation.type()))
                .build();

    if (Objects.isNull(accountCreation.currency())) {
      newAccount.setCurrency(Currency.PLN);
    } else {
      newAccount.setCurrency(accountCreation.currency());
    }

    return mapper.toDto(accountRepository.save(newAccount));
  }

  @Override
  public void updateAccount(Long id, AccountRequestDto accountRequestDto) {
    AccountEntity accountEntity =
        accountRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new BusinessExceptionQ(
                        HttpStatus.NOT_FOUND,
                        BusinessExceptionQ.NOT_FOUND_ACCOUNT_BY_ID.formatted(id)));

    PatchUtils.updateIfPresent(accountEntity::setBalance, accountRequestDto.balance());

    accountRepository.save(accountEntity);
  }

  @Override
  public void deleteAccount(String accountNumber, String userId) {
    AccountEntity accountEntity =
        accountRepository
            .findAccountEntityByIbanAndUserId(accountNumber, userId)
            .orElseThrow(
                () ->
                    new BusinessExceptionQ(
                        HttpStatus.NOT_FOUND,
                        BusinessExceptionQ.NOT_FOUND_ACCOUNT_BY_IBAN_AND_USER_ID.formatted(
                            accountNumber, userId)));
    if (accountEntity.getBalance().compareTo(BigDecimal.ZERO) != 0) {
      throw new BusinessExceptionQ(
          HttpStatus.CONFLICT, BusinessExceptionQ.ACCOUNT_DELETION_RESTRICTION_MESSAGE);
    }
    accountRepository.delete(accountEntity);
    log.info(
        "{deleteAccount} -> Account with accountNumber {} was successfully deleted", accountNumber);
  }

  @Override
  public AccountBalanceResponseDto getAccountBalance(String accountNumber, String userId) {
    AccountEntity accountEntity =
        accountRepository
            .findAccountEntityByIbanAndUserId(accountNumber, userId)
            .orElseThrow(
                () ->
                    new BusinessExceptionQ(
                        HttpStatus.NOT_FOUND,
                        BusinessExceptionQ.NOT_FOUND_ACCOUNT_BY_IBAN_AND_USER_ID.formatted(
                            accountNumber, userId)));

    return new AccountBalanceResponseDto(accountEntity.getBalance());
  }

  @Override
  public AccountDetailedResponseDto getDetailedAccountInfo(String accountNumber, String userId) {
    AccountEntity accountEntity =
        accountRepository
            .findAccountEntityByIbanAndUserId(accountNumber, userId)
            .orElseThrow(
                () ->
                    new BusinessExceptionQ(
                        HttpStatus.NOT_FOUND,
                        BusinessExceptionQ.NOT_FOUND_ACCOUNT_BY_IBAN_AND_USER_ID.formatted(
                            accountNumber, userId)));

    return mapper.toDetailedDto(accountEntity);
  }

  @Override
  public AccountDetailedResponseDto getDetailedAccountInfo(String accountNumber) {
    AccountEntity accountEntity =
        accountRepository
            .findAccountEntityByIban(accountNumber)
            .orElseThrow(
                () ->
                    new BusinessExceptionQ(
                        HttpStatus.NOT_FOUND,
                        BusinessExceptionQ.NOT_FOUND_ACCOUNT_BY_ACCOUNT_NUMBER.formatted(
                            accountNumber)));

    return mapper.toDetailedDto(accountEntity);
  }

  @Override
  public void replenishAccountBalance(
      String accountNumber,
      AccountReplenishByAccountNumberRequestDto accountReplenishByIdRequestDto) {
    AccountEntity replenishedAccount =
        accountRepository
            .findAccountEntityByIban(accountNumber)
            .orElseThrow(
                () ->
                    new BusinessExceptionQ(
                        HttpStatus.NOT_FOUND,
                        BusinessExceptionQ.NOT_FOUND_ACCOUNT_BY_ACCOUNT_NUMBER.formatted(
                            accountNumber)));
    BigDecimal oldBalance = replenishedAccount.getBalance();
    BigDecimal replenishAmount = accountReplenishByIdRequestDto.replenishAmount();
    replenishedAccount.setBalance(oldBalance.add(replenishAmount));

    accountRepository.save(replenishedAccount);
  }

  @Override
  public void withdrawAccountBalance(
      String accountNumber,
      AccountWithdrawByAccountNumberRequestDto accountWithdrawByIdRequestDto) {
    AccountEntity withdrawnAccount =
        accountRepository
            .findAccountEntityByIban(accountNumber)
            .orElseThrow(
                () ->
                    new BusinessExceptionQ(
                        HttpStatus.NOT_FOUND,
                        BusinessExceptionQ.NOT_FOUND_ACCOUNT_BY_ACCOUNT_NUMBER.formatted(
                            accountNumber)));

    BigDecimal withdrawnAccountOldBalance = withdrawnAccount.getBalance();
    BigDecimal withdrawAmount = accountWithdrawByIdRequestDto.withdrawAmount();

    if (withdrawnAccount.getBalance().compareTo(withdrawAmount) < 0) {
      throw new BusinessExceptionQ(
          HttpStatus.BAD_REQUEST,
          BusinessExceptionQ.NOT_ENOUGH_FUNDS_ON_ACCOUNT_WITH_ID.formatted(accountNumber));
    }
    withdrawnAccount.setBalance(withdrawnAccountOldBalance.subtract(withdrawAmount));
    accountRepository.save(withdrawnAccount);
  }

  @Override
  public List<AccountNumberResponseDto> getAllAccountNumbers(String userId) {
    throw new BusinessException("dc");
    //    return accountRepository.findAllByUserId(userId).stream()
    //        .map(a -> new AccountNumberResponseDto(a.getIban()))
    //        .toList();
  }

  @Override
  public AccountInfoResponseDto getAccountInfoBySelectedOption(RequestOptionDto options) {
    if (Objects.nonNull(options.accountNumber())) {
      return getAccountInfoByAccountNumber(options.accountNumber());
    }
    if (Objects.nonNull(options.cardNumber())) {
      return getAccountInfoByCardNumber(options.cardNumber());
    }
    if (Objects.nonNull(options.phoneNumber())) {
      return getAccountInfoByPhoneNumber(options.phoneNumber());
    }
    return new AccountInfoResponseDto(null, Collections.emptyList());
  }

  public AccountInfoResponseDto getAccountInfoByAccountNumber(String accountNumber) {
    log.info("{getAccountInfoByAccountNumber} -> Request to get account info by accountNumber");
    AccountEntity accountEntity =
        accountRepository
            .findAccountEntityByIban(accountNumber)
            .orElseThrow(
                () ->
                    new BusinessExceptionQ(
                        HttpStatus.NOT_FOUND,
                        String.format(
                            BusinessExceptionQ.NOT_FOUND_ACCOUNT_BY_ACCOUNT_NUMBER,
                            accountNumber)));

    List<AccountCurrencyResponseDto> activeAccounts = getActiveAccounts(List.of(accountEntity));
    UserDataResponseDto userData = userServiceClient.getUserData(accountEntity.getUserId());
    return new AccountInfoResponseDto(userData.getFullName(), activeAccounts);
  }

  public AccountInfoResponseDto getAccountInfoByPhoneNumber(String phoneNumber) {
    log.info("{getAccountInfoByPhoneNumber} -> Request to get account info by phoneNumber");
    UserDataResponseDto userData = userServiceClient.getUserDataByPhoneNumber(phoneNumber);
    List<AccountEntity> accountEntities = accountRepository.findAllByUserId(userData.id());
    List<AccountCurrencyResponseDto> activeAccounts = getActiveAccounts(accountEntities);
    return new AccountInfoResponseDto(userData.getFullName(), activeAccounts);
  }

  public AccountInfoResponseDto getAccountInfoByCardNumber(String cardNumber) {
    log.info("{getAccountInfoByCardNumber} -> Request to get account info by cardNumber");
    CardDetailedResponseDto cardDetailedResponseDto =
        cardServiceClient.getUserCardByNumber(cardNumber);
    List<String> accountNumbers = new ArrayList<>(cardDetailedResponseDto.accounts().keySet());
    List<AccountEntity> accountEntities = accountRepository.findAllByIbanIn(accountNumbers);
    List<AccountCurrencyResponseDto> activeAccounts = getActiveAccounts(accountEntities);
    return new AccountInfoResponseDto(cardDetailedResponseDto.cardholderName(), activeAccounts);
  }

  public List<AccountCurrencyResponseDto> getActiveAccounts(List<AccountEntity> accountEntities) {
    List<AccountCurrencyResponseDto> activeAccounts =
        accountEntities.stream()
            .filter(accountEntity -> accountEntity.getStatus() == AccountStatus.ACTIVE)
            .map(
                accountEntity ->
                    new AccountCurrencyResponseDto(
                        accountEntity.getId(),
                        accountEntity.getIban(),
                        accountEntity.getCurrency()))
            .toList();

    if (activeAccounts.isEmpty()) {
      throw new BusinessExceptionQ(HttpStatus.BAD_REQUEST, BusinessExceptionQ.NO_ACTIVE_ACCOUNTS);
    }
    return activeAccounts;
  }

  @Override
  public List<AccountInterestResponseDto> getAccountsBalances(
      List<String> accountsNumbers, String userId) {
    List<AccountInterestResponseDto> accountsBalances =
        accountRepository.findAllByIbanInAndUserId(accountsNumbers, userId).stream()
            .map(mapper::toInterestDto)
            .toList();
    if (accountsBalances.isEmpty()) {
      throw new BusinessExceptionQ(
          HttpStatus.NOT_FOUND, BusinessExceptionQ.NOT_FOUND_ACCOUNTS_BY_USER_ID.formatted(userId));
    }
    return accountsBalances;
  }

  @Override
  public Boolean changeIsBlocked(String accountNumber, String userId) {
    AccountEntity accountEntity =
        accountRepository
            .findAccountEntityByIbanAndUserId(accountNumber, userId)
            .orElseThrow(
                () ->
                    new BusinessExceptionQ(
                        HttpStatus.NOT_FOUND,
                        BusinessExceptionQ.NOT_FOUND_ACCOUNT_BY_IBAN_AND_USER_ID.formatted(
                            accountNumber, userId)));
    if (Boolean.TRUE.equals(accountEntity.getIsBlocked())) {
      accountEntity.setIsBlocked(Boolean.FALSE);
    } else {
      accountEntity.setIsBlocked(Boolean.TRUE);
    }
    accountRepository.save(accountEntity);
    return true;
  }

  private Long getAccountId() {
    return accountRepository.findFirstByOrderByIdDesc().map(AccountEntity::getId).orElse(null);
  }
}

package com.andersenlab.etalon.depositservice.service.business.impl;

import static com.andersenlab.etalon.depositservice.exception.BusinessException.DEPOSIT_BALANCE_NOT_FOUND;
import static com.andersenlab.etalon.depositservice.util.DepositUtils.calculateMonthlyIncome;

import com.andersenlab.etalon.depositservice.config.TimeProvider;
import com.andersenlab.etalon.depositservice.dto.account.response.AccountBalanceResponseDto;
import com.andersenlab.etalon.depositservice.dto.account.response.AccountInterestResponseDto;
import com.andersenlab.etalon.depositservice.dto.deposit.response.DepositToCloseResponseDto;
import com.andersenlab.etalon.depositservice.dto.deposit.response.DepositWithInterestResponseDto;
import com.andersenlab.etalon.depositservice.dto.transaction.response.EventResponseDto;
import com.andersenlab.etalon.depositservice.entity.DepositEntity;
import com.andersenlab.etalon.depositservice.entity.DepositInterestEntity;
import com.andersenlab.etalon.depositservice.exception.BusinessException;
import com.andersenlab.etalon.depositservice.mapper.DepositMapper;
import com.andersenlab.etalon.depositservice.service.business.DepositSchedulerService;
import com.andersenlab.etalon.depositservice.service.dao.DepositInterestServiceDao;
import com.andersenlab.etalon.depositservice.service.dao.DepositServiceDao;
import com.andersenlab.etalon.depositservice.service.facade.ExternalServiceFacade;
import com.andersenlab.etalon.depositservice.util.DepositConstants;
import com.andersenlab.etalon.depositservice.util.DepositUtils;
import com.andersenlab.etalon.depositservice.util.enums.DepositStatus;
import com.andersenlab.etalon.depositservice.util.enums.Details;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepositSchedulerServiceImpl implements DepositSchedulerService {
  private final ExternalServiceFacade externalServiceFacade;

  private final DepositServiceDao depositServiceDao;
  private final DepositInterestServiceDao depositInterestServiceDao;
  private final DepositMapper depositMapper;
  private final TimeProvider timeProvider;

  @Override
  @Transactional
  @Scheduled(cron = "${deposit.interest.calculation.scheduler}", zone = "${app.default.timezone}")
  public void calculateAndSaveDepositInterest() {
    final ZonedDateTime now = timeProvider.getCurrentZonedDateTime();

    List<DepositEntity> depositEntities = getActiveDeposits(now);
    if (depositEntities.isEmpty()) {
      log.info("No active deposits found for interest calculation");
      return;
    }

    List<DepositWithInterestResponseDto> depositWithInterestDtoList =
        mapToDepositWithInterestDtoList(depositEntities);

    List<AccountInterestResponseDto> accountInterestResponseDtoList =
        findAccountBalancesForActiveDeposits(depositWithInterestDtoList);
    if (accountInterestResponseDtoList.isEmpty()) {
      log.error("No account balances found for active deposits");
      return;
    }

    List<EventResponseDto> eventResponseDtoList =
        getTransactionsForAccounts(accountInterestResponseDtoList);

    List<DepositInterestEntity> depositInterestEntities =
        createDepositInterestEntities(
            depositWithInterestDtoList, accountInterestResponseDtoList, eventResponseDtoList);
    saveDepositInterestEntities(depositInterestEntities);
  }

  @Override
  @Transactional
  @Scheduled(cron = "${deposit.interest.transfer.scheduler}", zone = "${app.default.timezone}")
  public void calculateAndTransferMonthlyInterest() {
    final ZonedDateTime now = timeProvider.getCurrentZonedDateTime();

    List<DepositEntity> depositsToPay = getDepositsToPay(now);
    if (depositsToPay.isEmpty()) {
      log.info("No deposits to pay found");
      return;
    }

    List<DepositInterestEntity> depositInterestEntities =
        getDepositInterestEntities(depositsToPay, now);
    if (depositInterestEntities.isEmpty()) {
      log.info("No interest found for deposits");
      return;
    }

    depositsToPay = filterDepositsWithInterest(depositsToPay, depositInterestEntities);

    calculateAndCreateTransactions(depositsToPay, depositInterestEntities);
  }

  @Override
  @Transactional
  @Scheduled(cron = "${deposit.withdrawal.scheduler}", zone = "${app.default.timezone}")
  public void withdrawDepositsAfterExpiration() {
    ZonedDateTime now = timeProvider.getCurrentZonedDateTime();

    List<DepositEntity> expiredDeposits = findAndExpireDeposits(now);
    if (expiredDeposits.isEmpty()) {
      return;
    }

    Set<AccountInterestResponseDto> accountInterests = fetchAccountsWithBalances(expiredDeposits);
    if (accountInterests.isEmpty()) {
      return;
    }

    List<DepositToCloseResponseDto> depositsToClose =
        mapToDepositsToClose(expiredDeposits, accountInterests);
    processWithdrawals(depositsToClose);
    depositServiceDao.saveAll(expiredDeposits);
  }

  private List<DepositEntity> getActiveDeposits(ZonedDateTime now) {
    return getFilteredDeposits(
        deposit ->
            !deposit
                .getCreateAt()
                .truncatedTo(ChronoUnit.DAYS)
                .isEqual(now.truncatedTo(ChronoUnit.DAYS)));
  }

  private List<DepositWithInterestResponseDto> mapToDepositWithInterestDtoList(
      List<DepositEntity> depositEntities) {
    return depositEntities.stream().map(depositMapper::toDepositWithInterestResponseDto).toList();
  }

  private List<AccountInterestResponseDto> findAccountBalancesForActiveDeposits(
      List<DepositWithInterestResponseDto> depositWithInterestResponseDtoList) {
    return externalServiceFacade
        .getAccountsBalances(
            depositWithInterestResponseDtoList.stream()
                .map(DepositWithInterestResponseDto::accountNumber)
                .toList())
        .stream()
        .filter(account -> account.balance().compareTo(BigDecimal.ZERO) > 0)
        .toList();
  }

  private List<EventResponseDto> getTransactionsForAccounts(
      List<AccountInterestResponseDto> accountInterestResponseDtoList) {
    return externalServiceFacade.getAllTransactionsForAccounts(
        accountInterestResponseDtoList.stream()
            .map(AccountInterestResponseDto::accountNumber)
            .toList());
  }

  private List<DepositInterestEntity> createDepositInterestEntities(
      List<DepositWithInterestResponseDto> depositWithInterestResponseDtoList,
      List<AccountInterestResponseDto> accountInterestResponseDtoList,
      List<EventResponseDto> eventResponseDtoList) {
    return depositWithInterestResponseDtoList.stream()
        .map(
            d ->
                createDepositInterestEntity(
                    d, accountInterestResponseDtoList, eventResponseDtoList))
        .toList();
  }

  private void saveDepositInterestEntities(List<DepositInterestEntity> depositInterestEntities) {
    depositInterestServiceDao.saveAllInterests(depositInterestEntities);
  }

  private List<DepositEntity> getDepositsToPay(ZonedDateTime now) {
    return getFilteredDeposits(d -> d.getCreateAt().isAfter(now.minusMonths(1).plusDays(1)));
  }

  private List<DepositInterestEntity> getDepositInterestEntities(
      List<DepositEntity> depositsToPay, ZonedDateTime now) {
    return depositInterestServiceDao.findAllInterestsByDepositIdInAndCreateAtGreaterThanEqual(
        depositsToPay.stream().map(DepositEntity::getId).toList(),
        DepositUtils.subtractMonth(now.truncatedTo(ChronoUnit.DAYS)));
  }

  private void calculateAndCreateTransactions(
      List<DepositEntity> depositsToPay, List<DepositInterestEntity> depositInterestEntities) {
    calculateMonthlyIncome(depositsToPay, depositInterestEntities)
        .forEach(externalServiceFacade::createTransaction);
  }

  private List<DepositEntity> findAndExpireDeposits(ZonedDateTime now) {
    List<DepositEntity> depositsToExpire =
        depositServiceDao.findAllByStatusNot(DepositStatus.EXPIRED).stream()
            .filter(deposit -> DepositUtils.isDepositExpired(deposit, now))
            .toList();
    depositsToExpire.forEach(deposit -> deposit.setStatus(DepositStatus.EXPIRED));
    return depositsToExpire;
  }

  private Set<AccountInterestResponseDto> fetchAccountsWithBalances(
      List<DepositEntity> expiredDeposits) {
    List<AccountInterestResponseDto> accountInterests =
        externalServiceFacade
            .getAccountsBalances(
                expiredDeposits.stream().map(DepositEntity::getAccountNumber).toList())
            .stream()
            .filter(account -> account.balance().compareTo(BigDecimal.ZERO) > 0)
            .toList();
    if (accountInterests.isEmpty()) {
      log.info("No balances found for the expired deposits.");
      return Collections.emptySet();
    }
    return new HashSet<>(accountInterests);
  }

  private List<DepositToCloseResponseDto> mapToDepositsToClose(
      List<DepositEntity> expiredDeposits, Set<AccountInterestResponseDto> accountInterests) {
    return expiredDeposits.stream()
        .map(depositMapper::toDepositToCloseResponseDto)
        .map(dto -> enrichWithAccountBalance(dto, accountInterests))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .toList();
  }

  private void processWithdrawals(List<DepositToCloseResponseDto> depositsToClose) {
    depositsToClose.forEach(
        deposit ->
            externalServiceFacade.createTransactionForDeposit(
                deposit.finalTransferAccountNumber(),
                deposit.balance(),
                deposit.accountNumber(),
                String.format(
                    DepositConstants.WITHDRAW_DEPOSIT_TRANSACTION_TEXT,
                    deposit.depositProductName()),
                Details.WITHDRAW_DEPOSIT));
  }

  private List<DepositEntity> filterDepositsWithInterest(
      List<DepositEntity> depositsToPay, List<DepositInterestEntity> depositInterestEntities) {
    Set<Long> depositIds =
        depositInterestEntities.stream()
            .map(DepositInterestEntity::getDepositId)
            .collect(Collectors.toSet());

    return depositsToPay.stream().filter(d -> depositIds.contains(d.getId())).toList();
  }

  private List<DepositEntity> getFilteredDeposits(Predicate<DepositEntity> filter) {
    return depositServiceDao
        .findAllByExample(
            Example.of(
                DepositEntity.builder().status(DepositStatus.ACTIVE).build(),
                ExampleMatcher.matchingAny()))
        .stream()
        .filter(filter)
        .toList();
  }

  private List<EventResponseDto> getEvents(
      List<EventResponseDto> eventResponseDtoList, DepositWithInterestResponseDto d) {
    return eventResponseDtoList.stream()
        .filter(x -> x.accountNumber().equals(d.accountNumber()))
        .toList();
  }

  private BigDecimal getBalance(
      List<AccountInterestResponseDto> accountInterestResponseDtoList,
      DepositWithInterestResponseDto d) {
    return accountInterestResponseDtoList.stream()
        .filter(b -> b.accountNumber().equals(d.accountNumber()))
        .map(AccountInterestResponseDto::balance)
        .findFirst()
        .orElseThrow(
            () ->
                new BusinessException(
                    HttpStatus.NOT_FOUND, String.format(DEPOSIT_BALANCE_NOT_FOUND, d.id())));
  }

  private DepositInterestEntity createDepositInterestEntity(
      DepositWithInterestResponseDto depositDto,
      List<AccountInterestResponseDto> accountInterestResponseDtoList,
      List<EventResponseDto> eventResponseDtoList) {
    BigDecimal balance = getBalance(accountInterestResponseDtoList, depositDto);
    List<EventResponseDto> events = getEvents(eventResponseDtoList, depositDto);

    return DepositInterestEntity.builder()
        .depositId(depositDto.id())
        .interestAmount(
            DepositUtils.calculateDailyDepositIncome(
                events,
                depositDto,
                AccountBalanceResponseDto.builder().accountBalance(balance).build()))
        .build();
  }

  private Optional<DepositToCloseResponseDto> enrichWithAccountBalance(
      DepositToCloseResponseDto dto, Set<AccountInterestResponseDto> accountInterests) {
    return accountInterests.stream()
        .filter(account -> account.accountNumber().equals(dto.accountNumber()))
        .findFirst()
        .map(account -> dto.toBuilder().balance(account.balance()).build());
  }
}

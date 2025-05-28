package com.andersenlab.etalon.loanservice.service.impl;

import static com.andersenlab.etalon.loanservice.util.Constants.MONTHS_IN_YEAR;
import static com.andersenlab.etalon.loanservice.util.LoanUtils.calculateLoanPayments;

import com.andersenlab.etalon.loanservice.config.TimeProvider;
import com.andersenlab.etalon.loanservice.dto.calculation.LoanCalculationInitialData;
import com.andersenlab.etalon.loanservice.entity.LoanOrderEntity;
import com.andersenlab.etalon.loanservice.entity.LoanProductEntity;
import com.andersenlab.etalon.loanservice.exception.BusinessException;
import com.andersenlab.etalon.loanservice.repository.LoanOrderRepository;
import com.andersenlab.etalon.loanservice.repository.LoanProductRepository;
import com.andersenlab.etalon.loanservice.service.SchedulerService;
import com.andersenlab.etalon.loanservice.util.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SchedulerServiceImpl implements SchedulerService {

  private final LoanOrderRepository loanOrderRepository;
  private final LoanProductRepository loanProductRepository;
  private final TimeProvider timeProvider;

  @Scheduled(fixedDelay = 60000, zone = "${app.default.timezone}")
  public void approveLoans() {
    loanOrderRepository
        .findAllByStatus(OrderStatus.IN_REVIEW)
        .ifPresent(
            loanOrders -> {
              List<LoanOrderEntity> loanOrdersToUpdate =
                  loanOrders.stream().filter(isLoanOlderThanOneMinute()).toList();

              loanOrdersToUpdate.forEach(
                  loanOrder -> {
                    loanOrder.setStatus(checkLoanOrder(loanOrder));
                    loanOrderRepository.save(loanOrder);
                    log.info(
                        "Loan order with ID: {} has been {}",
                        loanOrder.getId(),
                        loanOrder.getStatus());
                  });
            });
  }

  public OrderStatus checkLoanOrder(final LoanOrderEntity loanOrder) {
    LoanProductEntity loanProduct =
        loanProductRepository
            .findById(loanOrder.getProduct().getId())
            .orElseThrow(
                () ->
                    new BusinessException(
                        HttpStatus.NOT_FOUND,
                        String.format(
                            BusinessException.LOAN_PRODUCT_NOT_FOUND_BY_ID,
                            loanOrder.getProduct().getId())));

    final Integer monthsDuration = loanProduct.getDuration() * MONTHS_IN_YEAR;
    final BigDecimal freeCash =
        loanOrder.getAverageMonthlySalary().subtract(loanOrder.getAverageMonthlyExpenses());
    final BigDecimal nextPaymentAmount =
        calculateLoanPayments(
                LoanCalculationInitialData.builder()
                    .amount(loanOrder.getAmount())
                    .monthlyCommission(loanProduct.getMonthlyCommission())
                    .apr(loanProduct.getApr())
                    .duration(monthsDuration)
                    .createAt(ZonedDateTime.now())
                    .build(),
                BigDecimal.ZERO,
                timeProvider.getCurrentZonedDateTime())
            .loanDebtGrossAmount();

    return freeCash.compareTo(nextPaymentAmount) < 0 ? OrderStatus.REJECTED : OrderStatus.APPROVED;
  }

  public static Predicate<LoanOrderEntity> isLoanOlderThanOneMinute() {
    return loan -> Duration.between(loan.getCreateAt(), ZonedDateTime.now()).toMinutes() >= 1;
  }
}

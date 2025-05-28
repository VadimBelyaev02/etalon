package com.andersenlab.etalon.loanservice.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.andersenlab.etalon.loanservice.MockData;
import com.andersenlab.etalon.loanservice.config.TimeProvider;
import com.andersenlab.etalon.loanservice.entity.LoanOrderEntity;
import com.andersenlab.etalon.loanservice.entity.LoanProductEntity;
import com.andersenlab.etalon.loanservice.repository.LoanOrderRepository;
import com.andersenlab.etalon.loanservice.repository.LoanProductRepository;
import com.andersenlab.etalon.loanservice.service.impl.SchedulerServiceImpl;
import com.andersenlab.etalon.loanservice.util.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SchedulerServiceImplTest {
  @Mock private LoanOrderRepository loanOrderRepository;
  @Mock private LoanProductRepository loanProductRepository;
  @Mock private TimeProvider timeProvider;
  @InjectMocks private SchedulerServiceImpl underTest;

  private LoanOrderEntity loanOrderEntity;
  private LoanProductEntity loanProductEntity;

  private static final Long LOAN_PRODUCT_ID = 2L;
  private static final Long LOAN_ORDER_ID = 2L;

  @BeforeEach
  void setUp() {
    loanOrderEntity = MockData.getValidLoanOrderEntity();
    loanProductEntity = MockData.getValidLoanProductEntity();
  }

  @Test
  void whenCheckLoanOrder_ThenApprove() {
    // given
    when(loanProductRepository.findById(LOAN_ORDER_ID)).thenReturn(Optional.of(loanProductEntity));
    when(timeProvider.getCurrentZonedDateTime()).thenReturn(ZonedDateTime.now());
    // when
    OrderStatus result = underTest.checkLoanOrder(loanOrderEntity);

    // then
    assertEquals(OrderStatus.APPROVED, result);
  }

  @Test
  void whenCheckLoanOrder_ThenReject() {
    // given
    loanOrderEntity.setAverageMonthlySalary(BigDecimal.valueOf(1000));
    when(loanProductRepository.findById(LOAN_ORDER_ID)).thenReturn(Optional.of(loanProductEntity));
    when(timeProvider.getCurrentZonedDateTime()).thenReturn(ZonedDateTime.now());

    // when
    OrderStatus result = underTest.checkLoanOrder(loanOrderEntity);

    // then
    assertEquals(OrderStatus.REJECTED, result);
  }

  @Test
  void testApproveLoans_WhenThereAreOrdersToApprove() {
    // given
    when(timeProvider.getCurrentZonedDateTime()).thenReturn(ZonedDateTime.now());
    LoanOrderEntity approvedOrder = MockData.getValidLoanOrderEntity();
    LoanOrderEntity rejectedOrder = MockData.getValidLoanOrderEntity();
    List<LoanOrderEntity> orders = List.of(approvedOrder, rejectedOrder);

    for (LoanOrderEntity order : orders) {
      order.setStatus(OrderStatus.IN_REVIEW);
      order.setCreateAt(timeProvider.getCurrentZonedDateTime().minusMinutes(2));
    }

    rejectedOrder.setAverageMonthlySalary(BigDecimal.valueOf(1000));

    when(loanOrderRepository.findAllByStatus(OrderStatus.IN_REVIEW))
        .thenReturn(Optional.of(orders));
    when(loanProductRepository.findById(LOAN_PRODUCT_ID))
        .thenReturn(Optional.of(loanProductEntity));

    // when
    underTest.approveLoans();

    // then
    verify(loanOrderRepository, times(2)).save(any(LoanOrderEntity.class));
    assertEquals(OrderStatus.APPROVED, approvedOrder.getStatus());
    assertEquals(OrderStatus.REJECTED, rejectedOrder.getStatus());
  }

  @Test
  void testApproveLoans_WhenNoOrdersToApprove() {
    // given
    when(loanOrderRepository.findAllByStatus(OrderStatus.IN_REVIEW)).thenReturn(Optional.empty());

    // when
    underTest.approveLoans();

    // then
    verify(loanOrderRepository, never()).save(any());
  }

  @Test
  void testIsLoanOlderThanOneMinute() {
    // given
    when(timeProvider.getCurrentZonedDateTime()).thenReturn(ZonedDateTime.now());
    LoanOrderEntity order1 = MockData.getValidLoanOrderEntity();
    LoanOrderEntity order2 = MockData.getValidLoanOrderEntity();
    LoanOrderEntity order3 = MockData.getValidLoanOrderEntity();
    order1.setCreateAt(timeProvider.getCurrentZonedDateTime());
    order2.setCreateAt(timeProvider.getCurrentZonedDateTime().minusMinutes(2));
    order3.setCreateAt(timeProvider.getCurrentZonedDateTime().minusMinutes(1));
    List<LoanOrderEntity> orders = List.of(order1, order2, order3);

    for (LoanOrderEntity order : orders) {
      order.setStatus(OrderStatus.IN_REVIEW);
    }

    // when
    Predicate<LoanOrderEntity> predicate = SchedulerServiceImpl.isLoanOlderThanOneMinute();

    // then
    assertFalse(predicate.test(order1));
    assertTrue(predicate.test(order2));
    assertTrue(predicate.test(order3));
  }
}

package com.andersenlab.etalon.depositservice.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.andersenlab.etalon.depositservice.MockData;
import com.andersenlab.etalon.depositservice.config.TimeProvider;
import com.andersenlab.etalon.depositservice.dto.account.response.AccountBalanceResponseDto;
import com.andersenlab.etalon.depositservice.dto.deposit.response.MonthlyInterestIncomeDto;
import com.andersenlab.etalon.depositservice.entity.DepositEntity;
import com.andersenlab.etalon.depositservice.util.DepositUtils;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DepositUtilsTest {

  @Mock private TimeProvider timeProvider;

  @Test
  void whenGetDepositInterestHistory_thenSuccess() {
    // given
    when(timeProvider.getZone()).thenReturn(ZoneId.of("UTC"));
    DepositEntity entity = MockData.getValidDepositEntity();
    entity.setCreateAt(ZonedDateTime.parse("2023-08-15T10:00:00Z"));
    // when
    List<MonthlyInterestIncomeDto> result =
        DepositUtils.getDepositInterestHistory(
            MockData.getValidDepositInterestDtoList(),
            MockData.getValidDepositEntity(),
            timeProvider.getZone());
    // then
    assertTrue(result.containsAll(MockData.getValidMonthlyPaymentDtoList()));
  }

  @Test
  void whenCalculateDailyDepositIncome_thenSuccess() {
    // given
    DepositEntity entity = MockData.getValidDepositEntity();
    entity.setCreateAt(ZonedDateTime.parse("2023-08-15T10:00:00Z"));
    // when
    BigDecimal result =
        DepositUtils.calculateDailyDepositIncome(
            MockData.getValidDepositEventResponseDtoList(),
            MockData.getValidDepositWithInterestResponseDto(),
            AccountBalanceResponseDto.builder().accountBalance(BigDecimal.valueOf(100000)).build());
    // then
    assertEquals(BigDecimal.valueOf(2.74), result);
  }
}

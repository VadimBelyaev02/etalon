package com.andersenlab.etalon.transactionservice.unit;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.andersenlab.etalon.transactionservice.dto.info.response.ExchangeRateResponseDto;
import com.andersenlab.etalon.transactionservice.exception.BusinessException;
import com.andersenlab.etalon.transactionservice.util.enums.CurrencyName;
import com.andersenlab.etalon.transactionservice.util.exchange_rate.ExchangeRateUtil;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ExchangeRateUtilTest {

  @Test
  void testFindRate_success() {
    List<ExchangeRateResponseDto> rates =
        List.of(
            new ExchangeRateResponseDto(
                CurrencyName.USD, 1L, BigDecimal.valueOf(0.98), BigDecimal.valueOf(1.02), null),
            new ExchangeRateResponseDto(
                CurrencyName.EUR, 2L, BigDecimal.valueOf(0.99), BigDecimal.valueOf(1.01), null),
            new ExchangeRateResponseDto(
                CurrencyName.PLN, 3L, BigDecimal.valueOf(4.20), BigDecimal.valueOf(4.25), null));

    BigDecimal result =
        ExchangeRateUtil.findRate(CurrencyName.EUR, rates, ExchangeRateResponseDto::sellingRate);
    Assertions.assertEquals(BigDecimal.valueOf(0.99), result);
  }

  @Test
  void testFindRate_notFound() {
    List<ExchangeRateResponseDto> rates =
        List.of(
            new ExchangeRateResponseDto(
                CurrencyName.USD, 1L, BigDecimal.valueOf(0.98), BigDecimal.valueOf(1.02), null));

    Exception exception =
        assertThrows(
            BusinessException.class,
            () ->
                ExchangeRateUtil.findRate(
                    CurrencyName.PLN, rates, ExchangeRateResponseDto::sellingRate));

    String expectedMessage = "Cannot find exchange rates for currency PLN";
    String actualMessage = exception.getMessage();
    Assertions.assertTrue(actualMessage.contains(expectedMessage));
  }
}

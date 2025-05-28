package com.andersenlab.etalon.infoservice.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.andersenlab.etalon.infoservice.MockData;
import com.andersenlab.etalon.infoservice.config.ExchangeCoefficientsProperties;
import com.andersenlab.etalon.infoservice.config.TimeProvider;
import com.andersenlab.etalon.infoservice.dto.response.ExchangeRateResponseDto;
import com.andersenlab.etalon.infoservice.service.impl.ExchangeRatesCalculationServiceImpl;
import com.andersenlab.etalon.infoservice.service.impl.ExchangeRatesRequestServiceImpl;
import com.andersenlab.etalon.infoservice.util.enums.CurrencyName;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExchangeRatesCalculationServiceImplTest {
  @Mock ExchangeCoefficientsProperties coefficientsProperties;
  @Mock TimeProvider timeProvider;
  @InjectMocks private ExchangeRatesCalculationServiceImpl underTest;
  @Mock private ExchangeRatesRequestServiceImpl exchangeRatesRequestService;

  @Test
  void whenRequestGetExchangeRates_thenReturnListExchangeRates() {
    // given
    List<ExchangeRateResponseDto> expected = MockData.getValidListExchangeRates();
    Map<String, Double> map = Map.of("USD", 0.9, "GBP", 1.0, "PLN", 4.0, "CHF", 0.95);
    when(timeProvider.getCurrentZonedDateTime()).thenReturn(ZonedDateTime.now());

    // when
    when(coefficientsProperties.getBuy()).thenReturn(new BigDecimal("0.98"));
    when(coefficientsProperties.getSell()).thenReturn(new BigDecimal("1.02"));
    when(exchangeRatesRequestService.getExchangeRates()).thenReturn(map);

    final List<ExchangeRateResponseDto> rates = underTest.getCurrencyRates(CurrencyName.PLN.name());

    // then
    assertEquals(expected.get(0).currencyName(), rates.get(0).currencyName());
    assertEquals(expected.get(0).sellingRate(), rates.get(0).sellingRate());
    assertEquals(expected.get(0).buyingRate(), rates.get(0).buyingRate());
    assertEquals(expected.get(1).currencyName(), rates.get(1).currencyName());
    assertEquals(expected.get(1).sellingRate(), rates.get(1).sellingRate());
    assertEquals(expected.get(1).buyingRate(), rates.get(1).buyingRate());
    assertEquals(expected.get(2).currencyName(), rates.get(2).currencyName());
    assertEquals(expected.get(2).sellingRate(), rates.get(2).sellingRate());
    assertEquals(expected.get(2).buyingRate(), rates.get(2).buyingRate());
    assertEquals(expected.get(3).currencyName(), rates.get(3).currencyName());
    assertEquals(expected.get(3).sellingRate(), rates.get(3).sellingRate());
    assertEquals(expected.get(3).buyingRate(), rates.get(3).buyingRate());
  }
}

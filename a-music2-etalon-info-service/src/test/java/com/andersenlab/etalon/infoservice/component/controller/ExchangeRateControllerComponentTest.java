package com.andersenlab.etalon.infoservice.component.controller;

import static com.andersenlab.etalon.infoservice.MockData.getValidListExchangeRates;
import static com.andersenlab.etalon.infoservice.MockData.getValidListExchangeRatesUsdAsBaseCurrency;
import static com.andersenlab.etalon.infoservice.controller.ExchangeRatesController.EXCHANGE_RATES_URL;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.infoservice.component.AbstractComponentTest;
import com.andersenlab.etalon.infoservice.dto.response.ExchangeRateResponseDto;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;

class ExchangeRateControllerComponentTest extends AbstractComponentTest {

  public static final String QUERY_PARAM_CURRENCY = "currency";
  public static final String BASE_CURRENCY = "USD";

  @Test
  void whenRequestGetExchangeRates_shouldReturnOkAndListExchangeRates() throws Exception {
    // given
    final List<ExchangeRateResponseDto> expected = getValidListExchangeRates();

    // when
    ResultActions result = mockMvc.perform(get(EXCHANGE_RATES_URL));

    // then

    assertAll(
        () ->
            result
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$[0].currencyName", is(expected.get(0).currencyName().name())))
                .andExpect(jsonPath("$[1].currencyName", is(expected.get(1).currencyName().name())))
                .andExpect(jsonPath("$[2].currencyName", is(expected.get(2).currencyName().name())))
                .andExpect(
                    jsonPath("$[3].currencyName", is(expected.get(3).currencyName().name()))));
  }

  @Test
  void whenRequestGetExchangeRates_shouldReturnOkAndListExchangeRatesExpectBaseCurrency()
      throws Exception {
    // given
    final List<ExchangeRateResponseDto> expected = getValidListExchangeRatesUsdAsBaseCurrency();

    // when
    ResultActions result =
        mockMvc.perform(get(EXCHANGE_RATES_URL).queryParam(QUERY_PARAM_CURRENCY, BASE_CURRENCY));

    // then
    assertAll(
        () ->
            result
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$[0].currencyName", is(expected.get(0).currencyName().name())))
                .andExpect(jsonPath("$[1].currencyName", is(expected.get(1).currencyName().name())))
                .andExpect(jsonPath("$[2].currencyName", is(expected.get(2).currencyName().name())))
                .andExpect(
                    jsonPath("$[3].currencyName", is(expected.get(3).currencyName().name()))));
  }
}

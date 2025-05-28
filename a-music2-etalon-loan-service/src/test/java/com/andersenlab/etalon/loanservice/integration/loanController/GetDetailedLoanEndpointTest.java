package com.andersenlab.etalon.loanservice.integration.loanController;

import static com.andersenlab.etalon.loanservice.controller.LoanController.LOANS_URL_WITH_LOAN_ID;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.loanservice.annotation.WithUserId;
import com.andersenlab.etalon.loanservice.config.TimeProvider;
import com.andersenlab.etalon.loanservice.exception.BusinessException;
import com.andersenlab.etalon.loanservice.integration.AbstractIntegrationTest;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;

public class GetDetailedLoanEndpointTest extends AbstractIntegrationTest {

  public static final String LOAN_ID = "1";
  public static final String NONEXISTENT_LOAN_ID = "10";
  public static final String USER_ID = "user";

  public static final DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

  @Mock private TimeProvider timeProvider;

  @Test
  @WithUserId(USER_ID)
  void whenGetDetailedLoanWithCorrectLoanId_thenSuccess() throws Exception {

    // given
    when(timeProvider.getCurrentZonedDateTime()).thenReturn(ZonedDateTime.now());
    mockMvc
        .perform(get(LOANS_URL_WITH_LOAN_ID, LOAN_ID))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.productName", is("Cash loan")))
        .andExpect(jsonPath("$.duration", is(2)))
        .andExpect(jsonPath("$.amount", is(6000)))
        .andExpect(jsonPath("$.debtNetAmount", is(5500.0)))
        .andExpect(jsonPath("$.contractNumber", is("CN00000000000001")))
        .andExpect(jsonPath("$.apr", is(12.83)))
        .andExpect(jsonPath("$.nextPaymentAmount", is(282.08)))
        .andExpect(
            jsonPath(
                "$.nextPaymentDate",
                is(
                    timeProvider
                        .getCurrentZonedDateTime()
                        .minus(4, ChronoUnit.DAYS)
                        .truncatedTo(ChronoUnit.DAYS)
                        .format(formatter))))
        .andExpect(jsonPath("$.status", is("ACTIVE")))
        .andExpect(jsonPath("$.accountNumber", is("PL04234567840000000000000001")));
  }

  @Test
  @WithUserId(USER_ID)
  void whenGetDetailedLoanWithIncorrectLoanId_ReturnNotFoundError() throws Exception {

    mockMvc
        .perform(get(LOANS_URL_WITH_LOAN_ID, NONEXISTENT_LOAN_ID))
        .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof BusinessException))
        .andExpect(
            result ->
                assertEquals(
                    "Cannot find loan with id 10", result.getResolvedException().getMessage()));
  }
}

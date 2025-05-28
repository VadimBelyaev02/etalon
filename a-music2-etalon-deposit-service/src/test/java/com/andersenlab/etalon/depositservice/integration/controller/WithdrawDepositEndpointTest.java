package com.andersenlab.etalon.depositservice.integration.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.depositservice.MockData;
import com.andersenlab.etalon.depositservice.controller.DepositController;
import com.andersenlab.etalon.depositservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.depositservice.dto.deposit.request.DepositWithdrawRequestDto;
import com.andersenlab.etalon.depositservice.integration.AbstractIntegrationTest;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.util.UriComponentsBuilder;

@Sql(
    scripts = {
      "classpath:data/deposit-products-initial-data.sql",
      "classpath:data/deposit-initial-data.sql",
      "classpath:data/deposit-interest-initial-data.sql"
    },
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class WithdrawDepositEndpointTest extends AbstractIntegrationTest {
  public static final Long DEPOSIT_ID = 1L;
  private DepositWithdrawRequestDto depositWithdrawRequestDto;
  public static final Long DEPOSIT_ID_WITH_INTEREST = 8L;

  @BeforeEach
  void setUp() {
    depositWithdrawRequestDto =
        MockData.getValidDepositWithdrawRequestDto().toBuilder()
            .withdrawAmount(BigDecimal.valueOf(9000.0))
            .build();
  }

  @Test
  void whenWithdrawDeposit_shouldReturnCorrectUserDeposit() throws Exception {

    mockMvc
        .perform(
            post(UriComponentsBuilder.fromPath(
                        DepositController.DEPOSITS_V1_URL
                            + "/"
                            + DEPOSIT_ID
                            + DepositController.WITHDRAWN_URI)
                    .toUriString())
                .header("authenticated-user-id", "2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    toJson(
                        depositWithdrawRequestDto.toBuilder()
                            .withdrawAmount(BigDecimal.valueOf(100))
                            .build())))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$.message", is(MessageResponseDto.DEPOSIT_WITHDRAWAL_SUCCESSFUL)));
  }

  @Test
  void whenWithdrawDepositWithFullWithdrawal_shouldReturnCorrectUserDeposit() throws Exception {
    depositWithdrawRequestDto =
        MockData.getValidDepositWithdrawRequestDto().toBuilder()
            .targetAccountNumber("PL04234567840000000000000003")
            .withdrawAmount(BigDecimal.valueOf(9000.0))
            .build();

    mockMvc
        .perform(
            post(UriComponentsBuilder.fromPath(
                        DepositController.DEPOSITS_V1_URL
                            + "/"
                            + DEPOSIT_ID_WITH_INTEREST
                            + DepositController.WITHDRAWN_URI)
                    .toUriString())
                .header("authenticated-user-id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(depositWithdrawRequestDto)))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$.message", is(MessageResponseDto.DEPOSIT_CLOSED_SUCCESSFULLY)));
  }
}

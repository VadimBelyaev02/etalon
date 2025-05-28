package com.andersenlab.etalon.transactionservice.integration.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.transactionservice.annotation.WithUserId;
import com.andersenlab.etalon.transactionservice.controller.ScheduledTransferController;
import com.andersenlab.etalon.transactionservice.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class ScheduledTransferControllerTest extends AbstractIntegrationTest {

  public static final String USER_ID_1 = "1";
  public static final String INVALID_USER_ID_2 = "2";
  public static final String USER_ID_3 = "3";

  @Test
  @WithUserId(INVALID_USER_ID_2)
  void whenGetScheduledTransferWithIncorrectUser_thenBadRequest() throws Exception {
    mockMvc
        .perform(
            get(ScheduledTransferController.SCHEDULED_TRANSFER_URL)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$.content", hasSize(0)))
        .andExpect(jsonPath("$.pageNumber").value(0))
        .andExpect(jsonPath("$.pageSize").value(20))
        .andExpect(jsonPath("$.totalElements").value(0));
  }

  @Test
  @WithUserId(USER_ID_1)
  void whenGetScheduledTransferWithCorrectUser_thenSuccessRequest() throws Exception {
    mockMvc
        .perform(
            get(ScheduledTransferController.SCHEDULED_TRANSFER_URL)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$.content", hasSize(4)))
        .andExpect(jsonPath("$.pageNumber").value(0))
        .andExpect(jsonPath("$.pageSize").value(20))
        .andExpect(jsonPath("$.totalElements").value(4));
  }

  @Test
  @WithUserId(USER_ID_1)
  void whenGetScheduledTransferWithPageSize_thenSuccessRequest() throws Exception {
    mockMvc
        .perform(
            get(ScheduledTransferController.SCHEDULED_TRANSFER_URL)
                .param("page", "1")
                .param("size", "2")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$.content", hasSize(2)))
        .andExpect(jsonPath("$.pageNumber").value(1))
        .andExpect(jsonPath("$.pageSize").value(2))
        .andExpect(jsonPath("$.totalElements").value(4));
  }

  @Test
  @WithUserId(USER_ID_3)
  void whenGetScheduledTransferWithCheckAllData_thenSuccessRequest() throws Exception {
    mockMvc
        .perform(
            get(ScheduledTransferController.SCHEDULED_TRANSFER_URL)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$.content[0].id").value(8))
        .andExpect(jsonPath("$.content[0].name").value("transaction7"))
        .andExpect(jsonPath("$.content[0].status").value("ACTIVE"))
        .andExpect(jsonPath("$.content[0].frequency").value("ONCE_A_QUARTER"))
        .andExpect(jsonPath("$.content[0].amount").value(701))
        .andExpect(jsonPath("$.content[0].currency").value("PLN"))
        .andExpect(
            jsonPath("$.content[0].senderAccountNumber").value("PL48234567840000000000000012"))
        .andExpect(jsonPath("$.content[0].startDate").value("2023-09-29"))
        .andExpect(jsonPath("$.content[0].endDate").value("2023-09-30"))
        .andExpect(jsonPath("$.content[0].nextTransferDate").value("2023-09-30"));
  }

  @Test
  @WithUserId(USER_ID_1)
  void whenGetScheduledTransferFilteredByActiveStatus_thenOnlyActiveReturned() throws Exception {
    mockMvc
        .perform(
            get(ScheduledTransferController.SCHEDULED_TRANSFER_URL)
                .param("status", "ACTIVE")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(3))
        .andExpect(jsonPath("$.content[0].status").value("ACTIVE"));
  }
}

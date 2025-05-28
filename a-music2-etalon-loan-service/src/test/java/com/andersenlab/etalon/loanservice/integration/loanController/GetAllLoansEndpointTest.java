package com.andersenlab.etalon.loanservice.integration.loanController;

import static com.andersenlab.etalon.loanservice.controller.LoanController.LOANS_URL;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.loanservice.MockData;
import com.andersenlab.etalon.loanservice.annotation.WithUserId;
import com.andersenlab.etalon.loanservice.dto.loan.response.LoanResponseDto;
import com.andersenlab.etalon.loanservice.integration.AbstractIntegrationTest;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class GetAllLoansEndpointTest extends AbstractIntegrationTest {

  public static final String USER_ID_WITH_OPEN_LOANS = "user";
  public static final String USER_ID_WITHOUT_OPEN_LOANS = "2";
  public static final DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
  private final List<LoanResponseDto> loanResponseDto = List.of(MockData.getValidLoanResponseDto());
  private final List<LoanResponseDto> listOfLoanResponses =
      MockData.getUnorderedListOfLoanResponseDtos();

  @Test
  @WithUserId(USER_ID_WITH_OPEN_LOANS)
  void whenGetAllLoans_thenSuccess() throws Exception {

    mockMvc
        .perform(get(LOANS_URL).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$[0].id", is(Math.toIntExact(loanResponseDto.get(0).id()))))
        .andExpect(jsonPath("$[0].amount", is(loanResponseDto.get(0).amount().intValue())))
        .andExpect(jsonPath("$[0].nextPaymentAmount", is(282.08)))
        .andExpect(
            jsonPath(
                "$[0].nextPaymentDate",
                is(loanResponseDto.get(0).nextPaymentDate().format(formatter))))
        .andExpect(jsonPath("$[0].status", is(loanResponseDto.get(0).status().name())))
        .andExpect(jsonPath("$[0].accountNumber", is(loanResponseDto.get(0).accountNumber())))
        .andExpect(
            jsonPath("$[0].productId", is(Math.toIntExact(loanResponseDto.get(0).productId()))))
        .andExpect(jsonPath("$[0].duration", is(loanResponseDto.get(0).duration())))
        .andExpect(jsonPath("$[0].productName", is(loanResponseDto.get(0).productName())));
  }

  @Test
  @WithUserId(USER_ID_WITH_OPEN_LOANS)
  void whenGetAllLoansUnordered_thenSuccess() throws Exception {
    mockMvc
        .perform(get(LOANS_URL).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$[0].id", is(Math.toIntExact(listOfLoanResponses.get(0).id()))))
        .andExpect(jsonPath("$[1].id", is(Math.toIntExact(listOfLoanResponses.get(2).id()))))
        .andExpect(jsonPath("$[2].id", is(Math.toIntExact(listOfLoanResponses.get(1).id()))))
        .andExpect(jsonPath("$[3]").doesNotExist());
  }

  @Test
  @WithUserId(USER_ID_WITHOUT_OPEN_LOANS)
  void whenGetNoLoans_thenReturnEmptyList() throws Exception {

    mockMvc
        .perform(get(LOANS_URL).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$").isEmpty());
  }
}

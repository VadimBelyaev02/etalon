package com.andersenlab.etalon.transactionservice.integration.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.transactionservice.MockData;
import com.andersenlab.etalon.transactionservice.annotation.WithUserId;
import com.andersenlab.etalon.transactionservice.controller.TransferTypeController;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.TransferTypeResponseDto;
import com.andersenlab.etalon.transactionservice.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

@ExtendWith(MockitoExtension.class)
public class GetTransferTypesEndpointTest extends AbstractIntegrationTest {

  public static final String USER_ID = "1";
  public static final String USER_ID_WITH_ONE_ACCOUNT = "3";

  @Test
  @WithUserId
  void whenGetTransferTypes_thenSuccess() throws Exception {
    TransferTypeResponseDto transferTypeResponseDto1 =
        MockData.getValidTransferTypeToAnotherAccountResponseDto();
    TransferTypeResponseDto transferTypeResponseDto2 =
        MockData.getValidTransferTypeToCardResponseDto();
    TransferTypeResponseDto transferTypeResponseDto3 =
        MockData.getValidTransferTypeToMyAccountResponseDto();

    mockMvc
        .perform(
            get(TransferTypeController.TRANSFER_TYPES_URL).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id", is(transferTypeResponseDto1.id().intValue())))
        .andExpect(jsonPath("$[0].name", is(transferTypeResponseDto1.name())))
        .andExpect(jsonPath("$[0].transferType", is(transferTypeResponseDto1.transferType())))
        .andExpect(jsonPath("$[1].id", is(transferTypeResponseDto2.id().intValue())))
        .andExpect(jsonPath("$[1].name", is(transferTypeResponseDto2.name())))
        .andExpect(jsonPath("$[1].transferType", is(transferTypeResponseDto2.transferType())))
        .andExpect(jsonPath("$[2].id", is(transferTypeResponseDto3.id().intValue())))
        .andExpect(jsonPath("$[2].name", is(transferTypeResponseDto3.name())))
        .andExpect(jsonPath("$[2].transferType", is(transferTypeResponseDto3.transferType())))
        .andExpect(jsonPath("$.*", hasSize(3)));
  }

  @Test
  @WithUserId(USER_ID_WITH_ONE_ACCOUNT)
  void whenGetTransferTypesToUserWithOneAccount_thenSuccess() throws Exception {
    TransferTypeResponseDto transferTypeResponseDto1 =
        MockData.getValidTransferTypeToAnotherAccountResponseDto();
    TransferTypeResponseDto transferTypeResponseDto2 =
        MockData.getValidTransferTypeToCardResponseDto();

    mockMvc
        .perform(
            get(TransferTypeController.TRANSFER_TYPES_URL).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id", is(transferTypeResponseDto1.id().intValue())))
        .andExpect(jsonPath("$[0].name", is(transferTypeResponseDto1.name())))
        .andExpect(jsonPath("$[0].transferType", is(transferTypeResponseDto1.transferType())))
        .andExpect(jsonPath("$[1].id", is(transferTypeResponseDto2.id().intValue())))
        .andExpect(jsonPath("$[1].name", is(transferTypeResponseDto2.name())))
        .andExpect(jsonPath("$[1].transferType", is(transferTypeResponseDto2.transferType())))
        .andExpect(jsonPath("$.*", hasSize(3)));
  }
}

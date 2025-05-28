package com.andersenlab.etalon.infoservice.component.controller;

import static com.andersenlab.etalon.infoservice.MockData.getValidBankInfoResponseDto;
import static com.andersenlab.etalon.infoservice.controller.BankInfoController.BANK_SEARCH_URL;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.infoservice.component.AbstractComponentTest;
import com.andersenlab.etalon.infoservice.dto.response.BankInfoResponseDto;
import com.andersenlab.etalon.infoservice.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;

class InfoBanksSearchComponentTest extends AbstractComponentTest {
  public static final String VALID_CARD_NUMBER = "4150461111111111";
  public static final String VALID_IBAN = "PL10315046000000000000000000";
  public static final String IBAN_WITH_NOT_EXISTING_BANK_CODE = "PL10115046000000000000000000";
  public static final String CARD_NUMBER_WITH_NOT_EXISTING_BIN = "6666661111111111";
  public static final String CARD_NUMBER_VIOLATION_CONSTRAINT_MESSAGE =
      "Card number must contain exactly 16 digits";
  public static final String IBAN_VIOLATION_CONSTRAINT_MESSAGE =
      "IBAN should start with two letters followed by 26 digits, 28 characters in total";
  public static final String BANK_NOT_FOUND_BY_BANK_CODE =
      "Cannot find foreign bank with bank code";
  public static final String BANK_NOT_FOUND_BY_BIN = "Cannot find foreign bank with bin";

  @Test
  void whenGetBankInfoWithValidIban_shouldReturnBankInfoResponseDto() throws Exception {
    // given
    final BankInfoResponseDto expected = getValidBankInfoResponseDto();

    // when/then
    mockMvc
        .perform(
            get(BANK_SEARCH_URL).param("iban", VALID_IBAN).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.isForeignBank", is(expected.isForeignBank())))
        .andExpect(jsonPath("$.callName", is(expected.callName())))
        .andExpect(jsonPath("$.fullName", is(expected.fullName())))
        .andExpect(jsonPath("$.bankDetails[0].bin", is(expected.bankDetails().get(0).bin())))
        .andExpect(
            jsonPath("$.bankDetails[0].bankCode", is(expected.bankDetails().get(0).bankCode())))
        .andExpect(
            jsonPath(
                "$.bankDetails[0].currency", is(expected.bankDetails().get(0).currency().name())));
  }

  @Test
  void whenGetBankInfoWithValidCardNumber_shouldReturnBankInfoResponseDto() throws Exception {
    // given
    final BankInfoResponseDto expected = getValidBankInfoResponseDto();

    // when/then
    mockMvc
        .perform(
            get(BANK_SEARCH_URL)
                .param("cardNumber", VALID_CARD_NUMBER)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.isForeignBank", is(expected.isForeignBank())))
        .andExpect(jsonPath("$.callName", is(expected.callName())))
        .andExpect(jsonPath("$.fullName", is(expected.fullName())));
  }

  @Test
  void whenGetBankInfoWithAllNullParameters_shouldThrowBusinessException() throws Exception {

    mockMvc
        .perform(get(BANK_SEARCH_URL).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", is(BusinessException.INFORMATION_NOT_PROVIDED)));
  }

  @Test
  void whenGetBankInfoByNotExistingBankCode_shouldThrowBusinessException() throws Exception {

    mockMvc
        .perform(get(BANK_SEARCH_URL).param("iban", IBAN_WITH_NOT_EXISTING_BANK_CODE))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message", containsString(BANK_NOT_FOUND_BY_BANK_CODE)));
  }

  @Test
  void whenGetBankInfoByNotExistingBIN_shouldThrowBusinessException() throws Exception {

    mockMvc
        .perform(get(BANK_SEARCH_URL).param("cardNumber", CARD_NUMBER_WITH_NOT_EXISTING_BIN))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message", containsString(BANK_NOT_FOUND_BY_BIN)));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"12345678901234567", "123456789012345", "123456789012345Q", "q234567890123451"})
  void whenGetBankInfoByInvalidCardNumberFormat_shouldReturnValidationErrorMessage(
      String cardNumber) throws Exception {
    mockMvc
        .perform(get(BANK_SEARCH_URL).param("cardNumber", cardNumber))
        .andExpect(status().isBadRequest())
        .andReturn()
        .getResolvedException()
        .getMessage()
        .contains(CARD_NUMBER_VIOLATION_CONSTRAINT_MESSAGE);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "Pl10315046000000000000000000",
        "PLN1031504600000000000000000",
        "121031504600000000000000000",
        "121031504600000GZ0000000000",
        "PL10315046000000000g00000000",
        "PL103150460000000000000000001",
        "PL1031504600000000000000000",
        "pL10315046000000000000000000",
        "P110315046000000000000000000"
      })
  void whenGetBankInfoByInvalidIbanFormat_shouldReturnValidationErrorMessage(String iban)
      throws Exception {
    mockMvc
        .perform(get(BANK_SEARCH_URL).param("iban", iban))
        .andExpect(status().isBadRequest())
        .andReturn()
        .getResolvedException()
        .getMessage()
        .contains(IBAN_VIOLATION_CONSTRAINT_MESSAGE);
  }
}

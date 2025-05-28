package com.andersenlab.etalon.transactionservice.integration.controller;

import static com.andersenlab.etalon.transactionservice.MockData.getValidTemplatePatchRequestDto;
import static com.andersenlab.etalon.transactionservice.MockData.getValidTemplateWithSameNamePatchRequestDto;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.transactionservice.annotation.WithUserId;
import com.andersenlab.etalon.transactionservice.controller.TemplateController;
import com.andersenlab.etalon.transactionservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.transactionservice.dto.template.request.TemplatePatchRequestDto;
import com.andersenlab.etalon.transactionservice.exception.BusinessException;
import com.andersenlab.etalon.transactionservice.integration.AbstractIntegrationTest;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

class PatchTemplateEndpointTest extends AbstractIntegrationTest {
  private static final Long EXISTING_TEMPLATE_ID = 1L;
  private static final Long ABSENT_TEMPLATE_ID = -1L;
  private static final Long ABSENT_PAYMENT_PRODUCT_ID = -1L;
  private static final BigDecimal WRONG_PAYMENT_AMOUNT = BigDecimal.valueOf(20.456);
  private static final String EXISTING_NAME = "name2";

  @BeforeEach
  void setUp() {}

  @Test
  @WithUserId
  void whenPatchTemplateWithSameNameAsWas_shouldSuccess() throws Exception {
    TemplatePatchRequestDto templatePatchRequestDto = getValidTemplateWithSameNamePatchRequestDto();

    mockMvc
        .perform(
            patch(TemplateController.TEMPLATES_URL + "/" + EXISTING_TEMPLATE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(templatePatchRequestDto)))
        .andExpect(status().isOk())
        .andExpect(
            MockMvcResultMatchers.jsonPath(
                "$.message",
                is(
                    MessageResponseDto.TEMPLATE_PATCH_IS_SUCCESSFUL.formatted(
                        EXISTING_TEMPLATE_ID))));
  }

  @Test
  @WithUserId
  void whenPatchTemplate_shouldSuccess() throws Exception {
    TemplatePatchRequestDto templatePatchRequestDto = getValidTemplatePatchRequestDto();

    mockMvc
        .perform(
            patch(TemplateController.TEMPLATES_URL + "/" + EXISTING_TEMPLATE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(templatePatchRequestDto)))
        .andExpect(status().isOk())
        .andExpect(
            MockMvcResultMatchers.jsonPath(
                "$.message",
                is(
                    MessageResponseDto.TEMPLATE_PATCH_IS_SUCCESSFUL.formatted(
                        EXISTING_TEMPLATE_ID))));
  }

  @Test
  @WithUserId
  void whenPatchTemplate_shouldFail() throws Exception {
    TemplatePatchRequestDto templatePatchRequestDto = getValidTemplatePatchRequestDto();

    mockMvc
        .perform(
            patch(TemplateController.TEMPLATES_URL + "/" + ABSENT_TEMPLATE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(templatePatchRequestDto)))
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath(
                "$.message",
                is(BusinessException.TEMPLATE_NOT_FOUND_BY_ID.formatted(ABSENT_TEMPLATE_ID))));
  }

  @Test
  @WithUserId
  void whenPatchTemplateWithAbsentPaymentProductId_shouldFail() throws Exception {
    TemplatePatchRequestDto templatePatchRequestDto =
        getValidTemplatePatchRequestDto().toBuilder().productId(ABSENT_PAYMENT_PRODUCT_ID).build();

    mockMvc
        .perform(
            patch(TemplateController.TEMPLATES_URL + "/" + EXISTING_TEMPLATE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(templatePatchRequestDto)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message", is(BusinessException.PRODUCT_TYPE_DOES_NOT_EXIST)));
  }

  @Test
  @WithUserId
  void whenPatchTemplateWithWrongAmount_shouldFail() throws Exception {
    TemplatePatchRequestDto templatePatchRequestDto =
        getValidTemplatePatchRequestDto().toBuilder().amount(WRONG_PAYMENT_AMOUNT).build();

    mockMvc
        .perform(
            patch(TemplateController.TEMPLATES_URL + "/" + EXISTING_TEMPLATE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(templatePatchRequestDto)))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath(
                "$.message",
                is(
                    BusinessException
                        .OPERATION_REJECTED_BECAUSE_AMOUNT_HAS_MORE_THAN_2_DIGITS_AFTER_DOT)));
  }

  @Test
  @WithUserId
  void whenPatchTemplateWithExistingName_shouldFail() throws Exception {
    TemplatePatchRequestDto templatePatchRequestDto =
        getValidTemplatePatchRequestDto().toBuilder().templateName(EXISTING_NAME).build();

    mockMvc
        .perform(
            patch(TemplateController.TEMPLATES_URL + "/" + EXISTING_TEMPLATE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(templatePatchRequestDto)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", is(BusinessException.TEMPLATE_NAME_ALREADY_BEEN_SAVED)));
  }
}

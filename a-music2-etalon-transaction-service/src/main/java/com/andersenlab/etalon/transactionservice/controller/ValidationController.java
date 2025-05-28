package com.andersenlab.etalon.transactionservice.controller;

import com.andersenlab.etalon.transactionservice.controller.impl.TemplateControllerImpl;
import com.andersenlab.etalon.transactionservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.StatusMessageResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;

public interface ValidationController {
  String API_V1_URI = "/api/v1";
  String VALIDATIONS_URI = "/validations";
  String VALIDATIONS_URL = API_V1_URI + VALIDATIONS_URI + TemplateControllerImpl.TEMPLATES_URI;
  String TEMPLATE_NAME_URI = "/{templateName}";

  @Operation(summary = "Check if template name valid — US 6.6")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Information provided",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array =
                        @ArraySchema(
                            schema = @Schema(implementation = StatusMessageResponseDto.class)))),
        @ApiResponse(
            responseCode = "401",
            description = "User is not authorized",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
      })
  StatusMessageResponseDto isTemplateNameValid(@PathVariable String templateName);
}

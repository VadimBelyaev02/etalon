package com.andersenlab.etalon.transactionservice.controller;

import com.andersenlab.etalon.transactionservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.transactionservice.dto.template.request.TemplatePatchRequestDto;
import com.andersenlab.etalon.transactionservice.dto.template.response.TemplateInfoResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface TemplateController {
  String API_V1_URI = "/api/v1";
  String TEMPLATES_URI = "/templates";
  String TEMPLATE_ID_PATH = "/{templateId}";
  String TEMPLATES_URL = API_V1_URI + TEMPLATES_URI;

  @Operation(summary = "Get detailed template info — US 7.8")
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
                            schema = @Schema(implementation = TemplateInfoResponseDto.class)))),
        @ApiResponse(
            responseCode = "401",
            description = "User is not authorized",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
      })
  TemplateInfoResponseDto getTemplate(@PathVariable Long templateId);

  @Operation(summary = "Delete payment template by template id — US 7.8")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Deleting of payment template completed",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array =
                        @ArraySchema(schema = @Schema(implementation = MessageResponseDto.class)))),
        @ApiResponse(
            responseCode = "401",
            description = "User is not authorized",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Payment template with such id does not exist",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
      })
  MessageResponseDto deleteTemplate(@PathVariable Long templateId);

  @Operation(summary = "Get all user templates — US 7.8")
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
                            schema = @Schema(implementation = TemplateInfoResponseDto.class)))),
        @ApiResponse(
            responseCode = "401",
            description = "User is not authorized",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
      })
  List<TemplateInfoResponseDto> getTemplates();

  @Operation(summary = "Patch payment template — US 7.8")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Patching of payment template completed",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array =
                        @ArraySchema(schema = @Schema(implementation = MessageResponseDto.class)))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid payment amount provided",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
        @ApiResponse(
            responseCode = "401",
            description = "User is not authorized",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
        @ApiResponse(
            responseCode = "404",
            description =
                "Payment template with such id does not exist | Payment product with such id does not exist",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class)))
      })
  MessageResponseDto patchTemplate(
      @PathVariable Long templateId, @Valid @RequestBody TemplatePatchRequestDto patchRequestDto);
}

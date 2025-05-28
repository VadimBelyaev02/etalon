package com.andersenlab.etalon.depositservice.controller;

import com.andersenlab.etalon.depositservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.depositservice.dto.deposit.request.DepositReplenishRequestDto;
import com.andersenlab.etalon.depositservice.dto.deposit.request.DepositUpdateRequestDto;
import com.andersenlab.etalon.depositservice.dto.deposit.request.DepositWithdrawRequestDto;
import com.andersenlab.etalon.depositservice.dto.deposit.request.OpenDepositRequestDto;
import com.andersenlab.etalon.depositservice.dto.deposit.response.ConfirmationOpenDepositResponseDto;
import com.andersenlab.etalon.depositservice.dto.deposit.response.DepositDetailedResponseDto;
import com.andersenlab.etalon.depositservice.dto.deposit.response.DepositResponseDto;
import com.andersenlab.etalon.depositservice.util.filter.CustomPageRequest;
import com.andersenlab.etalon.depositservice.util.filter.DepositFilterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface DepositController {

  String API_V1_URI = "/api/v1";
  String API_V2_URI = "/api/v2";
  String DEPOSIT_URI = "/deposits";
  String DEPOSIT_ID_URI = "/{depositId}";
  String WITHDRAWN_URI = "/withdrawn";
  String REPLENISHED_URI = "/replenished";
  String DEPOSITS_V1_URL = API_V1_URI + DEPOSIT_URI;
  String DEPOSITS_V2_URL = API_V2_URI + DEPOSIT_URI;
  String DEPOSIT_ID_WITHDRAWN_URL = DEPOSIT_ID_URI + WITHDRAWN_URI;
  String DEPOSIT_ID_REPLENISHED_URL = DEPOSIT_ID_URI + REPLENISHED_URI;
  String DEFAULT_PAGE_NO = "0";
  String DEFAULT_PAGE_SIZE = "3";

  @Operation(summary = "Get all open user deposits paged")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Information provided",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array =
                        @ArraySchema(schema = @Schema(implementation = DepositResponseDto.class)))),
        @ApiResponse(
            responseCode = "401",
            description = "User is not authorized",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
      })
  List<DepositResponseDto> getAllDepositsByUserId(
      @RequestParam(defaultValue = DEFAULT_PAGE_NO) int pageNo,
      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize);

  @Operation(summary = "Get detailed deposit info")
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
                            schema = @Schema(implementation = DepositDetailedResponseDto.class)))),
        @ApiResponse(
            responseCode = "401",
            description = "User is not authorized",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Cannot find deposit entity",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class)))
      })
  DepositDetailedResponseDto getDetailedDeposit(@PathVariable("depositId") Long depositId);

  @Operation(summary = "Make deposit withdrawal")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Information provided",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array =
                        @ArraySchema(schema = @Schema(implementation = MessageResponseDto.class)))),
        @ApiResponse(
            responseCode = "400",
            description = "Withdraw for deposit with ID: %s has been rejected",
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
            description = "Cannot find deposit with id %s",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class)))
      })
  MessageResponseDto withdrawDeposit(
      @PathVariable Long depositId,
      @RequestBody DepositWithdrawRequestDto depositWithdrawRequestDto);

  @Operation(summary = "Make deposit replenish")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Information provided",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array =
                        @ArraySchema(schema = @Schema(implementation = MessageResponseDto.class)))),
        @ApiResponse(
            responseCode = "400",
            description = "Replenish for deposit with ID: %s has been rejected",
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
            description = "Cannot find deposit with id %s",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class)))
      })
  MessageResponseDto replenishDeposit(
      @PathVariable Long depositId,
      @RequestBody DepositReplenishRequestDto depositReplenishRequestDto);

  @Operation(summary = "Open new deposit")
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
                            schema =
                                @Schema(
                                    implementation = ConfirmationOpenDepositResponseDto.class)))),
        @ApiResponse(
            responseCode = "401",
            description = "User is not authorized",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Cannot find deposit product with id %s",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class)))
      })
  ConfirmationOpenDepositResponseDto openNewDeposit(@RequestBody OpenDepositRequestDto dto);

  @Operation(summary = "Get filtered open user deposits paged")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Information provided",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array =
                        @ArraySchema(schema = @Schema(implementation = DepositResponseDto.class)))),
        @ApiResponse(
            responseCode = "401",
            description = "User is not authorized",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
      })
  Page<DepositResponseDto> getFilteredDepositsByUserId(
      @ParameterObject @Valid CustomPageRequest pageRequest,
      @ParameterObject @Valid DepositFilterRequest filter);

  @Operation(summary = "Update deposit accounts")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Deposit with id %s updated successfully",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Cannot find deposit with id %s; Account entity with account number %s",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
        @ApiResponse(
            responseCode = "400",
            description =
                "At least one field must be provided; "
                    + "IBAN should start with two letters followed by 26 digits, 28 characters in total; "
                    + "Operation for user has been rejected because given account number is invalid; "
                    + "Transaction for user has been rejected because given account is blocked; "
                    + "Deposit update rejected due to the same account number provided",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class)))
      })
  MessageResponseDto patchDeposit(
      @PathVariable Long depositId,
      @Valid @RequestBody DepositUpdateRequestDto depositUpdateRequestDto);
}

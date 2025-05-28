package com.andersenlab.etalon.accountservice.controller;

import static com.andersenlab.etalon.accountservice.util.Constants.ACCOUNT_NUMBER_PATTERN;
import static com.andersenlab.etalon.accountservice.util.Constants.WRONG_ACCOUNT_NUMBER_MESSAGE;

import com.andersenlab.etalon.accountservice.dto.account.response.AccountBalanceResponseDto;
import com.andersenlab.etalon.accountservice.dto.account.response.AccountDetailedResponseDto;
import com.andersenlab.etalon.accountservice.dto.account.response.AccountInterestResponseDto;
import com.andersenlab.etalon.accountservice.dto.common.response.MessageResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

public interface AccountByNumberController {
  String API_V1_URI = "/api/v1";
  String ACCOUNT_BY_NUMBER_URI = "/accounts-by-number";
  String BALANCE_URI = "/balance";
  String ACCOUNT_NUMBER_PATH = "/{accountNumber}";
  String BLOCKING_URI = "/blocking";
  String ACCOUNT_BY_NUMBER_URL = API_V1_URI + ACCOUNT_BY_NUMBER_URI;
  String ACCOUNT_BALANCE_BY_ACCOUNT_NUMBER_PATH_URL = ACCOUNT_NUMBER_PATH + BALANCE_URI;

  @Operation(summary = "Get user account balance by account number")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Account balance provided",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = AccountBalanceResponseDto.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Cannot find account entity with account number %s",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class)))
      })
  AccountBalanceResponseDto getAccountBalanceByAccountNumber(
      @PathVariable
          @Valid
          @Pattern(regexp = ACCOUNT_NUMBER_PATTERN, message = WRONG_ACCOUNT_NUMBER_MESSAGE)
          String accountNumber);

  @Operation(summary = "Get detailed account info")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Detailed account info provided",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = AccountDetailedResponseDto.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Cannot find account entity with account number %s",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class)))
      })
  AccountDetailedResponseDto getDetailedAccountInfo(
      @PathVariable
          @Valid
          @Pattern(regexp = ACCOUNT_NUMBER_PATTERN, message = WRONG_ACCOUNT_NUMBER_MESSAGE)
          String accountNumber);

  @Operation(summary = "Get user account balances")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Account balances provided",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array =
                        @ArraySchema(
                            schema = @Schema(implementation = AccountInterestResponseDto.class)))),
        @ApiResponse(
            responseCode = "404",
            description = "Cannot find account balances",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class)))
      })
  List<AccountInterestResponseDto> getAccountsBalances(
      @RequestParam
          List<
                  @Valid
                  @Pattern(regexp = ACCOUNT_NUMBER_PATTERN, message = WRONG_ACCOUNT_NUMBER_MESSAGE)
                  String>
              accountsNumbers);

  @Operation(summary = "Change blocked status of account")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Return blocking status"),
        @ApiResponse(
            responseCode = "404",
            description = "Cannot find account entity with account number %s",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class)))
      })
  Boolean changeIsBlocked(
      @RequestParam
          @Valid
          @Pattern(regexp = ACCOUNT_NUMBER_PATTERN, message = WRONG_ACCOUNT_NUMBER_MESSAGE)
          String accountNumber);

  @Operation(summary = "Delete account by account number")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "404",
            description = "Cannot find account entity with account number %s",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
        @ApiResponse(
            responseCode = "409",
            description = "An account with a positive balance cannot be deleted",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class)))
      })
  void deleteAccount(
      @PathVariable
          @Valid
          @Pattern(regexp = ACCOUNT_NUMBER_PATTERN, message = WRONG_ACCOUNT_NUMBER_MESSAGE)
          String accountNumber);
}

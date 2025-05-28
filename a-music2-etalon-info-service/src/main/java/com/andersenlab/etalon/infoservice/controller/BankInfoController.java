package com.andersenlab.etalon.infoservice.controller;

import com.andersenlab.etalon.infoservice.dto.request.BankInfoRequestDto;
import com.andersenlab.etalon.infoservice.dto.response.BankBranchesAndAtmsResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.BankContactsResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.BankInfoResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.DateTimeResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.MessageResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.HashMap;
import java.util.List;
import org.springframework.http.MediaType;

public interface BankInfoController {

  String API_V1_URI = "/api/v1";
  String INFO_URI = "/info";
  String BRANCHES_URI = "/branches";
  String BRANCHES_AND_ATMS_URI = "/branches-and-atms";
  String CONTACTS_URI = "/contacts";
  String CITIES_URI = "/cities";
  String SERVER_DATE_TIME_URI = "/date";
  String BRANCH_ID_PATH = "/{bankBranchId}";
  String BANKS_URI = "/banks";
  String SEARCH_URI = "/search";
  String BANK_SEARCH_URI = BANKS_URI + SEARCH_URI;
  String BRANCHES_BRANCH_ID_URI = BRANCHES_URI + BRANCH_ID_PATH;

  String BANK_INFO_URL = API_V1_URI + INFO_URI;
  String SERVER_DATE_TIME_URL = BANK_INFO_URL + SERVER_DATE_TIME_URI;
  String BANK_INFO_CONTACTS_URL = BANK_INFO_URL + CONTACTS_URI;
  String BANK_INFO_CITIES_URL = BANK_INFO_URL + CITIES_URI;
  String BANK_BRANCHES_AND_ATMS_URL = BANK_INFO_URL + BRANCHES_AND_ATMS_URI;
  String BANK_SEARCH_URL = BANK_INFO_URL + BANK_SEARCH_URI;

  @Operation(summary = "Get all branches and atms info — US 1.5")
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
                                @Schema(implementation = BankBranchesAndAtmsResponseDto.class))))
      })
  List<BankBranchesAndAtmsResponseDto> getAllBranchesAndAtms(final String city);

  @Operation(summary = "Get all bank contacts — US 1.5")
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
                            schema = @Schema(implementation = BankContactsResponseDto.class))))
      })
  BankContactsResponseDto getAllContacts();

  @Operation(summary = "Get all cities — US 1.5")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Information provided",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = String.class))))
      })
  List<String> getAllCities();

  @Operation(summary = "Get current date — US 1.1")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Information provided",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = HashMap.class)))),
      })
  DateTimeResponseDto getCurrentDate();

  @Operation(summary = "Get bank info by cardNumber or iban — US 7.3")
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
                            schema = @Schema(implementation = BankInfoResponseDto.class)))),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Not Found",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
      })
  BankInfoResponseDto getBankInfoBySelectedOption(BankInfoRequestDto bankInfoRequestDto);
}

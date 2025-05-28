package com.andersenlab.etalon.infoservice.controller.impl;

import com.andersenlab.etalon.infoservice.controller.BankInfoController;
import com.andersenlab.etalon.infoservice.dto.request.BankInfoRequestDto;
import com.andersenlab.etalon.infoservice.dto.response.BankBranchesAndAtmsResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.BankBranchesResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.BankContactsResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.BankInfoResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.DateTimeResponseDto;
import com.andersenlab.etalon.infoservice.service.BankBranchesAndAtmsService;
import com.andersenlab.etalon.infoservice.service.BankContactsService;
import com.andersenlab.etalon.infoservice.service.BankInfoService;
import com.andersenlab.etalon.infoservice.service.InfoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(BankInfoController.BANK_INFO_URL)
@RequiredArgsConstructor
@Tag(name = "Bank Info")
public class BankInfoControllerImpl implements BankInfoController {

  private final BankBranchesAndAtmsService bankBranchesAndAtmsService;
  private final BankInfoService bankInfoService;
  private final BankContactsService bankContactsService;
  private final InfoService infoService;

  @GetMapping(BRANCHES_AND_ATMS_URI)
  public List<BankBranchesAndAtmsResponseDto> getAllBranchesAndAtms(
      @RequestParam(required = false, name = "city") String city) {
    return bankBranchesAndAtmsService.getAllBranchesAndAtmsByCity(city);
  }

  @GetMapping(CITIES_URI)
  public List<String> getAllCities() {
    return bankBranchesAndAtmsService.getAllCities();
  }

  @GetMapping(CONTACTS_URI)
  public BankContactsResponseDto getAllContacts() {
    return bankContactsService.getAllContacts();
  }

  @GetMapping(SERVER_DATE_TIME_URI)
  public DateTimeResponseDto getCurrentDate() {
    return infoService.getCurrentDate();
  }

  @GetMapping(BRANCHES_BRANCH_ID_URI)
  public BankBranchesResponseDto getBankBranch(@PathVariable Long bankBranchId) {
    return bankBranchesAndAtmsService.getBankBranchById(bankBranchId);
  }

  @GetMapping(BANK_SEARCH_URI)
  public BankInfoResponseDto getBankInfoBySelectedOption(
      @Valid @ParameterObject BankInfoRequestDto bankInfoRequestDto) {
    return bankInfoService.getBankInfoBySelectedOption(bankInfoRequestDto);
  }
}

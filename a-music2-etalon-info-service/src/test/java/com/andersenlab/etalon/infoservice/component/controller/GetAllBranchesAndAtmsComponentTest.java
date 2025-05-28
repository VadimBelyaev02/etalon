package com.andersenlab.etalon.infoservice.component.controller;

import static com.andersenlab.etalon.infoservice.controller.BankInfoController.BANK_BRANCHES_AND_ATMS_URL;
import static com.andersenlab.etalon.infoservice.controller.BankInfoController.BANK_INFO_CITIES_URL;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.infoservice.MockData;
import com.andersenlab.etalon.infoservice.component.AbstractComponentTest;
import com.andersenlab.etalon.infoservice.entity.AtmEntity;
import com.andersenlab.etalon.infoservice.entity.BankBranchEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

class GetAllBranchesAndAtmsComponentTest extends AbstractComponentTest {

  private AtmEntity atm;
  private BankBranchEntity bankBranch;

  @BeforeEach
  void setUp() {
    atm = MockData.getValidAtmEntity();
    bankBranch = MockData.getValidBankBranchEntity();
  }

  @Test
  void whenGetAllBranchesAndAtms_shouldSuccess() throws Exception {

    // when/then
    mockMvc
        .perform(get(UriComponentsBuilder.fromPath(BANK_BRANCHES_AND_ATMS_URL).toUriString()))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$[1].id", is(Math.toIntExact(bankBranch.getId()))))
        .andExpect(jsonPath("$[1].name", is(bankBranch.getBankBranchName())))
        .andExpect(jsonPath("$[1].city", is(bankBranch.getCity())))
        .andExpect(jsonPath("$[1].latitude", is(bankBranch.getLatitude())))
        .andExpect(jsonPath("$[1].longitude", is(bankBranch.getLongitude())))
        .andExpect(jsonPath("$[5].id", is(Math.toIntExact(atm.getId()))))
        .andExpect(jsonPath("$[5].name", is(atm.getAtmName())))
        .andExpect(jsonPath("$[5].city", is(atm.getCity())))
        .andExpect(jsonPath("$[5].latitude", is(atm.getLatitude())))
        .andExpect(jsonPath("$[5].longitude", is(atm.getLongitude())));
  }

  @Test
  void whenGetAllBranchesAndAtmsByCity_shouldSuccess() throws Exception {

    // when/then
    mockMvc
        .perform(
            get(UriComponentsBuilder.fromPath(BANK_BRANCHES_AND_ATMS_URL).toUriString())
                .param("city", "Gdansk"))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$[1].id", is(Math.toIntExact(bankBranch.getId()))))
        .andExpect(jsonPath("$[1].name", is(bankBranch.getBankBranchName())))
        .andExpect(jsonPath("$[1].city", is(bankBranch.getCity())))
        .andExpect(jsonPath("$[1].latitude", is(bankBranch.getLatitude())))
        .andExpect(jsonPath("$[1].longitude", is(bankBranch.getLongitude())))
        .andExpect(jsonPath("$[5].id", is(Math.toIntExact(atm.getId()))))
        .andExpect(jsonPath("$[5].name", is(atm.getAtmName())))
        .andExpect(jsonPath("$[5].city", is(atm.getCity())))
        .andExpect(jsonPath("$[5].latitude", is(atm.getLatitude())))
        .andExpect(jsonPath("$[5].longitude", is(atm.getLongitude())));
  }

  @Test
  void whenGetAllCities_shouldSuccess() throws Exception {

    // when/then
    mockMvc
        .perform(get(UriComponentsBuilder.fromPath(BANK_INFO_CITIES_URL).toUriString()))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$[0]", is(bankBranch.getCity())))
        .andExpect(jsonPath("$[0]", is(atm.getCity())));
  }
}

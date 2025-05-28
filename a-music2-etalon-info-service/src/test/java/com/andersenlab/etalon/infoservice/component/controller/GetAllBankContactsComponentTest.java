package com.andersenlab.etalon.infoservice.component.controller;

import static com.andersenlab.etalon.infoservice.controller.BankInfoController.BANK_INFO_CONTACTS_URL;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.infoservice.MockData;
import com.andersenlab.etalon.infoservice.component.AbstractComponentTest;
import com.andersenlab.etalon.infoservice.entity.BankContactsEntity;
import java.util.Locale;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

class GetAllBankContactsComponentTest extends AbstractComponentTest {

  private BankContactsEntity bankContacts;

  @BeforeEach
  void setUp() {
    bankContacts = MockData.getValidBankContactsEntity();
  }

  @Test
  void whenGetAllBranchesAndAtms_withEnglishLocale_shouldSuccess() throws Exception {
    LocaleContextHolder.setLocale(Locale.ENGLISH);
    // when/then
    mockMvc
        .perform(
            get(UriComponentsBuilder.fromPath(BANK_INFO_CONTACTS_URL).toUriString())
                .header("Accept-Language", "en"))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$.id", is(Math.toIntExact(bankContacts.getId()))))
        .andExpect(jsonPath("$.bankName", is(bankContacts.getBankName())))
        .andExpect(jsonPath("$.address", is("Jana Pawła str., 10, 00-854 Warsaw")))
        .andExpect(jsonPath("$.nip", is(bankContacts.getNip())))
        .andExpect(jsonPath("$.regon", is(bankContacts.getRegon())))
        .andExpect(jsonPath("$.swiftCode", is(bankContacts.getSwiftCode())))
        .andExpect(jsonPath("$.email", is(bankContacts.getEmail())))
        .andExpect(jsonPath("$.phoneNumber", is(bankContacts.getPhoneNumber())));
  }

  @Test
  void whenGetAllBranchesAndAtms_withPolishLocale_shouldSuccess() throws Exception {
    LocaleContextHolder.setLocale(new Locale("pl"));
    // when/then
    mockMvc
        .perform(
            get(UriComponentsBuilder.fromPath(BANK_INFO_CONTACTS_URL).toUriString())
                .header("Accept-Language", "pl"))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$.id", is(Math.toIntExact(bankContacts.getId()))))
        .andExpect(jsonPath("$.bankName", is(bankContacts.getBankName())))
        .andExpect(jsonPath("$.address", is("ul. Jana Pawła, 10, 00-854 Warszawa")))
        .andExpect(jsonPath("$.nip", is(bankContacts.getNip())))
        .andExpect(jsonPath("$.regon", is(bankContacts.getRegon())))
        .andExpect(jsonPath("$.swiftCode", is(bankContacts.getSwiftCode())))
        .andExpect(jsonPath("$.email", is(bankContacts.getEmail())))
        .andExpect(jsonPath("$.phoneNumber", is(bankContacts.getPhoneNumber())));
  }

  @AfterEach
  void tearDown() {
    LocaleContextHolder.resetLocaleContext();
  }
}

package com.andersenlab.etalon.infoservice.component.controller;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.infoservice.MockData;
import com.andersenlab.etalon.infoservice.component.AbstractComponentTest;
import com.andersenlab.etalon.infoservice.controller.CountryCodesController;
import com.andersenlab.etalon.infoservice.dto.response.CountryCodesResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

class CountryCodesControllerComponentTest extends AbstractComponentTest {

  @Value("${aws.s3.bucket.image-country-code-url}")
  String imageBaseUrl;

  @Test
  void whenGetCountryCodesPl_shouldSuccess() throws Exception {
    CountryCodesResponseDto countryCodesResponseDto = MockData.getCountryCodesResponseDtoPl();
    Integer countryIdExpected = countryCodesResponseDto.id().intValue();
    String countryNameExpected = countryCodesResponseDto.countryName();
    String countryPhoneCodeExpected = countryCodesResponseDto.phoneCode();
    String countryImageUrlExpected = imageBaseUrl + countryCodesResponseDto.imageUrl();
    String url =
        UriComponentsBuilder.fromPath(CountryCodesController.COUNTRY_CODES_INFO_URL)
            .path(CountryCodesController.COUNTRY_CODES_URI)
            .queryParam("locale", "pl")
            .build()
            .toUriString();

    mockMvc
        .perform(get(url))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(
            jsonPath(
                "$[?(@.countryName=='" + countryNameExpected + "')].id",
                hasItem(countryIdExpected)))
        .andExpect(
            jsonPath(
                "$[?(@.countryName=='" + countryNameExpected + "')].phoneCode",
                hasItem(countryPhoneCodeExpected)))
        .andExpect(
            jsonPath(
                "$[?(@.countryName=='" + countryNameExpected + "')].imageUrl",
                hasItem(countryImageUrlExpected)));
  }

  @Test
  void whenGetCountryCodes_shouldSuccess() throws Exception {
    CountryCodesResponseDto countryCodesResponseDto = MockData.getCountryCodesResponseDtoDefault();
    Integer countryIdExpected = countryCodesResponseDto.id().intValue();
    String countryNameExpected = countryCodesResponseDto.countryName();
    String countryPhoneCodeExpected = countryCodesResponseDto.phoneCode();
    String countryImageUrlExpected = imageBaseUrl + countryCodesResponseDto.imageUrl();
    String url =
        UriComponentsBuilder.fromPath(CountryCodesController.COUNTRY_CODES_INFO_URL)
            .path(CountryCodesController.COUNTRY_CODES_URI)
            .build()
            .toUriString();

    mockMvc
        .perform(get(url))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(
            jsonPath(
                "$[?(@.countryName=='" + countryNameExpected + "')].id",
                hasItem(countryIdExpected)))
        .andExpect(
            jsonPath(
                "$[?(@.countryName=='" + countryNameExpected + "')].phoneCode",
                hasItem(countryPhoneCodeExpected)))
        .andExpect(
            jsonPath(
                "$[?(@.countryName=='" + countryNameExpected + "')].imageUrl",
                hasItem(countryImageUrlExpected)));
  }

  @Test
  void whenGetCountryCodes_shouldFail() throws Exception {
    String url =
        UriComponentsBuilder.fromPath(CountryCodesController.COUNTRY_CODES_INFO_URL)
            .path(CountryCodesController.COUNTRY_CODES_URI)
            .queryParam("locale", "unknown")
            .build()
            .toUriString();

    mockMvc
        .perform(get(url))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
        .andExpect(jsonPath("$.message", is("This locale is not supported")));
  }
}

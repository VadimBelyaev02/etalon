package com.andersenlab.etalon.infoservice.controller.impl;

import com.andersenlab.etalon.infoservice.controller.CountryCodesController;
import com.andersenlab.etalon.infoservice.dto.response.CountryCodesResponseDto;
import com.andersenlab.etalon.infoservice.service.CountryCodesService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(CountryCodesController.COUNTRY_CODES_INFO_URL)
@RequiredArgsConstructor
@Tag(name = "Country Codes")
public class CountryCodesControllerImpl implements CountryCodesController {

  private final CountryCodesService countryCodesService;

  @Override
  @GetMapping(COUNTRY_CODES_URI)
  public List<CountryCodesResponseDto> getAllCountryCodes(String locale) {
    return countryCodesService.getAllCountryCodes(locale);
  }
}

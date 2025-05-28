package com.andersenlab.etalon.infoservice.service.strategy.countrycode;

import com.andersenlab.etalon.infoservice.dto.response.CountryCodesResponseDto;
import com.andersenlab.etalon.infoservice.entity.CountryCodesEntity;
import java.util.List;

public interface CountryCodesStrategy {
  String COUNTRY_CODES_STRATEGY_PREFIX = "countryCodeStrategyPrefix";
  String UNSUPPORTED_COUNTRY_CODE_STRATEGY = "unsupportedCountryCodesStrategy";

  List<CountryCodesResponseDto> getCountryCodeResponseDto(
      List<CountryCodesEntity> countryCodesEntities);
}

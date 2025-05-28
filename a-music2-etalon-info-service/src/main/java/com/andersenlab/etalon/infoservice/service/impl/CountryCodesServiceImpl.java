package com.andersenlab.etalon.infoservice.service.impl;

import com.andersenlab.etalon.infoservice.dto.response.CountryCodesResponseDto;
import com.andersenlab.etalon.infoservice.entity.CountryCodesEntity;
import com.andersenlab.etalon.infoservice.repository.CountryCodesRepository;
import com.andersenlab.etalon.infoservice.service.CountryCodesService;
import com.andersenlab.etalon.infoservice.service.strategy.countrycode.CountryCodesStrategy;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CountryCodesServiceImpl implements CountryCodesService {

  private final CountryCodesRepository countryCodesRepository;
  private final Map<String, CountryCodesStrategy> codesStrategyMap;

  @Override
  public List<CountryCodesResponseDto> getAllCountryCodes(String locale) {
    List<CountryCodesEntity> countryCodesEntities = countryCodesRepository.findAll();

    CountryCodesStrategy unsupportedCountryCodeStrategy =
        codesStrategyMap.get(CountryCodesStrategy.UNSUPPORTED_COUNTRY_CODE_STRATEGY);

    CountryCodesStrategy countryCodesStrategy =
        codesStrategyMap.getOrDefault(
            CountryCodesStrategy.COUNTRY_CODES_STRATEGY_PREFIX + locale,
            unsupportedCountryCodeStrategy);
    return countryCodesStrategy.getCountryCodeResponseDto(countryCodesEntities);
  }
}

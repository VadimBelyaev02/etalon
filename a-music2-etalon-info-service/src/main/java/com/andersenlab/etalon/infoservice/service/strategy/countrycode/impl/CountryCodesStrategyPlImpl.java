package com.andersenlab.etalon.infoservice.service.strategy.countrycode.impl;

import com.andersenlab.etalon.infoservice.dto.response.CountryCodesResponseDto;
import com.andersenlab.etalon.infoservice.entity.CountryCodesEntity;
import com.andersenlab.etalon.infoservice.mapper.CountryCodesMapper;
import com.andersenlab.etalon.infoservice.service.strategy.countrycode.CountryCodesStrategy;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service(CountryCodesStrategy.COUNTRY_CODES_STRATEGY_PREFIX + "pl")
@RequiredArgsConstructor
public class CountryCodesStrategyPlImpl implements CountryCodesStrategy {

  private final CountryCodesMapper countryCodesMapper;

  @Override
  public List<CountryCodesResponseDto> getCountryCodeResponseDto(
      List<CountryCodesEntity> countryCodesEntities) {
    return countryCodesEntities.stream().map(countryCodesMapper::toDtoPl).toList();
  }
}

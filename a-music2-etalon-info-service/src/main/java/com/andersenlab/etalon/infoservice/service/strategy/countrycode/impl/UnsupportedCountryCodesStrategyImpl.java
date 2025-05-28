package com.andersenlab.etalon.infoservice.service.strategy.countrycode.impl;

import com.andersenlab.etalon.infoservice.dto.response.CountryCodesResponseDto;
import com.andersenlab.etalon.infoservice.entity.CountryCodesEntity;
import com.andersenlab.etalon.infoservice.exception.BusinessException;
import com.andersenlab.etalon.infoservice.service.strategy.countrycode.CountryCodesStrategy;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service(CountryCodesStrategy.UNSUPPORTED_COUNTRY_CODE_STRATEGY)
public class UnsupportedCountryCodesStrategyImpl implements CountryCodesStrategy {
  @Override
  public List<CountryCodesResponseDto> getCountryCodeResponseDto(
      List<CountryCodesEntity> countryCodesEntities) {
    throw new BusinessException(HttpStatus.BAD_REQUEST, "This locale is not supported");
  }
}

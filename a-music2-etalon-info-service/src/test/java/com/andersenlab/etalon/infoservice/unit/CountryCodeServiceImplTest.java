package com.andersenlab.etalon.infoservice.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.andersenlab.etalon.infoservice.dto.response.CountryCodesResponseDto;
import com.andersenlab.etalon.infoservice.entity.CountryCodesEntity;
import com.andersenlab.etalon.infoservice.repository.CountryCodesRepository;
import com.andersenlab.etalon.infoservice.service.impl.CountryCodesServiceImpl;
import com.andersenlab.etalon.infoservice.service.strategy.countrycode.CountryCodesStrategy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CountryCodeServiceImplTest {

  @Mock private CountryCodesRepository countryCodesRepository;
  @Mock private CountryCodesStrategy countryCodesStrategy;
  private CountryCodesServiceImpl countryCodesService;

  @BeforeEach
  public void setUp() {
    Map<String, CountryCodesStrategy> codesStrategyMap = new HashMap<>();
    codesStrategyMap.put(
        CountryCodesStrategy.COUNTRY_CODES_STRATEGY_PREFIX + "pl", countryCodesStrategy);
    codesStrategyMap.put(
        CountryCodesStrategy.UNSUPPORTED_COUNTRY_CODE_STRATEGY, countryCodesStrategy);
    countryCodesService = new CountryCodesServiceImpl(countryCodesRepository, codesStrategyMap);
  }

  @Test
  void whenGetAllCountryCodes_shouldSuccess() {
    List<CountryCodesEntity> countryCodesEntityList = new ArrayList<>();
    countryCodesEntityList.add(new CountryCodesEntity(131L, "Poland", "Polska", "1", "image"));

    List<CountryCodesResponseDto> expectedCountryCodesResponseDtos = new ArrayList<>();
    expectedCountryCodesResponseDtos.add(new CountryCodesResponseDto(131L, "Polska", "1", "image"));

    when(countryCodesRepository.findAll()).thenReturn(countryCodesEntityList);
    when(countryCodesStrategy.getCountryCodeResponseDto(countryCodesEntityList))
        .thenReturn(expectedCountryCodesResponseDtos);

    List<CountryCodesResponseDto> actualCountryCodesResponseDtos =
        countryCodesService.getAllCountryCodes("pl");

    assertEquals(expectedCountryCodesResponseDtos, actualCountryCodesResponseDtos);
    verify(countryCodesRepository, times(1)).findAll();
  }
}

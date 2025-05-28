package com.andersenlab.etalon.infoservice.service;

import com.andersenlab.etalon.infoservice.dto.response.CountryCodesResponseDto;
import java.util.List;

public interface CountryCodesService {

  List<CountryCodesResponseDto> getAllCountryCodes(String locale);
}

package com.andersenlab.etalon.infoservice.service;

import com.andersenlab.etalon.infoservice.dto.request.BaseEmailRequestDto;

public interface EmailPostProcessorService {
  void postProcess(BaseEmailRequestDto requestDto);
}

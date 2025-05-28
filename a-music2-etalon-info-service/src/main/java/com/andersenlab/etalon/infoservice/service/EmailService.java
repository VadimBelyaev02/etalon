package com.andersenlab.etalon.infoservice.service;

import com.andersenlab.etalon.infoservice.dto.request.BaseEmailRequestDto;

public interface EmailService {
  void sendEmail(BaseEmailRequestDto emailRequest);

  void processSendEmail(BaseEmailRequestDto baseEmailRequestDto);
}

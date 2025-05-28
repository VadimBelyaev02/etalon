package com.andersenlab.etalon.infoservice.service.impl;

import com.andersenlab.etalon.infoservice.dto.request.BaseEmailRequestDto;
import com.andersenlab.etalon.infoservice.service.EmailPostProcessorService;
import com.andersenlab.etalon.infoservice.service.EmailService;
import com.andersenlab.etalon.infoservice.service.EmailStrategyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.SendRawEmailRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

  private final SesClient sesClient;
  private final EmailStrategyService emailStrategyService;
  private final EmailPostProcessorService emailPostProcessorService;

  @Override
  public void processSendEmail(BaseEmailRequestDto baseEmailRequestDto) {
    SendRawEmailRequest message = emailStrategyService.buildMessage(baseEmailRequestDto);
    log.info("{processSendEmail} email send to email -> {}", baseEmailRequestDto.getToEmail());
    this.sesClient.sendRawEmail(message);
  }

  @Override
  public void sendEmail(BaseEmailRequestDto emailRequest) {
    emailPostProcessorService.postProcess(emailRequest);
  }
}

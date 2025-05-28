package com.andersenlab.etalon.infoservice.controller.impl;

import com.andersenlab.etalon.infoservice.controller.EmailController;
import com.andersenlab.etalon.infoservice.dto.request.BaseEmailRequestDto;
import com.andersenlab.etalon.infoservice.interceptor.AuthenticationHolder;
import com.andersenlab.etalon.infoservice.service.EmailService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(EmailController.API_V1_URI)
@Tag(name = "Email")
public class EmailControllerImpl implements EmailController {

  private final EmailService emailService;
  private final AuthenticationHolder authenticationHolder;

  @PostMapping(SEND_EMAIL)
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void sendEmail(@RequestBody BaseEmailRequestDto emailRequest) {
    emailService.sendEmail(emailRequest);
  }
}

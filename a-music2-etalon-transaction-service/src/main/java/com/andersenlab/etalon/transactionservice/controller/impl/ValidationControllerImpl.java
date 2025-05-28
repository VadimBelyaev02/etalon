package com.andersenlab.etalon.transactionservice.controller.impl;

import com.andersenlab.etalon.transactionservice.controller.ValidationController;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.StatusMessageResponseDto;
import com.andersenlab.etalon.transactionservice.interceptor.AuthenticationHolder;
import com.andersenlab.etalon.transactionservice.service.ValidationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(ValidationController.VALIDATIONS_URL)
@RestController
@Tag(name = "Validation")
public class ValidationControllerImpl implements ValidationController {

  private final ValidationService validationService;
  private final AuthenticationHolder authenticationHolder;

  @GetMapping(TEMPLATE_NAME_URI)
  public StatusMessageResponseDto isTemplateNameValid(@PathVariable String templateName) {
    String userId = authenticationHolder.getUserId();
    return validationService.validateTemplateName(templateName, userId);
  }
}

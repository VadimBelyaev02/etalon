package com.andersenlab.etalon.transactionservice.controller.impl;

import com.andersenlab.etalon.transactionservice.controller.TemplateController;
import com.andersenlab.etalon.transactionservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.transactionservice.dto.template.request.TemplatePatchRequestDto;
import com.andersenlab.etalon.transactionservice.dto.template.response.TemplateInfoResponseDto;
import com.andersenlab.etalon.transactionservice.interceptor.AuthenticationHolder;
import com.andersenlab.etalon.transactionservice.service.TemplateService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(TemplateController.TEMPLATES_URL)
@Tag(name = "Template")
public class TemplateControllerImpl implements TemplateController {

  private final TemplateService templateService;
  private final AuthenticationHolder authenticationHolder;

  @GetMapping(TEMPLATE_ID_PATH)
  public TemplateInfoResponseDto getTemplate(@PathVariable Long templateId) {
    return templateService.getTemplateById(templateId, authenticationHolder.getUserId());
  }

  @GetMapping
  public List<TemplateInfoResponseDto> getTemplates() {
    log.info("{getTemplates} -> Getting templates for user: {}", authenticationHolder.getUserId());
    return templateService.getTemplates(authenticationHolder.getUserId());
  }

  @PatchMapping(TEMPLATE_ID_PATH)
  public MessageResponseDto patchTemplate(
      @PathVariable Long templateId, @RequestBody TemplatePatchRequestDto patchRequestDto) {
    return templateService.patchTemplateById(
        templateId, patchRequestDto, authenticationHolder.getUserId());
  }

  @DeleteMapping(TEMPLATE_ID_PATH)
  public MessageResponseDto deleteTemplate(@PathVariable Long templateId) {
    return templateService.deleteTemplateById(templateId, authenticationHolder.getUserId());
  }
}

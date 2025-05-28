package com.andersenlab.etalon.transactionservice.service;

import com.andersenlab.etalon.transactionservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.transactionservice.dto.template.request.TemplatePatchRequestDto;
import com.andersenlab.etalon.transactionservice.dto.template.response.TemplateInfoResponseDto;
import java.util.List;

public interface TemplateService {

  List<TemplateInfoResponseDto> getTemplates(String userId);

  TemplateInfoResponseDto getTemplateById(Long templateId, String userId);

  MessageResponseDto patchTemplateById(
      Long templateId, TemplatePatchRequestDto patchRequestDto, String userId);

  MessageResponseDto deleteTemplateById(Long templateId, String userId);
}

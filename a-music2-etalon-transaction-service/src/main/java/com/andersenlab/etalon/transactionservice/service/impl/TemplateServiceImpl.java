package com.andersenlab.etalon.transactionservice.service.impl;

import static com.andersenlab.etalon.transactionservice.exception.BusinessException.TEMPLATE_NOT_FOUND_BY_ID;

import com.andersenlab.etalon.transactionservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.transactionservice.dto.template.request.TemplatePatchRequestDto;
import com.andersenlab.etalon.transactionservice.dto.template.response.TemplateInfoResponseDto;
import com.andersenlab.etalon.transactionservice.entity.TemplateEntity;
import com.andersenlab.etalon.transactionservice.exception.BusinessException;
import com.andersenlab.etalon.transactionservice.mapper.TemplateMapper;
import com.andersenlab.etalon.transactionservice.repository.TemplateRepository;
import com.andersenlab.etalon.transactionservice.service.TemplateService;
import com.andersenlab.etalon.transactionservice.service.ValidationService;
import com.andersenlab.etalon.transactionservice.util.PerformUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService {

  private final TemplateRepository templateRepository;
  private final TemplateMapper templateMapper;
  private final ValidationService validationService;

  @Override
  public List<TemplateInfoResponseDto> getTemplates(String userId) {
    return templateMapper.templateEntityToListOfDtos(
        templateRepository.getTemplateEntitiesByUserIdOrderByCreateAtDesc(userId));
  }

  @Override
  public TemplateInfoResponseDto getTemplateById(Long templateId, String userId) {
    TemplateEntity templateEntity =
        templateRepository
            .findById(templateId)
            .orElseThrow(
                () ->
                    new BusinessException(
                        HttpStatus.NOT_FOUND, String.format(TEMPLATE_NOT_FOUND_BY_ID, templateId)));
    if (!userId.equals(templateEntity.getUserId())) {
      throw new BusinessException(
          HttpStatus.BAD_REQUEST, BusinessException.THIS_TEMPLATE_DOES_NOT_BELONG_TO_THIS_USER);
    }
    return templateMapper.templateEntityToDto(templateEntity);
  }

  @Override
  public MessageResponseDto patchTemplateById(
      Long templateId, TemplatePatchRequestDto patchRequestDto, String userId) {
    TemplateEntity templateEntity =
        templateRepository
            .findById(templateId)
            .orElseThrow(
                () ->
                    new BusinessException(
                        HttpStatus.NOT_FOUND, String.format(TEMPLATE_NOT_FOUND_BY_ID, templateId)));
    // validation of presence product id and correct amount
    if (!userId.equals(templateEntity.getUserId())) {
      throw new BusinessException(
          HttpStatus.BAD_REQUEST, BusinessException.THIS_TEMPLATE_DOES_NOT_BELONG_TO_THIS_USER);
    }
    if (Boolean.FALSE.equals(
            validationService.validateTemplateName(patchRequestDto.templateName(), userId).status())
        && !templateEntity.getTemplateName().equals(patchRequestDto.templateName())) {
      throw new BusinessException(
          HttpStatus.BAD_REQUEST, BusinessException.TEMPLATE_NAME_ALREADY_BEEN_SAVED);
    }
    PerformUtils.performIfPresent(
        validationService::validateAmountMoreThanOne, patchRequestDto.amount());
    PerformUtils.performIfPresent(
        validationService::validateProductType, patchRequestDto.productId());
    PerformUtils.performIfPresent(templateEntity::setProductId, patchRequestDto.productId());
    PerformUtils.performIfPresent(templateEntity::setTemplateName, patchRequestDto.templateName());
    PerformUtils.performIfPresent(templateEntity::setDescription, patchRequestDto.description());
    PerformUtils.performIfPresent(templateEntity::setAmount, patchRequestDto.amount());
    PerformUtils.performIfPresent(templateEntity::setSource, patchRequestDto.source());
    PerformUtils.performIfPresent(templateEntity::setDestination, patchRequestDto.destination());

    templateRepository.save(templateEntity);

    return new MessageResponseDto(
        MessageResponseDto.TEMPLATE_PATCH_IS_SUCCESSFUL.formatted(templateId));
  }

  @Override
  public MessageResponseDto deleteTemplateById(Long templateId, String userId) {
    TemplateEntity templateEntity =
        templateRepository
            .findById(templateId)
            .orElseThrow(
                () ->
                    new BusinessException(
                        HttpStatus.NOT_FOUND, String.format(TEMPLATE_NOT_FOUND_BY_ID, templateId)));
    if (!userId.equals(templateEntity.getUserId())) {
      throw new BusinessException(
          HttpStatus.BAD_REQUEST, BusinessException.THIS_TEMPLATE_DOES_NOT_BELONG_TO_THIS_USER);
    }
    templateRepository.delete(templateEntity);
    return new MessageResponseDto(
        MessageResponseDto.TEMPLATE_DELETE_IS_SUCCESSFUL.formatted(templateId));
  }
}

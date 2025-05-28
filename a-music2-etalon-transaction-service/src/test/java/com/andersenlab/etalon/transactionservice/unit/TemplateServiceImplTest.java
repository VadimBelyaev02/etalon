package com.andersenlab.etalon.transactionservice.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.andersenlab.etalon.transactionservice.MockData;
import com.andersenlab.etalon.transactionservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.transactionservice.dto.template.response.TemplateInfoResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.StatusMessageResponseDto;
import com.andersenlab.etalon.transactionservice.exception.BusinessException;
import com.andersenlab.etalon.transactionservice.mapper.TemplateMapper;
import com.andersenlab.etalon.transactionservice.repository.TemplateRepository;
import com.andersenlab.etalon.transactionservice.service.impl.TemplateServiceImpl;
import com.andersenlab.etalon.transactionservice.service.impl.ValidationServiceImpl;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TemplateServiceImplTest {
  @Mock private TemplateRepository templateRepository;
  @Mock private ValidationServiceImpl validationService;
  @Spy protected TemplateMapper templateMapper = Mappers.getMapper(TemplateMapper.class);
  @InjectMocks private TemplateServiceImpl templateService;

  @Test
  void whenGetTemplate_thenSuccess() {
    // given
    when(templateRepository.findById(1L))
        .thenReturn(Optional.of(MockData.getValidTemplateEntityForPayments()));
    TemplateInfoResponseDto expectedResult = MockData.getValidTemplateInfoResponseDtoForPayments();
    // when
    TemplateInfoResponseDto actualResult = templateService.getTemplateById(1L, "user");
    // then
    assertEquals(expectedResult, actualResult);
  }

  @Test
  void whenGetTemplate_for_wrongUser_thenFail() {
    // given
    when(templateRepository.findById(1L))
        .thenReturn(Optional.of(MockData.getValidTemplateEntityForPayments()));
    // when
    // then
    assertThrows(
        BusinessException.class, () -> templateService.getTemplateById(1L, "not_this_user"));
  }

  @Test
  void whenDeleteTemplate_thenSuccess() {
    // given
    when(templateRepository.findById(1L))
        .thenReturn(Optional.of(MockData.getValidTemplateEntityForPayments()));
    MessageResponseDto expectedResult =
        new MessageResponseDto(String.format(MessageResponseDto.TEMPLATE_DELETE_IS_SUCCESSFUL, 1L));
    // when
    MessageResponseDto actualResult = templateService.deleteTemplateById(1L, "user");
    // then
    assertEquals(expectedResult, actualResult);
  }

  @Test
  void whenDeleteTemplate_for_wrongUser_thenFail() {
    // given
    when(templateRepository.findById(1L))
        .thenReturn(Optional.of(MockData.getValidTemplateEntityForPayments()));
    // when
    // then
    assertThrows(
        BusinessException.class, () -> templateService.deleteTemplateById(1L, "not_this_user"));
  }

  @Test
  void whenDeleteNonExistingTemplate_thenFail() {
    // given
    when(templateRepository.findById(1L)).thenReturn(Optional.empty());
    // when
    // then
    assertThrows(BusinessException.class, () -> templateService.deleteTemplateById(1L, "user"));
  }

  @Test
  void whenPatchTemplate_thenSuccess() {
    // given
    when(templateRepository.findById(1L))
        .thenReturn(Optional.of(MockData.getValidTemplateEntityForPayments()));
    when(validationService.validateTemplateName(
            MockData.getValidTemplatePatchRequestDto().templateName(), "user"))
        .thenReturn(
            new StatusMessageResponseDto(true, StatusMessageResponseDto.TEMPLATE_NAME_IS_VALID));
    MessageResponseDto expectedResult =
        new MessageResponseDto(String.format(MessageResponseDto.TEMPLATE_PATCH_IS_SUCCESSFUL, 1L));
    // when
    MessageResponseDto actualResult =
        templateService.patchTemplateById(1L, MockData.getValidTemplatePatchRequestDto(), "user");
    // then
    assertEquals(expectedResult, actualResult);
  }

  @Test
  void whenPatchTemplate_for_wrongUser_thenFail() {
    // given
    when(templateRepository.findById(1L))
        .thenReturn(Optional.of(MockData.getValidTemplateEntityForPayments()));
    // when
    // then
    assertThrows(
        BusinessException.class,
        () ->
            templateService.patchTemplateById(
                1L, MockData.getValidTemplatePatchRequestDto(), "not_this_user"));
  }

  @Test
  void whenPatchTemplateWithAbsentId_thenFail() {
    // given
    when(templateRepository.findById(1L)).thenReturn(Optional.empty());
    // when
    // then
    assertThrows(
        BusinessException.class,
        () ->
            templateService.patchTemplateById(
                1L, MockData.getValidTemplatePatchRequestDto(), "user"));
  }

  @Test
  void whenPatchTemplateWithWrongAmount_thenFail() {
    // given
    when(templateRepository.findById(1L))
        .thenReturn(Optional.of(MockData.getValidTemplateEntityForPayments()));
    when(validationService.validateTemplateName(
            MockData.getValidTemplatePatchRequestDto().templateName(), "user"))
        .thenReturn(
            new StatusMessageResponseDto(true, StatusMessageResponseDto.TEMPLATE_NAME_IS_VALID));
    doThrow(BusinessException.class)
        .when(validationService)
        .validateAmountMoreThanOne(any(BigDecimal.class));
    // when
    // then
    assertThrows(
        BusinessException.class,
        () ->
            templateService.patchTemplateById(
                1L, MockData.getValidTemplatePatchRequestDto(), "user"));
  }

  @Test
  void whenPatchTemplateWithExistName_thenFail() {
    // given
    when(templateRepository.findById(1L))
        .thenReturn(Optional.of(MockData.getValidTemplateEntityForPayments()));
    doThrow(BusinessException.class)
        .when(validationService)
        .validateTemplateName(any(String.class), anyString());

    // when
    // then
    assertThrows(
        BusinessException.class,
        () ->
            templateService.patchTemplateById(
                1L, MockData.getValidTemplatePatchRequestDto(), "user"));
  }

  @Test
  void whenGetTemplates_thenSuccess() {
    // given
    when(templateRepository.getTemplateEntitiesByUserIdOrderByCreateAtDesc(any(String.class)))
        .thenReturn(List.of(MockData.getValidTemplateEntityForPayments()));
    List<TemplateInfoResponseDto> expectedResult =
        List.of(MockData.getValidTemplateInfoResponseDtoForPayments());
    // when
    List<TemplateInfoResponseDto> actualResult = templateService.getTemplates("user");
    // then
    assertEquals(expectedResult, actualResult);
  }
}

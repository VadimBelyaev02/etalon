package com.andersenlab.etalon.transactionservice.unit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.andersenlab.etalon.transactionservice.MockData;
import com.andersenlab.etalon.transactionservice.exception.BusinessException;
import com.andersenlab.etalon.transactionservice.repository.TemplateRepository;
import com.andersenlab.etalon.transactionservice.service.ValidationService;
import com.andersenlab.etalon.transactionservice.service.impl.ValidationServiceImpl;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class ValidationServiceImplTest {
  @Mock private TemplateRepository templateRepository;
  @Mock private ValidationService validationService;
  @InjectMocks private ValidationServiceImpl underTest;
  private static final String USER_ID = "1L";

  @Test
  void whenValidateOwnership_thenFail() {
    assertThrows(
        BusinessException.class,
        () ->
            underTest.validateOwnership(
                List.of("PL1"),
                List.of("PL2"),
                new BusinessException(HttpStatus.BAD_REQUEST, "test")));
  }

  @Test
  void whenValidateOwnership_thenSuccess() {
    BusinessException exception = new BusinessException(HttpStatus.BAD_REQUEST, "test");
    validationService.validateOwnership(List.of("PL1"), List.of("PL1"), exception);
    verify(validationService, times(1))
        .validateOwnership(List.of("PL1"), List.of("PL1"), exception);
  }

  @Test
  void whenValidatePaymentAmountWithWrongScale_thenFail() {
    assertThrows(
        BusinessException.class,
        () -> underTest.validateAmountMoreThanOne(BigDecimal.valueOf(3.256)));
  }

  @Test
  void whenValidatePaymentAmountWithZeroAmount_thenFail() {
    assertThrows(
        BusinessException.class, () -> underTest.validateAmountMoreThanOne(BigDecimal.valueOf(0)));
  }

  @Test
  void whenValidatePaymentAmountWithNegativeAmount_thenFail() {
    assertThrows(
        BusinessException.class, () -> underTest.validateAmountMoreThanOne(BigDecimal.valueOf(-1)));
  }

  @Test
  void whenValidatePaymentAmountWithNullValue_thenFail() {
    assertThrows(BusinessException.class, () -> underTest.validateAmountMoreThanOne(null));
  }

  @Test
  void whenValidateTransactionAmountWithWrongScale_thenFail() {
    assertThrows(
        BusinessException.class,
        () -> underTest.validateAmountMoreThanZero(BigDecimal.valueOf(3.256)));
  }

  @Test
  void whenValidateTransactionAmountWithZeroAmount_thenFail() {
    assertThrows(
        BusinessException.class, () -> underTest.validateAmountMoreThanZero(BigDecimal.valueOf(0)));
  }

  @Test
  void whenValidateTransactionAmountWithNegativeAmount_thenFail() {
    assertThrows(
        BusinessException.class,
        () -> underTest.validateAmountMoreThanZero(BigDecimal.valueOf(-1)));
  }

  @Test
  void whenValidateTransactionAmountWithNullValue_thenFail() {
    assertThrows(BusinessException.class, () -> underTest.validateAmountMoreThanZero(null));
  }

  @Test
  void whenCheckIsTemplateNameValid_thenFail() {
    // given
    when(templateRepository.findByTemplateNameAndUserId("name", USER_ID))
        .thenReturn(MockData.getValidTemplateEntityForPayments());
    // when
    boolean status = underTest.validateTemplateName("name", USER_ID).status();
    // then
    assertFalse(status);
  }

  @Test
  void whenCheckIsTemplateNameValid_thenSuccess() {
    // given
    when(templateRepository.findByTemplateNameAndUserId("name2", USER_ID)).thenReturn(null);
    // when
    boolean status = underTest.validateTemplateName("name2", USER_ID).status();
    // then
    assertTrue(status);
  }
}

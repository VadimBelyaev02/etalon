package com.andersenlab.etalon.loanservice.config;

import com.andersenlab.etalon.loanservice.dto.loan.annotation.LoanPaymentRequest;
import com.andersenlab.etalon.loanservice.dto.loan.request.ActiveLoanPaymentRequestDto;
import com.andersenlab.etalon.loanservice.dto.loan.request.DelinquentLoanPaymentRequestDto;
import com.andersenlab.etalon.loanservice.dto.loan.request.LoanPaymentRequestDto;
import com.andersenlab.etalon.loanservice.entity.LoanEntity;
import com.andersenlab.etalon.loanservice.exception.BusinessException;
import com.andersenlab.etalon.loanservice.repository.LoanRepository;
import com.andersenlab.etalon.loanservice.util.enums.LoanStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;

@Component
public class LoanPaymentRequestArgumentResolver implements HandlerMethodArgumentResolver {
  private final LoanRepository loanRepository;
  private final ObjectMapper objectMapper;
  private final Validator validator;

  @Autowired
  public LoanPaymentRequestArgumentResolver(
      LoanRepository loanRepository, ObjectMapper objectMapper, Validator validator) {
    this.loanRepository = loanRepository;
    this.objectMapper = objectMapper;
    this.validator = validator;
  }

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.hasParameterAnnotation(LoanPaymentRequest.class)
        && LoanPaymentRequestDto.class.isAssignableFrom(parameter.getParameterType());
  }

  @Override
  public Object resolveArgument(
      MethodParameter parameter,
      ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest,
      WebDataBinderFactory binderFactory)
      throws Exception {

    Map<String, String> pathVariables =
        (Map<String, String>)
            webRequest.getAttribute(
                HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
    String loanIdStr = pathVariables.get("loanId");
    if (loanIdStr == null) {
      throw new BusinessException(HttpStatus.BAD_REQUEST, "Missing loanId path variable");
    }
    Long loanId;
    try {
      loanId = Long.valueOf(loanIdStr);
    } catch (NumberFormatException e) {
      throw new BusinessException(HttpStatus.BAD_REQUEST, "Invalid loanId format");
    }

    LoanEntity loanEntity =
        loanRepository
            .findById(loanId)
            .orElseThrow(
                () ->
                    new BusinessException(
                        HttpStatus.NOT_FOUND,
                        String.format(BusinessException.LOAN_NOT_FOUND_BY_ID, loanId)));

    LoanStatus loanStatus = loanEntity.getStatus();

    HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
    if (servletRequest == null) {
      throw new BusinessException(HttpStatus.BAD_REQUEST, "Invalid request");
    }

    String body =
        servletRequest.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
    if (body.isEmpty()) {
      throw new BusinessException(HttpStatus.BAD_REQUEST, "Request body is empty");
    }

    JsonNode requestBody;
    try {
      requestBody = objectMapper.readTree(body);
    } catch (Exception e) {
      throw new BusinessException(HttpStatus.BAD_REQUEST, "Malformed JSON request");
    }

    LoanPaymentRequestDto paymentRequestDto;
    try {
      if (loanStatus == LoanStatus.ACTIVE) {
        paymentRequestDto =
            objectMapper.treeToValue(requestBody, ActiveLoanPaymentRequestDto.class);
      } else if (loanStatus == LoanStatus.DELINQUENT) {
        paymentRequestDto =
            objectMapper.treeToValue(requestBody, DelinquentLoanPaymentRequestDto.class);
      } else {
        throw new BusinessException(
            HttpStatus.BAD_REQUEST,
            String.format(BusinessException.UNSUPPORTED_LOAN_STATUS, loanStatus));
      }
    } catch (JsonProcessingException e) {
      throw new BusinessException(HttpStatus.BAD_REQUEST, BusinessException.INVALID_REQUEST_DATA);
    }

    Set<ConstraintViolation<LoanPaymentRequestDto>> violations =
        validator.validate(paymentRequestDto);
    if (!violations.isEmpty()) {
      String errors =
          violations.stream()
              .map(ConstraintViolation::getMessage)
              .collect(Collectors.joining(", "));
      throw new BusinessException(HttpStatus.BAD_REQUEST, errors);
    }

    return paymentRequestDto;
  }
}

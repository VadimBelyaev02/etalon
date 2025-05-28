package com.andersenlab.etalon.transactionservice.controller.impl;

import com.andersenlab.etalon.transactionservice.controller.PaymentTypeController;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.PaymentTypeResponseDto;
import com.andersenlab.etalon.transactionservice.service.PaymentService;
import com.andersenlab.etalon.transactionservice.util.filter.PaymentFilter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(PaymentTypeController.PAYMENT_TYPES_URL)
@RestController
@Tag(name = "Payment Type")
public class PaymentTypeControllerImpl implements PaymentTypeController {

  private final PaymentService paymentService;

  @GetMapping()
  public List<PaymentTypeResponseDto> getPaymentTypes(@ParameterObject PaymentFilter filter) {
    return paymentService.getAllPaymentTypes(filter);
  }

  @GetMapping(PAYMENT_TYPE_ID_URI)
  public PaymentTypeResponseDto getPaymentType(@PathVariable Long paymentTypeId) {
    return paymentService.getPaymentTypeById(paymentTypeId);
  }
}

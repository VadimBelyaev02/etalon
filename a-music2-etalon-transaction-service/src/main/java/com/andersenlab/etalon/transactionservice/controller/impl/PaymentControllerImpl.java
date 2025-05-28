package com.andersenlab.etalon.transactionservice.controller.impl;

import com.andersenlab.etalon.transactionservice.controller.PaymentController;
import com.andersenlab.etalon.transactionservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.request.PaymentRequestDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.CreatePaymentResponseDto;
import com.andersenlab.etalon.transactionservice.interceptor.AuthenticationHolder;
import com.andersenlab.etalon.transactionservice.service.PaymentService;
import com.andersenlab.etalon.transactionservice.util.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(PaymentController.PAYMENTS_URL)
@RestController
@Tag(name = "Payment")
public class PaymentControllerImpl implements PaymentController {

  private final AuthenticationHolder authenticationHolder;
  private final PaymentService paymentService;

  @PostMapping
  public CreatePaymentResponseDto createPayment(@RequestBody PaymentRequestDto paymentRequestDto) {
    log.info("{createPayment} -> Creating payment for user {}", authenticationHolder.getUserId());
    return paymentService.createPayment(paymentRequestDto, authenticationHolder.getUserId());
  }

  @PostMapping(PAYMENT_CONFIRM_URL)
  @ResponseStatus(HttpStatus.CREATED)
  public MessageResponseDto processCreatingPayment(@PathVariable final Long paymentId) {
    return paymentService.processCreatingPayment(paymentId);
  }

  @PutMapping(PAYMENT_STATUS_UPDATE_URL)
  public void confirmPaymentStatus(
      @PathVariable Long paymentId, @RequestParam PaymentStatus status) {
    paymentService.updatePaymentStatus(paymentId, status);
  }
}

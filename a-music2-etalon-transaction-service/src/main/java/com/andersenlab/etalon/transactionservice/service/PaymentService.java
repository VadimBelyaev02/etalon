package com.andersenlab.etalon.transactionservice.service;

import com.andersenlab.etalon.transactionservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.request.PaymentRequestDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.CreatePaymentResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.PaymentTypeResponseDto;
import com.andersenlab.etalon.transactionservice.entity.PaymentEntity;
import com.andersenlab.etalon.transactionservice.util.enums.PaymentStatus;
import com.andersenlab.etalon.transactionservice.util.filter.PaymentFilter;
import java.util.List;

public interface PaymentService {

  CreatePaymentResponseDto createPayment(PaymentRequestDto paymentRequestDto, String userId);

  List<PaymentTypeResponseDto> getAllPaymentTypes(PaymentFilter filter);

  MessageResponseDto processCreatingPayment(Long paymentId);

  PaymentTypeResponseDto getPaymentTypeById(Long paymentTypeId);

  void updatePaymentStatus(Long paymentId, PaymentStatus newStatus);

  PaymentEntity getPaymentById(long paymentId);
}

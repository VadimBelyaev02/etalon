package com.andersenlab.etalon.transactionservice.mapper;

import com.andersenlab.etalon.transactionservice.dto.transaction.request.PaymentRequestDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.FinePaymentTypeResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.PaymentTypeResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.TaxPaymentTypeResponseDto;
import com.andersenlab.etalon.transactionservice.entity.PaymentEntity;
import com.andersenlab.etalon.transactionservice.entity.PaymentTypeEntity;
import com.andersenlab.etalon.transactionservice.util.OperationUtils;
import com.andersenlab.etalon.transactionservice.util.enums.PaymentStatus;
import com.andersenlab.etalon.transactionservice.util.enums.PaymentType;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class PaymentMapper {
  private static final String TAXES_AND_FINES = "TAXES_AND_FINES";

  public List<PaymentTypeResponseDto> paymentTypeEntitiesToDto(
      List<PaymentTypeEntity> paymentTypes) {
    return paymentTypes.stream()
        .map(
            paymentType -> {
              if (PaymentType.TAXES.equals(paymentType.getType()))
                return taxPaymentTypeEntityToDto(paymentType);
              if (PaymentType.FINES.equals(paymentType.getType()))
                return finePaymentTypeEntityToDto(paymentType);
              return paymentTypeEntityToDto(paymentType);
            })
        .toList();
  }

  @Mapping(target = "subType", constant = "DEFAULT")
  @Mapping(target = "fee", constant = OperationUtils.BANK_PAYMENT_COMMISSION)
  public abstract PaymentTypeResponseDto paymentTypeEntityToDto(PaymentTypeEntity paymentType);

  @Mapping(target = "taxType", source = "taxEntity.taxType")
  @Mapping(target = "recipientName", source = "taxEntity.recipientName")
  @Mapping(target = "taxDepartmentName", source = "taxEntity.taxDepartmentEntity.name")
  @Mapping(target = "type", constant = TAXES_AND_FINES)
  @Mapping(target = "subType", constant = "TAX")
  @Mapping(target = "fee", constant = OperationUtils.BANK_PAYMENT_COMMISSION)
  public abstract TaxPaymentTypeResponseDto taxPaymentTypeEntityToDto(
      PaymentTypeEntity paymentType);

  @Mapping(target = "fineType", source = "fineEntity.fineType")
  @Mapping(target = "recipientName", source = "fineEntity.recipientName")
  @Mapping(target = "type", constant = TAXES_AND_FINES)
  @Mapping(target = "subType", constant = "FINE")
  @Mapping(target = "fee", constant = OperationUtils.BANK_PAYMENT_COMMISSION)
  public abstract FinePaymentTypeResponseDto finePaymentTypeEntityToDto(
      PaymentTypeEntity paymentType);

  @Mapping(target = "userId", source = "userId")
  @Mapping(target = "amount", source = "paymentRequestDto.amount")
  @Mapping(target = "accountNumberWithdrawn", source = "paymentRequestDto.accountNumberWithdrawn")
  @Mapping(target = "paymentProductId", source = "paymentRequestDto.paymentProductId")
  @Mapping(target = "comment", source = "paymentRequestDto.comment")
  @Mapping(target = "isTemplate", source = "paymentRequestDto.isTemplate")
  @Mapping(target = "templateName", source = "paymentRequestDto.templateName")
  @Mapping(target = "status", source = "status")
  public abstract PaymentEntity toPaymentEntity(
      String userId, PaymentRequestDto paymentRequestDto, PaymentStatus status);
}

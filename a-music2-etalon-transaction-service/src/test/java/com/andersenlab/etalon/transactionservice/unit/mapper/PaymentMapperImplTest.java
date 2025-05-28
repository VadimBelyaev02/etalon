package com.andersenlab.etalon.transactionservice.unit.mapper;

import com.andersenlab.etalon.transactionservice.MockData;
import com.andersenlab.etalon.transactionservice.entity.PaymentTypeEntity;
import com.andersenlab.etalon.transactionservice.entity.TaxDepartmentEntity;
import com.andersenlab.etalon.transactionservice.entity.TaxEntity;
import com.andersenlab.etalon.transactionservice.mapper.PaymentMapper;
import com.andersenlab.etalon.transactionservice.util.enums.PaymentType;
import com.andersenlab.etalon.transactionservice.util.enums.TaxType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class PaymentMapperImplTest {
  PaymentMapper target = Mappers.getMapper(PaymentMapper.class);

  @Test
  void whenPaymentTypeEntityToDtoNull_thenReturnNull() {
    Assertions.assertThat(target.paymentTypeEntityToDto(null)).isNull();
  }

  @Test
  void whenTaxPaymentTypeEntityToDtoNull_thenReturnNull() {
    Assertions.assertThat(target.taxPaymentTypeEntityToDto(null)).isNull();
  }

  @Test
  void whenFinePaymentTypeEntityToDtoNull_thenReturnNull() {
    Assertions.assertThat(target.finePaymentTypeEntityToDto(null)).isNull();
  }

  @Test
  void whenToPaymentEntityAllArgsNull_thenReturnNull() {
    Assertions.assertThat(target.toPaymentEntity(null, null, null)).isNull();
  }

  @Test
  void whenTaxPaymentTypeEntityToDtoTaxEntityNull_thenTaxTypeNull() {
    PaymentTypeEntity paymentTypeEntity =
        MockData.getFilledPaymentTypeEntityBuilder().taxEntity(null).build();
    Assertions.assertThat(target.taxPaymentTypeEntityToDto(paymentTypeEntity).getTaxType())
        .isNull();
  }

  @Test
  void whenTaxPaymentTypeEntityToDtoTaxEntityTaxTypeIsNull_thenTaxTypeIsNull() {
    PaymentTypeEntity paymentTypeEntity =
        MockData.getFilledPaymentTypeEntityBuilder()
            .taxEntity(MockData.getFilledTaxEntityBuilder().taxType(null).build())
            .build();
    Assertions.assertThat(target.taxPaymentTypeEntityToDto(paymentTypeEntity).getTaxType())
        .isNull();
  }

  @Test
  void whenTaxPaymentTypeEntityToDtoRecipientNameIsNull_thenRecipientNameIsNull() {
    PaymentTypeEntity paymentTypeEntity =
        PaymentTypeEntity.builder()
            .type(PaymentType.FINES)
            .taxEntity(TaxEntity.builder().taxType(TaxType.INCOME_TAX).build())
            .build();
    Assertions.assertThat(target.taxPaymentTypeEntityToDto(paymentTypeEntity).getRecipientName())
        .isNull();
  }

  @Test
  void whenTaxPaymentTypeEntityToDtoTaxDepartmentEntityIsNull_thenGetTaxDepartmentNameIsNull() {
    PaymentTypeEntity paymentTypeEntity =
        MockData.getFilledPaymentTypeEntityBuilder()
            .taxEntity(MockData.getFilledTaxEntityBuilder().taxDepartmentEntity(null).build())
            .build();
    Assertions.assertThat(
            target.taxPaymentTypeEntityToDto(paymentTypeEntity).getTaxDepartmentName())
        .isNull();
  }

  @Test
  void whenTaxPaymentTypeEntityToDtoTaxDepartmentNameIsNull_thenGetTaxDepartmentNameIsNull() {
    PaymentTypeEntity paymentTypeEntity =
        MockData.getFilledPaymentTypeEntityBuilder()
            .taxEntity(
                MockData.getFilledTaxEntityBuilder()
                    .taxDepartmentEntity(TaxDepartmentEntity.builder().name(null).build())
                    .build())
            .build();
    Assertions.assertThat(
            target.taxPaymentTypeEntityToDto(paymentTypeEntity).getTaxDepartmentName())
        .isNull();
  }
}

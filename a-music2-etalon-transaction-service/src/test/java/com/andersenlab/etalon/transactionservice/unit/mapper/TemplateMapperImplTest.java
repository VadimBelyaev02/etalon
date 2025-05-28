package com.andersenlab.etalon.transactionservice.unit.mapper;

import com.andersenlab.etalon.transactionservice.mapper.TemplateMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class TemplateMapperImplTest {
  TemplateMapper target = Mappers.getMapper(TemplateMapper.class);

  @Test
  void whenTemplateEntityToDtoIsNull_thenReturnNull() {
    Assertions.assertThat(target.templateEntityToDto(null)).isNull();
  }

  @Test
  void whenPaymentToTemplateIsNull_thenReturnNull() {
    Assertions.assertThat(target.paymentToTemplate(null)).isNull();
  }

  @Test
  void whenTemplateEntityToListOfDtoIsNull_thenReturnNull() {
    Assertions.assertThat(target.templateEntityToListOfDtos(null)).isNull();
  }

  @Test
  void whenTransferToTemplateIsNull_thenReturnNull() {
    Assertions.assertThat(target.transferToTemplate(null)).isNull();
  }
}

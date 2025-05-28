package com.andersenlab.etalon.transactionservice.unit.mapper;

import com.andersenlab.etalon.transactionservice.mapper.ResponseMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class ResponseMapperImplTest {
  ResponseMapper target = Mappers.getMapper(ResponseMapper.class);

  @Test
  void whenToResponseFromMessageIsNull_thenReturnIsNull() {
    Assertions.assertThat(target.toResponseFromMessage(null)).isNull();
  }

  @Test
  void whenToResponseFromConfirmationCodeIsNull_thenReturnIsNull() {
    Assertions.assertThat(target.toResponseFromConfirmationCode(null)).isNull();
  }
}

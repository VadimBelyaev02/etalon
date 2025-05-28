package com.andersenlab.etalon.transactionservice.unit.mapper;

import com.andersenlab.etalon.transactionservice.mapper.TransferTypeMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class TransferTypeMapperImplTest {
  TransferTypeMapper target = Mappers.getMapper(TransferTypeMapper.class);

  @Test
  void whenToDtoIsNull_thenReturnNull() {
    Assertions.assertThat(target.toDto(null)).isNull();
  }

  @Test
  void whenToListDtoIsNull_thenReturnNull() {
    Assertions.assertThat(target.toListDto(null)).isNull();
  }
}

package com.andersenlab.etalon.transactionservice.unit.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.andersenlab.etalon.transactionservice.MockData;
import com.andersenlab.etalon.transactionservice.entity.EventEntity;
import com.andersenlab.etalon.transactionservice.mapper.EventsMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class EventsMapperImplTest {

  EventsMapper target = Mappers.getMapper(EventsMapper.class);

  @Test
  void whenToDtoEntityIsNull_thenEventResponseDtoIsNull() {
    assertThat(target.toDto(null)).isNull();
  }

  @Test
  void whenToDtoListIsNull_thenReturnNull() {
    assertThat(target.toListDto(null)).isNull();
  }

  @Test
  void whenToDtoEntityTransactionEntityIdIsNull_thenIdIsNull() {
    EventEntity eventEntity =
        MockData.getFilledEventEntityBuilder()
            .transactionEntity(MockData.getFilledTransactionEntityBuilder().id(null).build())
            .build();
    assertThat(target.toDto(eventEntity).id()).isNull();
  }

  @Test
  void whenToDtoCreateAtIsNull_thenCreateAtIsNull() {
    EventEntity eventEntity =
        MockData.getFilledEventEntityBuilder()
            .transactionEntity(MockData.getFilledTransactionEntityBuilder().createAt(null).build())
            .build();
    assertThat(target.toDto(eventEntity).createAt()).isNull();
  }

  @Test
  void whenToDtoStatusIsNull_thenStatusIsNull() {
    EventEntity eventEntity =
        MockData.getFilledEventEntityBuilder()
            .transactionEntity(MockData.getFilledTransactionEntityBuilder().status(null).build())
            .build();
    assertThat(target.toDto(eventEntity).status()).isNull();
  }

  @Test
  void whenToDtoTransactionNameIsNull_thenNameIsNull() {
    EventEntity eventEntity =
        MockData.getFilledEventEntityBuilder()
            .transactionEntity(
                MockData.getFilledTransactionEntityBuilder().transactionName(null).build())
            .build();
    assertThat(target.toDto(eventEntity).name()).isNull();
  }
}

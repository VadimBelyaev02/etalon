package com.andersenlab.etalon.transactionservice.mapper;

import com.andersenlab.etalon.transactionservice.dto.transaction.response.EventResponseDto;
import com.andersenlab.etalon.transactionservice.entity.EventEntity;
import com.andersenlab.etalon.transactionservice.util.enums.EventType;
import java.util.List;
import java.util.Objects;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface EventsMapper {

  String PAYMENT_FOR = "Payment for";
  String BANK_COMMISSION_FOR = "Bank commission for";
  String PAYMENT_FOR_ACCRUED_INTEREST_OF_THE_LOAN = "Payment for accrued interest of the loan";

  @Mapping(target = "id", source = "transactionEntity.id")
  @Mapping(target = "createAt", source = "transactionEntity.createAt")
  @Mapping(target = "status", source = "transactionEntity.status")
  @Mapping(target = "name", source = ".", qualifiedByName = "getName")
  EventResponseDto toDto(final EventEntity entity);

  List<EventResponseDto> toListDto(final List<EventEntity> eventEntityList);

  @Named("getName")
  default String getName(EventEntity eventEntity) {
    if (Objects.isNull(eventEntity.getTransactionEntity())) {
      return null;
    }
    if (eventEntity.getEventType().equals(EventType.FEE)) {
      return eventEntity
          .getTransactionEntity()
          .getTransactionName()
          .replace(PAYMENT_FOR, BANK_COMMISSION_FOR);
    }
    if (eventEntity.getEventType().equals(EventType.PERCENTAGE)) {
      return PAYMENT_FOR_ACCRUED_INTEREST_OF_THE_LOAN;
    } else return eventEntity.getTransactionEntity().getTransactionName();
  }
}

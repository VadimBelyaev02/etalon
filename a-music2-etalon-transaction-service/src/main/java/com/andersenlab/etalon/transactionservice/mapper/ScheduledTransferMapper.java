package com.andersenlab.etalon.transactionservice.mapper;

import com.andersenlab.etalon.transactionservice.dto.transaction.response.ScheduledTransferDto;
import com.andersenlab.etalon.transactionservice.entity.ScheduledTransferEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ScheduledTransferMapper {

  @Mapping(target = "currency", source = "currency")
  ScheduledTransferDto scheduledTransferDto(ScheduledTransferEntity scheduledTransferEntity);
}

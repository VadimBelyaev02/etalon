package com.andersenlab.etalon.transactionservice.mapper;

import com.andersenlab.etalon.transactionservice.dto.auth.response.CreateConfirmationResponseDto;
import com.andersenlab.etalon.transactionservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.CreateNewTransferResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.CreateTransferResponseDto;
import com.andersenlab.etalon.transactionservice.entity.TransferEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ResponseMapper {

  CreateTransferResponseDto toResponseFromMessage(MessageResponseDto messageResponseDto);

  @Mapping(target = "transferId", source = "transfer.id")
  @Mapping(target = "amount", source = "transfer.amount")
  @Mapping(target = "sender", source = "transfer.source")
  @Mapping(target = "beneficiary", source = "transfer.destination")
  @Mapping(target = "fee", source = "transfer.fee")
  @Mapping(target = "feeRate", source = "transfer.feeRate")
  @Mapping(target = "totalAmount", expression = "java(transfer.getFee().add(transfer.getAmount()))")
  @Mapping(target = "standardRate", source = "transfer.standardRate")
  @Mapping(target = "description", source = "transfer.comment")
  CreateNewTransferResponseDto toResponseFromEntity(TransferEntity transfer);

  CreateTransferResponseDto toResponseFromConfirmationCode(
      CreateConfirmationResponseDto createConfirmationResponseDto);
}

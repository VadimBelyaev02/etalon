package com.andersenlab.etalon.transactionservice.mapper;

import com.andersenlab.etalon.transactionservice.dto.account.response.UserDataResponseDto;
import com.andersenlab.etalon.transactionservice.dto.info.response.BankInfoResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.request.TransferRequestDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.CreateNewTransferResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.TransactionDetailedResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.TransferResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.TransferTypeResponseDto;
import com.andersenlab.etalon.transactionservice.entity.TransferEntity;
import com.andersenlab.etalon.transactionservice.util.enums.TransferStatus;
import java.math.BigDecimal;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface TransferMapper {
  @Mapping(target = "userId", source = "userId")
  @Mapping(target = "amount", source = "transferRequestDto.amount")
  @Mapping(target = "source", source = "transferRequestDto.source")
  @Mapping(target = "destination", source = "transferRequestDto.destination")
  @Mapping(target = "comment", source = "transferRequestDto.comment")
  @Mapping(target = "status", source = "status")
  @Mapping(target = "transferTypeId", source = "transferRequestDto.transferTypeId")
  @Mapping(target = "isTemplate", source = "transferRequestDto.isTemplate")
  @Mapping(target = "templateName", source = "transferRequestDto.templateName")
  TransferEntity toTransferEntity(
      String userId, TransferRequestDto transferRequestDto, TransferStatus status);

  @Mapping(target = "userId", source = "userId")
  @Mapping(target = "amount", source = "transferRequestDto.amount")
  @Mapping(target = "source", source = "transferRequestDto.sender")
  @Mapping(target = "destination", source = "transferRequestDto.beneficiary")
  @Mapping(target = "comment", source = "description")
  @Mapping(target = "fee", source = "transferRequestDto.fee")
  @Mapping(target = "feeRate", source = "transferRequestDto.feeRate")
  @Mapping(target = "standardRate", source = "transferRequestDto.standardRate")
  @Mapping(target = "status", source = "status")
  TransferEntity toTransferEntity(
      String userId,
      CreateNewTransferResponseDto transferRequestDto,
      TransferStatus status,
      String description);

  @Mapping(target = "createAt", source = "transferEntity.createAt")
  @Mapping(target = "updateAt", source = "transferEntity.updateAt")
  @Mapping(target = "id", source = "transferEntity.id")
  @Mapping(target = "transferType", source = "transferTypeResponseDto")
  @Mapping(target = "transaction", source = "transactionDetailedResponseDto")
  @Mapping(target = "description", source = "transferEntity.comment")
  @Mapping(target = "amount", source = "transferEntity.amount")
  @Mapping(target = "totalAmount", source = "transferEntity", qualifiedByName = "countTotalAmount")
  @Mapping(target = "fee", source = "transferEntity.fee")
  @Mapping(target = "currency", source = "transactionDetailedResponseDto.currency")
  @Mapping(target = "beneficiaryFullName", source = "beneficiary", qualifiedByName = "getShortName")
  @Mapping(target = "senderFullName", source = "sender", qualifiedByName = "getFullName")
  @Mapping(target = "senderAccountNumber", source = "transferEntity.source")
  @Mapping(target = "beneficiaryAccountNumber", source = "transferEntity.destination")
  @Mapping(target = "status", source = "transferEntity.status")
  @Mapping(
      target = "beneficiaryBank",
      source = "bankInfoResponseDto.callName",
      defaultValue = "Etalon Bank")
  TransferResponseDto toTransferResponseDto(
      TransferEntity transferEntity,
      TransferTypeResponseDto transferTypeResponseDto,
      TransactionDetailedResponseDto transactionDetailedResponseDto,
      UserDataResponseDto sender,
      UserDataResponseDto beneficiary,
      BankInfoResponseDto bankInfoResponseDto);

  @Named("countTotalAmount")
  default BigDecimal countTotalAmount(TransferEntity transferEntity) {
    return Objects.nonNull(transferEntity.getFee())
        ? transferEntity.getFee().add(transferEntity.getAmount())
        : transferEntity.getAmount();
  }

  @Named("getFullName")
  default String getFullName(UserDataResponseDto userDto) {
    if (Objects.isNull(userDto)) {
      return null;
    }
    return "%s %s".formatted(userDto.firstName(), userDto.lastName());
  }

  @Named("getShortName")
  default String getShortName(UserDataResponseDto userDto) {
    if (Objects.isNull(userDto)) {
      return null;
    }
    return "%s%s".formatted(userDto.firstName(), getInitial(userDto.lastName()));
  }

  @Named("getInitial")
  default String getInitial(String name) {
    if (Objects.isNull(name) || name.isEmpty()) {
      return StringUtils.EMPTY;
    }
    return " %s".formatted(name.charAt(0) + ".");
  }
}

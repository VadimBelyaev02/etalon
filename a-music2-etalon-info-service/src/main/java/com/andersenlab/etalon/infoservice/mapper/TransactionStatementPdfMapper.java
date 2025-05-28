package com.andersenlab.etalon.infoservice.mapper;

import com.andersenlab.etalon.infoservice.dto.request.TransactionStatementPdfRequestDto;
import com.andersenlab.etalon.infoservice.dto.response.TransactionDetailedResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.UserDataResponseDto;
import com.andersenlab.etalon.infoservice.util.enums.Currency;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface TransactionStatementPdfMapper {

  @Mappings({
    @Mapping(target = "firstName", source = "userData.firstName"),
    @Mapping(target = "lastName", source = "userData.lastName"),
    @Mapping(target = "transactionDate", source = "transaction.transactionDate"),
    @Mapping(target = "transactionTime", source = "transaction.transactionTime"),
    @Mapping(target = "transactionName", source = "transaction.transactionName"),
    @Mapping(target = "outcomeAccountNumber", source = "transaction.outcomeAccountNumber"),
    @Mapping(target = "incomeAccountNumber", source = "transaction.incomeAccountNumber"),
    @Mapping(target = "transactionAmount", source = "transaction.transactionAmount"),
    @Mapping(target = "currency", source = "currency"),
    @Mapping(target = "type", source = "transaction.type"),
    @Mapping(target = "status", source = "transaction.status")
  })
  TransactionStatementPdfRequestDto toTransactionStatementPdfDto(
      UserDataResponseDto userData, TransactionDetailedResponseDto transaction, Currency currency);
}

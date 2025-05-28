package com.andersenlab.etalon.infoservice.mapper;

import com.andersenlab.etalon.infoservice.dto.common.TransferReceiptContext;
import com.andersenlab.etalon.infoservice.dto.response.TransferResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface TransferReceiptContextMapper {

  @Mappings({
    @Mapping(target = "receiptNumber", source = "transferResponseDto.id"),
    @Mapping(target = "senderFullName", source = "transferResponseDto.senderFullName"),
    @Mapping(target = "beneficiaryFullName", source = "transferResponseDto.beneficiaryFullName"),
    @Mapping(target = "senderAccountNumber", source = "transferResponseDto.senderAccountNumber"),
    @Mapping(
        target = "beneficiaryAccountNumber",
        source = "transferResponseDto.beneficiaryAccountNumber"),
    @Mapping(target = "transferDate", source = "transferResponseDto.createAt"),
    @Mapping(
        target = "beneficiaryBank",
        source = "transferResponseDto.beneficiaryBank",
        defaultValue = "Etalon Bank"),
    @Mapping(target = "description", source = "transferResponseDto.description"),
    @Mapping(target = "totalAmount", source = "transferResponseDto.totalAmount"),
    @Mapping(target = "currency", source = "transferResponseDto.currency")
  })
  TransferReceiptContext toTransferReceiptContext(TransferResponseDto transferResponseDto);
}

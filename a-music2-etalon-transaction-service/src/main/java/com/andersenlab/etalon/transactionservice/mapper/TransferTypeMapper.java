package com.andersenlab.etalon.transactionservice.mapper;

import com.andersenlab.etalon.transactionservice.dto.transaction.response.TransferTypeResponseDto;
import com.andersenlab.etalon.transactionservice.entity.TransferTypeEntity;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransferTypeMapper {

  TransferTypeResponseDto toDto(final TransferTypeEntity entity);

  List<TransferTypeResponseDto> toListDto(final List<TransferTypeEntity> entities);
}

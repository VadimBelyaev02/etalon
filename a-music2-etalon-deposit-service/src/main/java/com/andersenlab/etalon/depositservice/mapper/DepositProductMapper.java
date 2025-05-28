package com.andersenlab.etalon.depositservice.mapper;

import com.andersenlab.etalon.depositservice.dto.deposit.response.DepositProductResponseDto;
import com.andersenlab.etalon.depositservice.entity.DepositProductEntity;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DepositProductMapper {

  List<DepositProductResponseDto> toListOfDto(final List<DepositProductEntity> entities);
}

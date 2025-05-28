package com.andersenlab.etalon.infoservice.mapper;

import com.andersenlab.etalon.infoservice.dto.response.BankDetailResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.BankInfoResponseDto;
import com.andersenlab.etalon.infoservice.entity.BankDetailsEntity;
import com.andersenlab.etalon.infoservice.entity.BankInfoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BankInfoAndDetailsMapper {
  @Mapping(target = "isForeignBank", constant = "true")
  BankInfoResponseDto toBankInfoResponseDto(BankInfoEntity bankInfoEntity);

  BankDetailResponseDto toBankDetailResponseDto(BankDetailsEntity bankDetailsEntity);
}

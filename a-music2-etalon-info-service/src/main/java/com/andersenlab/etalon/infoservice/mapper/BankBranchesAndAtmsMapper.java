package com.andersenlab.etalon.infoservice.mapper;

import com.andersenlab.etalon.infoservice.dto.response.BankBranchesAndAtmsResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.BankBranchesResponseDto;
import com.andersenlab.etalon.infoservice.entity.AtmEntity;
import com.andersenlab.etalon.infoservice.entity.BankBranchEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BankBranchesAndAtmsMapper {

  @Mapping(target = "type", constant = "ATM")
  @Mapping(target = "name", source = "atmName")
  @Mapping(target = "operationModes", source = "atmOperationModes")
  BankBranchesAndAtmsResponseDto toDto(final AtmEntity entity);

  @Mapping(target = "type", constant = "BANK_BRANCH")
  @Mapping(target = "name", source = "bankBranchName")
  @Mapping(target = "operationModes", source = "bankBranchOperationModes")
  BankBranchesAndAtmsResponseDto toDto(final BankBranchEntity entity);

  @Mapping(target = "name", source = "bankBranchName")
  BankBranchesResponseDto toDtoBankBranchesResponseDto(BankBranchEntity entity);
}

package com.andersenlab.etalon.accountservice.mapper;

import com.andersenlab.etalon.accountservice.dto.account.response.AccountDetailedResponseDto;
import com.andersenlab.etalon.accountservice.dto.account.response.AccountInterestResponseDto;
import com.andersenlab.etalon.accountservice.dto.account.response.AccountResponseDto;
import com.andersenlab.etalon.accountservice.entity.AccountEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {
  AccountResponseDto toDto(final AccountEntity cardEntity);

  AccountDetailedResponseDto toDetailedDto(final AccountEntity accountEntity);

  List<AccountResponseDto> toListOfDto(final List<AccountEntity> dto);

  @Mapping(target = "accountNumber", source = "iban")
  AccountInterestResponseDto toInterestDto(final AccountEntity accountEntities);
}

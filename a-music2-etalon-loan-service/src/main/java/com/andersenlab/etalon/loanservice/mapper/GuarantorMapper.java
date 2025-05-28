package com.andersenlab.etalon.loanservice.mapper;

import com.andersenlab.etalon.loanservice.dto.loan.request.GuarantorRequestDto;
import com.andersenlab.etalon.loanservice.dto.loan.response.GuarantorResponseDto;
import com.andersenlab.etalon.loanservice.entity.GuarantorEntity;
import java.util.Set;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GuarantorMapper {

  GuarantorResponseDto toDto(final GuarantorEntity entity);

  GuarantorEntity toEntity(final GuarantorRequestDto requestDto);

  Set<GuarantorEntity> setOfDtosToEntities(final Set<GuarantorRequestDto> requestDto);
}

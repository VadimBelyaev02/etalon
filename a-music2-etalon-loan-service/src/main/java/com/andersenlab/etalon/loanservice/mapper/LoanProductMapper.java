package com.andersenlab.etalon.loanservice.mapper;

import com.andersenlab.etalon.loanservice.dto.loan.response.LoanProductResponseDto;
import com.andersenlab.etalon.loanservice.entity.LoanProductEntity;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoanProductMapper {

  LoanProductResponseDto toDto(final LoanProductEntity entity);

  List<LoanProductResponseDto> listOfLoanTypesToListOfDtos(
      final List<LoanProductEntity> loanTypeEntities);
}

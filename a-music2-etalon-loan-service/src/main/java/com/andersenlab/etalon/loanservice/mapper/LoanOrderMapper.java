package com.andersenlab.etalon.loanservice.mapper;

import com.andersenlab.etalon.loanservice.dto.loan.response.LoanOrderDetailedResponseDto;
import com.andersenlab.etalon.loanservice.dto.loan.response.LoanOrderResponseDto;
import com.andersenlab.etalon.loanservice.entity.LoanOrderEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface LoanOrderMapper {

  @Mappings({
    @Mapping(source = "product.name", target = "productName"),
    @Mapping(source = "product.duration", target = "duration")
  })
  LoanOrderResponseDto toDto(final LoanOrderEntity cardEntity);

  List<LoanOrderResponseDto> listOfLoanOrdersToListOfDtos(
      final List<LoanOrderEntity> loanOrderEntities);

  @Mappings({
    @Mapping(source = "product.duration", target = "duration"),
    @Mapping(source = "product.apr", target = "apr"),
    @Mapping(source = "product.name", target = "productName")
  })
  LoanOrderDetailedResponseDto toDetailsDto(final LoanOrderEntity entity);
}

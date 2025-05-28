package com.andersenlab.etalon.loanservice.mapper;

import com.andersenlab.etalon.loanservice.dto.loan.response.LoanDetailedResponseDto;
import com.andersenlab.etalon.loanservice.dto.loan.response.LoanResponseDto;
import com.andersenlab.etalon.loanservice.entity.LoanEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface LoanMapper {

  @Mappings({
    @Mapping(target = "productId", source = "product.id"),
    @Mapping(target = "productName", source = "product.name"),
    @Mapping(target = "duration", source = "product.duration"),
    @Mapping(target = "createdAt", source = "createAt")
  })
  LoanResponseDto loanEntityToLoanResponseDto(final LoanEntity loanProductEntity);

  @Mappings({
    @Mapping(target = "productName", source = "product.name"),
    @Mapping(target = "duration", source = "product.duration"),
    @Mapping(target = "apr", source = "product.apr"),
    @Mapping(target = "createdAt", source = "createAt")
  })
  LoanDetailedResponseDto loanEntityToLoanDetailedDto(final LoanEntity loanProductEntities);

  List<LoanResponseDto> listOfLoanProductsToListOfDtos(final List<LoanEntity> loanProductEntities);
}

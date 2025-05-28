package com.andersenlab.etalon.depositservice.mapper;

import com.andersenlab.etalon.depositservice.client.service.AccountService;
import com.andersenlab.etalon.depositservice.dto.deposit.response.DepositDetailedResponseDto;
import com.andersenlab.etalon.depositservice.dto.deposit.response.DepositInterestDto;
import com.andersenlab.etalon.depositservice.dto.deposit.response.DepositResponseDto;
import com.andersenlab.etalon.depositservice.dto.deposit.response.DepositToCloseResponseDto;
import com.andersenlab.etalon.depositservice.dto.deposit.response.DepositWithInterestResponseDto;
import com.andersenlab.etalon.depositservice.entity.DepositEntity;
import com.andersenlab.etalon.depositservice.entity.DepositInterestEntity;
import com.andersenlab.etalon.depositservice.service.facade.DepositDaoFacade;
import com.andersenlab.etalon.depositservice.util.DepositUtils;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@Mapper(componentModel = "spring")
public interface DepositMapper {
  @Mappings({
    @Mapping(target = "actualAmount", ignore = true),
    @Mapping(target = "endDate", ignore = true),
    @Mapping(target = "createdAt", source = "createAt"),
    @Mapping(target = "depositProductId", source = "product.id")
  })
  DepositResponseDto toDto(final DepositEntity entity);

  @Mappings({
    @Mapping(target = "productName", source = "product.name"),
    @Mapping(target = "productCurrency", source = "product.currency"),
    @Mapping(target = "productInterestRate", source = "product.interestRate"),
    @Mapping(target = "isProductEarlyWithdrawal", source = "product.isEarlyWithdrawal"),
    @Mapping(target = "validFrom", source = "createAt"),
    @Mapping(target = "finalTransferAccountNumber", source = "finalTransferAccountNumber"),
    @Mapping(target = "validUntil", ignore = true),
    @Mapping(target = "actualAmount", ignore = true),
    @Mapping(target = "monthlyPayments", ignore = true)
  })
  DepositDetailedResponseDto depositEntityToDepositDetailedDto(final DepositEntity depositEntity);

  List<DepositInterestDto> depositInterestEntityToListOfDtos(
      List<DepositInterestEntity> depositInterestEntities);

  @Mapping(target = "interestRate", source = "product.interestRate")
  DepositWithInterestResponseDto toDepositWithInterestResponseDto(
      final DepositEntity depositEntity);

  @Mapping(target = "depositProductName", source = "product.name")
  DepositToCloseResponseDto toDepositToCloseResponseDto(final DepositEntity depositEntity);

  default Page<DepositResponseDto> toDtoPage(
      Page<DepositEntity> entityPage,
      AccountService accountService,
      DepositDaoFacade depositDaoFacade) {
    List<DepositResponseDto> dtoList =
        entityPage.getContent().stream()
            .map(
                entity -> {
                  DepositResponseDto dto = toDto(entity);
                  return dto.toBuilder()
                      .actualAmount(
                          accountService
                              .getAccountBalanceByAccountNumber(entity.getAccountNumber())
                              .accountBalance())
                      .endDate(
                          DepositUtils.calculateDepositEndDate(
                              entity.getCreateAt(),
                              entity.getDuration(),
                              depositDaoFacade
                                  .findDepositProductById(entity.getProduct().getId())
                                  .getTerm()))
                      .build();
                })
            .toList();
    return new PageImpl<>(dtoList, entityPage.getPageable(), entityPage.getTotalElements());
  }

  default List<DepositResponseDto> toDtoList(
      List<DepositEntity> entityList,
      AccountService accountService,
      DepositDaoFacade depositDaoFacade) {
    return entityList.stream()
        .map(
            entity -> {
              DepositResponseDto dto = toDto(entity);
              return dto.toBuilder()
                  .actualAmount(
                      accountService
                          .getAccountBalanceByAccountNumber(entity.getAccountNumber())
                          .accountBalance())
                  .endDate(
                      DepositUtils.calculateDepositEndDate(
                          entity.getCreateAt(),
                          entity.getDuration(),
                          depositDaoFacade
                              .findDepositProductById(entity.getProduct().getId())
                              .getTerm()))
                  .build();
            })
        .toList();
  }
}

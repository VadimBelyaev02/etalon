package com.andersenlab.etalon.depositservice.service.business.impl;

import com.andersenlab.etalon.depositservice.client.service.AccountService;
import com.andersenlab.etalon.depositservice.config.TimeProvider;
import com.andersenlab.etalon.depositservice.dto.account.response.AccountBalanceResponseDto;
import com.andersenlab.etalon.depositservice.dto.deposit.response.DepositDetailedResponseDto;
import com.andersenlab.etalon.depositservice.dto.deposit.response.DepositResponseDto;
import com.andersenlab.etalon.depositservice.dto.deposit.response.MonthlyInterestIncomeDto;
import com.andersenlab.etalon.depositservice.entity.DepositEntity;
import com.andersenlab.etalon.depositservice.entity.DepositInterestEntity;
import com.andersenlab.etalon.depositservice.mapper.DepositMapper;
import com.andersenlab.etalon.depositservice.service.business.DepositQueryService;
import com.andersenlab.etalon.depositservice.service.facade.DepositDaoFacade;
import com.andersenlab.etalon.depositservice.util.DepositUtils;
import com.andersenlab.etalon.depositservice.util.filter.CustomPageRequest;
import com.andersenlab.etalon.depositservice.util.filter.DepositFilterRequest;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DepositQueryServiceImpl implements DepositQueryService {
  private final AccountService accountService;
  private final DepositDaoFacade depositDaoFacade;
  private final DepositMapper depositMapper;
  private final TimeProvider timeProvider;

  @Override
  public List<DepositResponseDto> getAllOpenDepositsByUserId(
      String userId, int pageNo, int pageSize) {
    Pageable paging = PageRequest.of(pageNo, pageSize);
    Page<DepositEntity> pagedResult = depositDaoFacade.findAllDepositsByUserId(paging, userId);
    return depositMapper.toDtoList(pagedResult.getContent(), accountService, depositDaoFacade);
  }

  @Override
  public DepositDetailedResponseDto getDetailedDeposit(Long depositId, String userId) {
    DepositEntity entity = depositDaoFacade.findDepositByIdAndUserId(depositId, userId);
    List<DepositInterestEntity> depositInterestEntities =
        depositDaoFacade.findAllInterestsByDepositId(entity.getId());
    List<MonthlyInterestIncomeDto> monthlyInterestIncomeDtoList =
        depositInterestEntities.isEmpty()
            ? new ArrayList<>()
            : DepositUtils.getDepositInterestHistory(
                depositMapper.depositInterestEntityToListOfDtos(depositInterestEntities),
                entity,
                timeProvider.getZone());

    AccountBalanceResponseDto accountBalanceResponseDto =
        accountService.getAccountBalanceByAccountNumber(entity.getAccountNumber());

    return depositMapper.depositEntityToDepositDetailedDto(entity).toBuilder()
        .actualAmount(accountBalanceResponseDto.accountBalance())
        .validUntil(
            DepositUtils.calculateDepositEndDate(
                entity.getCreateAt(), entity.getDuration(), entity.getProduct().getTerm()))
        .monthlyPayments(monthlyInterestIncomeDtoList)
        .build();
  }

  @Override
  public Page<DepositResponseDto> getFilteredDepositsByUserId(
      String userId, CustomPageRequest pageRequest, DepositFilterRequest filter) {
    Page<DepositEntity> depositEntityPage =
        depositDaoFacade.findFilteredDepositsByUserId(userId, pageRequest, filter);
    return depositMapper.toDtoPage(depositEntityPage, accountService, depositDaoFacade);
  }
}

package com.andersenlab.etalon.depositservice.service.facade.impl;

import com.andersenlab.etalon.depositservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.depositservice.dto.deposit.request.DepositReplenishRequestDto;
import com.andersenlab.etalon.depositservice.dto.deposit.request.DepositUpdateRequestDto;
import com.andersenlab.etalon.depositservice.dto.deposit.request.DepositWithdrawRequestDto;
import com.andersenlab.etalon.depositservice.dto.deposit.request.OpenDepositRequestDto;
import com.andersenlab.etalon.depositservice.dto.deposit.response.ConfirmationOpenDepositResponseDto;
import com.andersenlab.etalon.depositservice.dto.deposit.response.DepositDetailedResponseDto;
import com.andersenlab.etalon.depositservice.dto.deposit.response.DepositResponseDto;
import com.andersenlab.etalon.depositservice.service.business.DepositManagementService;
import com.andersenlab.etalon.depositservice.service.business.DepositQueryService;
import com.andersenlab.etalon.depositservice.service.facade.DepositFacade;
import com.andersenlab.etalon.depositservice.util.filter.CustomPageRequest;
import com.andersenlab.etalon.depositservice.util.filter.DepositFilterRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DepositFacadeImpl implements DepositFacade {

  private final DepositQueryService depositQueryService;
  private final DepositManagementService depositManagementService;

  @Override
  public List<DepositResponseDto> getAllOpenDepositsByUserId(
      String userId, int pageNo, int pageSize) {
    return depositQueryService.getAllOpenDepositsByUserId(userId, pageNo, pageSize);
  }

  @Override
  public DepositDetailedResponseDto getDetailedDeposit(Long depositId, String userId) {
    return depositQueryService.getDetailedDeposit(depositId, userId);
  }

  @Override
  @Transactional
  public MessageResponseDto replenishDeposit(
      Long depositId, DepositReplenishRequestDto requestDto, String userId) {
    return depositManagementService.replenishDeposit(depositId, requestDto, userId);
  }

  @Override
  @Transactional
  public MessageResponseDto withdrawDeposit(
      Long depositId, DepositWithdrawRequestDto requestDto, String userId) {
    return depositManagementService.withdrawDeposit(depositId, requestDto, userId);
  }

  @Override
  @Transactional
  public ConfirmationOpenDepositResponseDto openNewDeposit(
      String userId, OpenDepositRequestDto dto) {
    return depositManagementService.openNewDeposit(userId, dto);
  }

  @Override
  public Page<DepositResponseDto> getFilteredDepositsByUserId(
      String userId, CustomPageRequest pageRequest, DepositFilterRequest filter) {
    return depositQueryService.getFilteredDepositsByUserId(userId, pageRequest, filter);
  }

  @Override
  public MessageResponseDto updateDeposit(
      Long depositId, DepositUpdateRequestDto depositUpdateRequestDto, String userId) {
    return depositManagementService.updateDeposit(depositId, depositUpdateRequestDto, userId);
  }
}

package com.andersenlab.etalon.depositservice.service.business;

import com.andersenlab.etalon.depositservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.depositservice.dto.deposit.request.DepositReplenishRequestDto;
import com.andersenlab.etalon.depositservice.dto.deposit.request.DepositUpdateRequestDto;
import com.andersenlab.etalon.depositservice.dto.deposit.request.DepositWithdrawRequestDto;
import com.andersenlab.etalon.depositservice.dto.deposit.request.OpenDepositRequestDto;
import com.andersenlab.etalon.depositservice.dto.deposit.response.ConfirmationOpenDepositResponseDto;

public interface DepositManagementService {
  MessageResponseDto replenishDeposit(
      Long depositId, DepositReplenishRequestDto requestDto, String userId);

  MessageResponseDto withdrawDeposit(
      Long depositId, DepositWithdrawRequestDto requestDto, String userId);

  ConfirmationOpenDepositResponseDto openNewDeposit(String userId, OpenDepositRequestDto dto);

  MessageResponseDto updateDeposit(
      Long depositId, DepositUpdateRequestDto depositUpdateRequestDto, String userId);
}

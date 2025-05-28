package com.andersenlab.etalon.depositservice.service.facade;

import com.andersenlab.etalon.depositservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.depositservice.dto.deposit.request.DepositReplenishRequestDto;
import com.andersenlab.etalon.depositservice.dto.deposit.request.DepositUpdateRequestDto;
import com.andersenlab.etalon.depositservice.dto.deposit.request.DepositWithdrawRequestDto;
import com.andersenlab.etalon.depositservice.dto.deposit.request.OpenDepositRequestDto;
import com.andersenlab.etalon.depositservice.dto.deposit.response.ConfirmationOpenDepositResponseDto;
import com.andersenlab.etalon.depositservice.dto.deposit.response.DepositDetailedResponseDto;
import com.andersenlab.etalon.depositservice.dto.deposit.response.DepositResponseDto;
import com.andersenlab.etalon.depositservice.util.filter.CustomPageRequest;
import com.andersenlab.etalon.depositservice.util.filter.DepositFilterRequest;
import java.util.List;
import org.springframework.data.domain.Page;

public interface DepositFacade {

  List<DepositResponseDto> getAllOpenDepositsByUserId(String userId, int pageNo, int pageSize);

  DepositDetailedResponseDto getDetailedDeposit(final Long depositId, final String userId);

  MessageResponseDto replenishDeposit(
      Long depositId, DepositReplenishRequestDto depositReplenishRequestDto, String userId);

  MessageResponseDto withdrawDeposit(
      Long depositId, DepositWithdrawRequestDto depositWithdrawRequestDto, String userId);

  ConfirmationOpenDepositResponseDto openNewDeposit(
      final String userId, final OpenDepositRequestDto dto);

  Page<DepositResponseDto> getFilteredDepositsByUserId(
      String userId, CustomPageRequest pageRequest, DepositFilterRequest filter);

  MessageResponseDto updateDeposit(
      Long depositId, DepositUpdateRequestDto depositUpdateRequestDto, String userId);
}

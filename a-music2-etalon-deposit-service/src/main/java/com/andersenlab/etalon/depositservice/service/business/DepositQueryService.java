package com.andersenlab.etalon.depositservice.service.business;

import com.andersenlab.etalon.depositservice.dto.deposit.response.DepositDetailedResponseDto;
import com.andersenlab.etalon.depositservice.dto.deposit.response.DepositResponseDto;
import com.andersenlab.etalon.depositservice.util.filter.CustomPageRequest;
import com.andersenlab.etalon.depositservice.util.filter.DepositFilterRequest;
import java.util.List;
import org.springframework.data.domain.Page;

public interface DepositQueryService {
  List<DepositResponseDto> getAllOpenDepositsByUserId(String userId, int pageNo, int pageSize);

  DepositDetailedResponseDto getDetailedDeposit(Long depositId, String userId);

  Page<DepositResponseDto> getFilteredDepositsByUserId(
      String userId, CustomPageRequest pageRequest, DepositFilterRequest filter);
}

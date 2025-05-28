package com.andersenlab.etalon.depositservice.service.business;

import com.andersenlab.etalon.depositservice.dto.common.response.MessageResponseDto;

public interface DepositOrderService {
  MessageResponseDto processOpeningNewDeposit(final Long depositOrderId);

  void proceedOpeningNewDeposit(Long transactionId);
}

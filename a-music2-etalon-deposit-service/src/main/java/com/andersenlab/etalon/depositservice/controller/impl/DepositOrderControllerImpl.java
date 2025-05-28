package com.andersenlab.etalon.depositservice.controller.impl;

import com.andersenlab.etalon.depositservice.controller.DepositOrderController;
import com.andersenlab.etalon.depositservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.depositservice.service.business.DepositOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(DepositOrderController.DEPOSIT_ORDER_URL)
@RestController
@RequiredArgsConstructor
public class DepositOrderControllerImpl implements DepositOrderController {

  private final DepositOrderService depositOrderService;

  @PostMapping(DEPOSIT_ORDER_ID_CONFIRM_URL)
  public MessageResponseDto processOpeningNewDeposit(@PathVariable final Long depositOrderId) {
    return depositOrderService.processOpeningNewDeposit(depositOrderId);
  }
}

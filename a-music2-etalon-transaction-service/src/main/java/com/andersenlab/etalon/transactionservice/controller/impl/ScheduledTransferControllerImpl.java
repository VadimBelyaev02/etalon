package com.andersenlab.etalon.transactionservice.controller.impl;

import com.andersenlab.etalon.transactionservice.controller.ScheduledTransferController;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.CustomPageDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.ScheduledTransferDto;
import com.andersenlab.etalon.transactionservice.interceptor.AuthenticationHolder;
import com.andersenlab.etalon.transactionservice.service.ScheduledTransferService;
import com.andersenlab.etalon.transactionservice.util.CustomPageDtoUtils;
import com.andersenlab.etalon.transactionservice.util.filter.ScheduledTransferFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class ScheduledTransferControllerImpl implements ScheduledTransferController {

  private final ScheduledTransferService scheduledTransferService;
  private final AuthenticationHolder authenticationHolder;

  @GetMapping(SCHEDULED_TRANSFER_URL)
  public CustomPageDto<ScheduledTransferDto> getScheduledTransferByUserId(
      @ParameterObject ScheduledTransferFilter scheduledTransferFilter, Pageable pageable) {
    String userId = authenticationHolder.getUserId();

    Page<ScheduledTransferDto> listOfScheduledTransferDto =
        scheduledTransferService.getScheduledTransferByUserId(
            userId, scheduledTransferFilter, pageable);
    return CustomPageDtoUtils.toCustomPageDto(listOfScheduledTransferDto);
  }
}

package com.andersenlab.etalon.depositservice.controller.impl;

import com.andersenlab.etalon.depositservice.controller.DepositController;
import com.andersenlab.etalon.depositservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.depositservice.dto.deposit.request.DepositReplenishRequestDto;
import com.andersenlab.etalon.depositservice.dto.deposit.request.DepositUpdateRequestDto;
import com.andersenlab.etalon.depositservice.dto.deposit.request.DepositWithdrawRequestDto;
import com.andersenlab.etalon.depositservice.dto.deposit.request.OpenDepositRequestDto;
import com.andersenlab.etalon.depositservice.dto.deposit.response.ConfirmationOpenDepositResponseDto;
import com.andersenlab.etalon.depositservice.dto.deposit.response.DepositDetailedResponseDto;
import com.andersenlab.etalon.depositservice.dto.deposit.response.DepositResponseDto;
import com.andersenlab.etalon.depositservice.interceptor.AuthenticationHolder;
import com.andersenlab.etalon.depositservice.service.facade.DepositFacade;
import com.andersenlab.etalon.depositservice.util.filter.CustomPageRequest;
import com.andersenlab.etalon.depositservice.util.filter.DepositFilterRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping()
@RestController
@RequiredArgsConstructor
@Validated
public class DepositControllerImpl implements DepositController {

  private final DepositFacade depositFacade;
  private final AuthenticationHolder authenticationHolder;

  @GetMapping(DEPOSITS_V1_URL)
  public List<DepositResponseDto> getAllDepositsByUserId(
      @RequestParam(defaultValue = DEFAULT_PAGE_NO) int pageNo,
      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
    log.info(
        "{getAllDepositsByUserId} -> Getting request to get all deposits for user: {}",
        authenticationHolder.getUserId());

    return depositFacade.getAllOpenDepositsByUserId(
        authenticationHolder.getUserId(), pageNo, pageSize);
  }

  @GetMapping({DEPOSITS_V1_URL + DEPOSIT_ID_URI})
  public DepositDetailedResponseDto getDetailedDeposit(@PathVariable final Long depositId) {
    return depositFacade.getDetailedDeposit(depositId, authenticationHolder.getUserId());
  }

  @PostMapping({DEPOSITS_V1_URL + DEPOSIT_ID_REPLENISHED_URL})
  public MessageResponseDto replenishDeposit(
      @PathVariable final Long depositId,
      @RequestBody DepositReplenishRequestDto depositReplenishRequestDto) {
    return depositFacade.replenishDeposit(
        depositId, depositReplenishRequestDto, authenticationHolder.getUserId());
  }

  @PostMapping(DEPOSITS_V1_URL + DEPOSIT_ID_WITHDRAWN_URL)
  public MessageResponseDto withdrawDeposit(
      @PathVariable final Long depositId,
      @RequestBody DepositWithdrawRequestDto depositWithdrawRequestDto) {
    return depositFacade.withdrawDeposit(
        depositId, depositWithdrawRequestDto, authenticationHolder.getUserId());
  }

  @PostMapping(DEPOSITS_V1_URL)
  public ConfirmationOpenDepositResponseDto openNewDeposit(@RequestBody OpenDepositRequestDto dto) {
    return depositFacade.openNewDeposit(authenticationHolder.getUserId(), dto);
  }

  @GetMapping(DEPOSITS_V2_URL)
  public Page<DepositResponseDto> getFilteredDepositsByUserId(
      @ParameterObject @Valid CustomPageRequest pageRequest,
      @ParameterObject @Valid DepositFilterRequest filter) {
    return depositFacade.getFilteredDepositsByUserId(
        authenticationHolder.getUserId(), pageRequest, filter);
  }

  @PatchMapping(DEPOSITS_V1_URL + DEPOSIT_ID_URI)
  public MessageResponseDto patchDeposit(
      @PathVariable Long depositId,
      @Valid @RequestBody DepositUpdateRequestDto depositUpdateRequestDto) {
    return depositFacade.updateDeposit(
        depositId, depositUpdateRequestDto, authenticationHolder.getUserId());
  }
}

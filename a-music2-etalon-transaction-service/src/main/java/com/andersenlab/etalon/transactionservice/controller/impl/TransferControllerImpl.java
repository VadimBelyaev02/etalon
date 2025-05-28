package com.andersenlab.etalon.transactionservice.controller.impl;

import com.andersenlab.etalon.transactionservice.controller.TransferController;
import com.andersenlab.etalon.transactionservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.request.CreateTransferRequestDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.CreateNewTransferResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.CreateTransferResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.TransferResponseDto;
import com.andersenlab.etalon.transactionservice.interceptor.AuthenticationHolder;
import com.andersenlab.etalon.transactionservice.service.TransferService;
import com.andersenlab.etalon.transactionservice.util.enums.ConfirmationMethod;
import com.andersenlab.etalon.transactionservice.util.enums.TransferStatus;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Transfer")
public class TransferControllerImpl implements TransferController {

  private final AuthenticationHolder authenticationHolder;
  private final TransferService transferService;

  @PostMapping(TRANSFER_CONFIRMED_V2_URL)
  @ResponseStatus(HttpStatus.CREATED)
  public MessageResponseDto processConfirmedTransfer(@PathVariable final Long transferId) {
    return transferService.processConfirmedTransfer(transferId);
  }

  @GetMapping(TRANSFERS_ID_URL)
  public TransferResponseDto getTransferById(@PathVariable final long transferId) {
    return transferService.getTransferByIdAndUserId(transferId, authenticationHolder.getUserId());
  }

  @PostMapping(TRANSFERS_V2_URL)
  @ResponseStatus(HttpStatus.CREATED)
  public CreateNewTransferResponseDto createTransfer(
      @RequestParam(defaultValue = "false") boolean isTransient,
      @RequestBody CreateTransferRequestDto transferRequestDto) {
    log.info(
        "{calculatingPayment} -> Calculating transfer for user {}",
        authenticationHolder.getUserId());

    return transferService.createTransfer(
        transferRequestDto, authenticationHolder.getUserId(), isTransient);
  }

  @PostMapping(TRANSFER_CONFIRM_V2_URL)
  @ResponseStatus(HttpStatus.CREATED)
  public CreateTransferResponseDto createConfirmation(
      @PathVariable final long transferId, @PathVariable ConfirmationMethod confirmationMethod) {
    return transferService.createConfirmation(transferId, confirmationMethod);
  }

  @DeleteMapping(TRANSFERS_ID_URL)
  @ResponseStatus(HttpStatus.OK)
  public void deleteTransfer(@PathVariable final long transferId) {
    transferService.deleteTransfer(transferId);
  }

  @PutMapping(TRANSFER_STATUS_UPDATE_URL)
  public void confirmTransferStatus(
      @PathVariable Long transferId, @RequestParam TransferStatus status) {
    transferService.updateTransferStatus(transferId, status);
  }
}

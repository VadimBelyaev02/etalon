package com.andersenlab.etalon.transactionservice.controller.impl;

import com.andersenlab.etalon.transactionservice.controller.TransferTypeController;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.TransferTypeResponseDto;
import com.andersenlab.etalon.transactionservice.interceptor.AuthenticationHolder;
import com.andersenlab.etalon.transactionservice.service.TransferService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(TransferTypeControllerImpl.TRANSFER_TYPES_URL)
@RestController
@Tag(name = "Transfer Type")
public class TransferTypeControllerImpl implements TransferTypeController {

  private final AuthenticationHolder authenticationHolder;
  private final TransferService transferService;

  @GetMapping
  public List<TransferTypeResponseDto> getTransferTypes() {
    log.info(
        "{getTransferTypes} -> Getting all transfer types for user "
            + authenticationHolder.getUserId());
    return transferService.getAllTransferTypes(authenticationHolder.getUserId());
  }
}

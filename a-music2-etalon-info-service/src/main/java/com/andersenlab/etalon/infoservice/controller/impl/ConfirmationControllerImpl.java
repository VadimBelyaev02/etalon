package com.andersenlab.etalon.infoservice.controller.impl;

import com.andersenlab.etalon.infoservice.controller.ConfirmationController;
import com.andersenlab.etalon.infoservice.dto.request.ConfirmationRequestDto;
import com.andersenlab.etalon.infoservice.dto.request.CreateConfirmationRequestDto;
import com.andersenlab.etalon.infoservice.dto.response.CreateConfirmationResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.MessageResponseDto;
import com.andersenlab.etalon.infoservice.service.ConfirmationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(ConfirmationController.CONFIRMATIONS_URL)
@Tag(name = "Confirmation")
public class ConfirmationControllerImpl implements ConfirmationController {

  private final ConfirmationService confirmationService;

  @PostMapping()
  public CreateConfirmationResponseDto createConfirmation(
      @RequestBody final CreateConfirmationRequestDto dto) {
    return confirmationService.createConfirmation(dto);
  }

  @PostMapping(CONFIRMATION_RESEND_BY_ID_URL)
  public CreateConfirmationResponseDto resendConfirmationCode(
      @PathVariable final Long confirmationId) {
    return confirmationService.resendConfirmationCode(confirmationId);
  }

  @PostMapping(CONFIRMATION_BY_CONFIRMATION_ID_AND_EMAIL)
  public MessageResponseDto verifyConfirmation(
      @PathVariable final Long confirmationId, @RequestBody final ConfirmationRequestDto dto) {
    return confirmationService.verifyConfirmationCode(confirmationId, dto);
  }
}

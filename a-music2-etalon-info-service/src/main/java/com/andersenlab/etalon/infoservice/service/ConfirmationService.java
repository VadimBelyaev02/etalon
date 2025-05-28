package com.andersenlab.etalon.infoservice.service;

import com.andersenlab.etalon.infoservice.dto.request.ConfirmationRequestDto;
import com.andersenlab.etalon.infoservice.dto.request.CreateConfirmationRequestDto;
import com.andersenlab.etalon.infoservice.dto.response.ConfirmationResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.CreateConfirmationResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.MessageResponseDto;
import com.andersenlab.etalon.infoservice.util.enums.Operation;
import java.util.List;

public interface ConfirmationService {
  CreateConfirmationResponseDto createConfirmation(final CreateConfirmationRequestDto dto);

  void processConfirmation(final Long confirmationId);

  MessageResponseDto verifyConfirmationCode(
      final Long confirmationId, final ConfirmationRequestDto dto);

  CreateConfirmationResponseDto resendConfirmationCode(Long confirmationId);

  void rejectConfirmation(Long confirmationId);

  List<ConfirmationResponseDto> getConfirmationsByOperationAndTargetId(
      Operation operation, Long targetId);
}

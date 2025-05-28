package com.andersenlab.etalon.infoservice.service.impl;

import com.andersenlab.etalon.infoservice.client.UserServiceClient;
import com.andersenlab.etalon.infoservice.dto.request.PeselRequestDto;
import com.andersenlab.etalon.infoservice.dto.response.StatusMessageResponseDto;
import com.andersenlab.etalon.infoservice.service.ValidatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidatorServiceImpl implements ValidatorService {

  private static final String VALID_CODE = "1234";
  private final UserServiceClient userServiceClient;

  @Override
  public boolean isPeselValidToRegister(final String pesel) {
    StatusMessageResponseDto response =
        userServiceClient.validatePeselBeforeRegistration(new PeselRequestDto(pesel));
    return response.status();
  }

  @Override
  public boolean isConfirmationCodeValidToRegister(final String confirmationCode) {
    return confirmationCode.equals(VALID_CODE);
  }
}

package com.andersenlab.etalon.depositservice.client.service;

import com.andersenlab.etalon.depositservice.client.InfoServiceClient;
import com.andersenlab.etalon.depositservice.dto.auth.request.CreateConfirmationRequestDto;
import com.andersenlab.etalon.depositservice.dto.auth.response.CreateConfirmationResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InfoService {
  private final InfoServiceClient infoServiceClient;

  public CreateConfirmationResponseDto createConfirmation(
      CreateConfirmationRequestDto authConfirmation) {
    return infoServiceClient.createConfirmation(authConfirmation);
  }
}

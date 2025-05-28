package com.andersenlab.etalon.infoservice.service.strategy.emailrequestpreparation.operation;

import com.andersenlab.etalon.infoservice.client.UserServiceClient;
import com.andersenlab.etalon.infoservice.dto.request.BaseEmailRequestDto;
import com.andersenlab.etalon.infoservice.dto.request.ConfirmationEmailRequestDto;
import com.andersenlab.etalon.infoservice.dto.response.UserDataResponseDto;
import com.andersenlab.etalon.infoservice.entity.ConfirmationEntity;
import com.andersenlab.etalon.infoservice.interceptor.AuthenticationHolder;
import com.andersenlab.etalon.infoservice.service.strategy.emailrequestpreparation.EmailRequestPreparationStrategy;
import com.andersenlab.etalon.infoservice.util.enums.EmailType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CreatePaymentRequestStrategy implements EmailRequestPreparationStrategy {
  private final UserServiceClient userServiceClient;
  private final AuthenticationHolder authenticationHolder;

  @Override
  public BaseEmailRequestDto prepareEmailRequest(ConfirmationEntity confirmation) {
    ConfirmationEmailRequestDto confirmationEmailRequestDto = new ConfirmationEmailRequestDto();
    confirmationEmailRequestDto.setVerificationCode(
        String.valueOf(confirmation.getConfirmationCode()));
    confirmationEmailRequestDto.setToEmail(getUserData().email());
    confirmationEmailRequestDto.setType(EmailType.CONFIRMATION);
    confirmationEmailRequestDto.setSubject("Confirmation of payment");
    return confirmationEmailRequestDto;
  }

  private UserDataResponseDto getUserData() {
    return userServiceClient.getUserData(authenticationHolder.getUserId());
  }
}

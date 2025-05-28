package com.andersenlab.etalon.infoservice.service.strategy.emailrequestpreparation.operation;

import static com.andersenlab.etalon.infoservice.util.enums.EmailType.REGISTRATION;

import com.andersenlab.etalon.infoservice.client.UserServiceClient;
import com.andersenlab.etalon.infoservice.dto.request.BaseEmailRequestDto;
import com.andersenlab.etalon.infoservice.dto.request.RegistrationEmailRequestDto;
import com.andersenlab.etalon.infoservice.dto.response.UserDataResponseDto;
import com.andersenlab.etalon.infoservice.entity.ConfirmationEntity;
import com.andersenlab.etalon.infoservice.service.strategy.emailrequestpreparation.EmailRequestPreparationStrategy;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserRegistrationRequestStrategy implements EmailRequestPreparationStrategy {

  private final UserServiceClient userServiceClient;

  @Override
  public BaseEmailRequestDto prepareEmailRequest(ConfirmationEntity confirmation) {

    UserDataResponseDto userData = userServiceClient.getUserDataById(confirmation.getTargetId());
    RegistrationEmailRequestDto registrationEmailRequestDto = new RegistrationEmailRequestDto();
    registrationEmailRequestDto.setToEmail(userData.email());
    registrationEmailRequestDto.setVerificationCode(
        String.valueOf(confirmation.getConfirmationCode()));
    registrationEmailRequestDto.setType(REGISTRATION);
    return registrationEmailRequestDto;
  }
}

package com.andersenlab.etalon.infoservice.service.strategy.emailrequestpreparation.operation;

import com.andersenlab.etalon.infoservice.client.UserServiceClient;
import com.andersenlab.etalon.infoservice.dto.request.BaseEmailRequestDto;
import com.andersenlab.etalon.infoservice.dto.request.EmailModificationRequestDto;
import com.andersenlab.etalon.infoservice.dto.response.EmailModificationInfoResponseDto;
import com.andersenlab.etalon.infoservice.entity.ConfirmationEntity;
import com.andersenlab.etalon.infoservice.service.strategy.emailrequestpreparation.EmailRequestPreparationStrategy;
import com.andersenlab.etalon.infoservice.util.enums.EmailType;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EmailModificationRequestStrategy implements EmailRequestPreparationStrategy {
  private final UserServiceClient userServiceClient;

  @Override
  public BaseEmailRequestDto prepareEmailRequest(ConfirmationEntity confirmation) {
    EmailModificationInfoResponseDto emailModification =
        userServiceClient.getEmailModificationInfo(confirmation.getTargetId());
    EmailModificationRequestDto emailModificationRequestDto = new EmailModificationRequestDto();
    emailModificationRequestDto.setToEmail(emailModification.newEmail());
    emailModificationRequestDto.setVerificationCode(
        String.valueOf(confirmation.getConfirmationCode()));
    emailModificationRequestDto.setType(EmailType.EMAIL_MODIFY_CONFIRMATION);
    emailModificationRequestDto.setNewEmail(emailModification.newEmail());
    return emailModificationRequestDto;
  }
}

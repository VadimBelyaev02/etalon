package com.andersenlab.etalon.infoservice.service.strategy.emailrequestpreparation;

import com.andersenlab.etalon.infoservice.dto.request.BaseEmailRequestDto;
import com.andersenlab.etalon.infoservice.entity.ConfirmationEntity;

public interface EmailRequestPreparationStrategy {
  BaseEmailRequestDto prepareEmailRequest(ConfirmationEntity confirmation);
}

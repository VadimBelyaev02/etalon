package com.andersenlab.etalon.infoservice.service;

import com.andersenlab.etalon.infoservice.entity.ConfirmationEntity;

public interface ConfirmationPostProcessorService {
  void postProcess(ConfirmationEntity confirmationEntity);
}

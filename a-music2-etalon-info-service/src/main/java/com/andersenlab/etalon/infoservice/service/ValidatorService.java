package com.andersenlab.etalon.infoservice.service;

public interface ValidatorService {
  boolean isPeselValidToRegister(final String pesel);

  boolean isConfirmationCodeValidToRegister(final String confirmationCode);
}

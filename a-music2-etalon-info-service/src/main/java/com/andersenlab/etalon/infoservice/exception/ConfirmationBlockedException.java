package com.andersenlab.etalon.infoservice.exception;

import com.andersenlab.etalon.infoservice.entity.ConfirmationEntity;
import java.time.ZonedDateTime;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ConfirmationBlockedException extends RuntimeException {
  private static final String CONFIRMATION_BLOCKED = "Confirmation is blocked";
  private final HttpStatus httpStatus;
  private final ZonedDateTime unblockDate;

  public ConfirmationBlockedException(
      final HttpStatus httpStatus, ConfirmationEntity confirmationEntity) {
    super(CONFIRMATION_BLOCKED);
    this.unblockDate = confirmationEntity.getBlockedUntil();
    this.httpStatus = httpStatus;
  }
}

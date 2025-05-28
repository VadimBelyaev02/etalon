package com.andersenlab.etalon.infoservice.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "confirmation.validation")
public class ConfirmationValidationProperties {
  private int NextCodeResendTime;
  private int MaxConfirmAttempts;
  private int BlockTimeAfterAllAttempts;
  private int ConfirmCodeExpirationTime;
  private int LastAttemptThreshold;
}

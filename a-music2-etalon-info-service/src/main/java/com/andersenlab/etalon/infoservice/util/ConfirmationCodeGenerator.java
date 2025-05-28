package com.andersenlab.etalon.infoservice.util;

import java.util.Random;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ConfirmationCodeGenerator {
  private final Random random = new Random();

  public static String generateConfirmationCode() {
    return String.format("%06d", 100000 + random.nextInt(900000));
  }
}

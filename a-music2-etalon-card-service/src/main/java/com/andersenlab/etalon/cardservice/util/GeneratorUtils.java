package com.andersenlab.etalon.cardservice.util;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@UtilityClass
public class GeneratorUtils {

  private static final int CARD_NUMBER_RANDOM_LENGTH = 10;
  private static final Long INITIAL_CARD_SERIAL_NUMBER = 1L;
  private static final String BIN = "2467";

  private static final Random rand = new SecureRandom();

  public static String generateCardNumber(String paymentSystem, Long serialNumber) {
    log.info("{generateCardNumber} -> Generate card number");

    StringBuilder builder = new StringBuilder(paymentSystem.concat(BIN));
    builder.append(generateCardSerialNumber(serialNumber));
    builder.append(calculateChecksum(builder.toString()));
    return builder.toString();
  }

  private String generateCardSerialNumber(Long serialNumber) {
    log.info("{generateCardSerialNumber} -> Generate card serial number");

    Long nextSerialNumber =
        Objects.nonNull(serialNumber) ? ++serialNumber : INITIAL_CARD_SERIAL_NUMBER;
    return StringUtils.leftPad(nextSerialNumber.toString(), CARD_NUMBER_RANDOM_LENGTH, "0");
  }

  private int calculateChecksum(String number) {
    log.info("{calculateChecksum} -> calculate checksum for card number");

    int sum = 0;
    for (int i = 0; i < number.length(); i++) {
      int digit = Integer.parseInt(number.substring(i, (i + 1)));

      if ((i % 2) == 0) {
        digit = digit * 2;

        if (digit > 9) {
          digit = (digit / 10) + (digit % 10);
        }
      }
      sum += digit;
    }
    int mod = sum % 10;

    return ((mod == 0) ? 0 : 10 - mod);
  }

  public static String generateRandomNumber(int numberLength) {
    log.info("{generateRandomNumber} -> generate random number");

    final int min = 1;
    final int max = 10;
    return rand.ints(numberLength, min, max)
        .mapToObj(String::valueOf)
        .collect(Collectors.joining());
  }
}

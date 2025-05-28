package com.andersenlab.etalon.loanservice.util;

import java.util.Objects;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@UtilityClass
public class GeneratorUtils {

  private static final Long INITIAL_CONTRACT_SERIAL_NUMBER = 1L;

  public static String generateContractNumber(Long serialNumber) {
    Long nextSerialNumber =
        Objects.nonNull(serialNumber) ? ++serialNumber : INITIAL_CONTRACT_SERIAL_NUMBER;
    return StringUtils.leftPad(nextSerialNumber.toString(), 14, "0");
  }
}

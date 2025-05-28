package com.andersenlab.etalon.infoservice.util;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {

  private DateTimeUtils() {}

  public static String getDateFromDateTimeString(String dateTimeString) {
    ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateTimeString);
    return zonedDateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
  }
}

package com.andersenlab.etalon.userservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class EmailFormatter {
  public static String maskEmail(String email) {
    if (email == null) {
      return "";
    }

    int atIndex = email.indexOf("@");

    if (atIndex <= 1) {
      return "****" + email.substring(atIndex);
    } else {
      return email.charAt(0) + "****" + email.substring(atIndex);
    }
  }
}

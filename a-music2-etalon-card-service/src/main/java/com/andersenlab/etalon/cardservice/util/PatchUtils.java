package com.andersenlab.etalon.cardservice.util;

import java.util.function.Consumer;

public class PatchUtils {

  public static <T> void updateIfPresent(Consumer<T> consumer, T value) {
    if (value != null) {
      consumer.accept(value);
    }
  }
}

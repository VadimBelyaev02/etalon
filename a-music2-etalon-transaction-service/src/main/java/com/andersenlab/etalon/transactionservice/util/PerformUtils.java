package com.andersenlab.etalon.transactionservice.util;

import java.util.Objects;
import java.util.function.Consumer;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PerformUtils {
  public static <T> void performIfPresent(Consumer<T> consumer, T value) {
    if (Objects.nonNull(value)) {
      consumer.accept(value);
    }
  }
}

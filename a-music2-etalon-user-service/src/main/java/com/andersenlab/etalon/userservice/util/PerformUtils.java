package com.andersenlab.etalon.userservice.util;

import java.util.Objects;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PerformUtils {
  public static <T> void performIfPresent(Consumer<T> consumer, T value) {
    if (Objects.nonNull(value)) {
      consumer.accept(value);
    }
  }
}

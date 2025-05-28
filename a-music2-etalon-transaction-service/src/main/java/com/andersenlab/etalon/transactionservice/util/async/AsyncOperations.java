package com.andersenlab.etalon.transactionservice.util.async;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AsyncOperations {

  @Async
  public <T> CompletableFuture<T> runAsync(Supplier<T> supplier) {
    return CompletableFuture.completedFuture(supplier.get());
  }
}

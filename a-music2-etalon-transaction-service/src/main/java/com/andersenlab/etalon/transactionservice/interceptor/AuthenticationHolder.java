package com.andersenlab.etalon.transactionservice.interceptor;

import jakarta.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationHolder {
  private String userId;

  @PreDestroy
  private void clearThreadLocal() {
    ThreadLocal<AuthenticationHolder> authenticationHolderThreadLocal = new ThreadLocal<>();
    authenticationHolderThreadLocal.remove();
  }
}

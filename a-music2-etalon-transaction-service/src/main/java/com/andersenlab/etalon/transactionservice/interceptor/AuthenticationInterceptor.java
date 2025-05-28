package com.andersenlab.etalon.transactionservice.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {
  private final ThreadLocal<AuthenticationHolder> threadLocalAuthenticationHolder;

  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {
    String operator = request.getHeader("authenticated-user-id");
    AuthenticationHolder authenticationHolder = threadLocalAuthenticationHolder.get();
    authenticationHolder.setUserId(operator);
    MDC.put("user_id", operator);
    return true;
  }

  @Override
  public void afterCompletion(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
      throws Exception {
    threadLocalAuthenticationHolder.remove();
    MDC.remove("user_id");
  }
}

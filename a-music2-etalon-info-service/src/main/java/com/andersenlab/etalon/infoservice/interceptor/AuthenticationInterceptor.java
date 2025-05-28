package com.andersenlab.etalon.infoservice.interceptor;

import com.andersenlab.etalon.infoservice.util.Constants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {
  private final AuthenticationHolder authenticationHolder;

  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {
    String operator = request.getHeader(Constants.AUTHENTICATED_USER_ID);
    authenticationHolder.setUserId(operator);
    MDC.put("user_id", operator);
    return true;
  }
}

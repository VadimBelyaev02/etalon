package com.andersenlab.etalon.transactionservice.interceptor;

import com.andersenlab.etalon.transactionservice.util.Constants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class TraceIdFilter extends OncePerRequestFilter {

  private final FeignRequestInterceptor feignRequestInterceptor;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String traceId = request.getHeader("X-B3-TraceId");

    if (traceId == null) {
      traceId = generateTraceId();
    }

    MDC.put(Constants.TRACE_ID, traceId);

    feignRequestInterceptor.addTraceIdToFeignRequest(traceId);

    filterChain.doFilter(request, response);
  }

  private String generateTraceId() {
    return java.util.UUID.randomUUID().toString();
  }
}

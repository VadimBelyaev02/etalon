package com.andersenlab.etalon.transactionservice.interceptor;

import com.andersenlab.etalon.transactionservice.util.Constants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class FeignRequestInterceptor implements RequestInterceptor {

  @Override
  public void apply(RequestTemplate requestTemplate) {
    String traceId = MDC.get(Constants.TRACE_ID);

    if (traceId != null) {
      requestTemplate.header("X-B3-TraceId", traceId);
    }
  }

  public void addTraceIdToFeignRequest(String traceId) {
    MDC.put(Constants.TRACE_ID, traceId);
  }
}

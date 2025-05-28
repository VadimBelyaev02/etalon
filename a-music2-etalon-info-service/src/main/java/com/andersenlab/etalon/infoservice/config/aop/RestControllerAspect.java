package com.andersenlab.etalon.infoservice.config.aop;

import com.andersenlab.etalon.infoservice.config.aop.util.LoggingUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class RestControllerAspect {
  @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
  public void restControllers() {}

  @Around("restControllers()")
  public Object logControllers(ProceedingJoinPoint joinPoint) throws Throwable {
    return LoggingUtil.logMethod(joinPoint);
  }
}

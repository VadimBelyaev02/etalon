package com.andersenlab.etalon.userservice.config.aop;

import com.andersenlab.etalon.userservice.config.aop.util.LoggingUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class ServiceAspect {

  @Pointcut("within(@org.springframework.stereotype.Service *)")
  public void serviceBeans() {}

  @Around("serviceBeans()")
  public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {
    return LoggingUtil.logMethod(joinPoint);
  }
}

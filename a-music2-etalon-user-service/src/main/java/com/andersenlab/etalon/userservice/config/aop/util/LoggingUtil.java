package com.andersenlab.etalon.userservice.config.aop.util;

import java.util.Arrays;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

@Slf4j
@UtilityClass
public class LoggingUtil {

  public static Object logMethod(ProceedingJoinPoint joinPoint) throws Throwable {
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    String method = methodSignature.toShortString();
    Object[] args = joinPoint.getArgs();
    String params = Arrays.toString(args);
    log.info("Enters on method: {}, with params: {}", method, params);

    try {
      Object result = joinPoint.proceed();
      log.info("Exiting method {} with return {}", method, result);
      return result;
    } catch (Throwable e) {
      log.error("Exception in method {}", method, e);
      throw e;
    }
  }
}

package com.andersenlab.etalon.cardservice.annotation;

import java.lang.reflect.Method;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

@Slf4j
public class WithUserIdExtension implements BeforeEachCallback, AfterEachCallback {
  private WithUserIdAnalyzer analyzer;

  @Override
  public void beforeEach(ExtensionContext context) {

    Optional<Object> currentInstance = context.getTestInstance();
    Optional<Method> currentMethod = context.getTestMethod();
    if (currentInstance.isEmpty() || currentMethod.isEmpty()) {
      return;
    }
    analyzer = new WithUserIdAnalyzer(currentInstance.get(), currentMethod.get());
    analyzer.analyze();
  }

  @Override
  public void afterEach(ExtensionContext context) {
    analyzer.cleanUp();
  }
}

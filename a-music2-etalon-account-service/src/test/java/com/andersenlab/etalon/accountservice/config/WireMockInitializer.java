package com.andersenlab.etalon.accountservice.config;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import com.github.tomakehurst.wiremock.WireMockServer;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.test.context.support.TestPropertySourceUtils;

@Slf4j
public class WireMockInitializer
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {
  private static final String FEIGN_URL_PROPERTY_FORMAT = "feign.%s.url=http://localhost:%s";
  private ConfigurableApplicationContext context;

  @Override
  public void initialize(@NotNull ConfigurableApplicationContext applicationContext) {
    this.context = applicationContext;
    Stream.of("card-service", "user-service")
        .collect(Collectors.toMap(serviceName -> serviceName, this::createWireMockServer))
        .entrySet()
        .stream()
        .peek(this::startWireMockServer)
        .peek(this::registerWireMockServerSingletonBean)
        .peek(this::setFeignServiceClientUrlProperties)
        .forEach(this::registerWireMockServersStopListener);
  }

  private WireMockServer createWireMockServer(String serverName) {
    return new WireMockServer(
        options().dynamicPort().usingFilesUnderClasspath("wiremock/" + serverName));
  }

  private void startWireMockServer(Map.Entry<String, WireMockServer> serverEntry) {
    serverEntry.getValue().start();
    log.info(
        "Service name->{}, port defined->{}", serverEntry.getKey(), serverEntry.getValue().port());
  }

  private void registerWireMockServerSingletonBean(Map.Entry<String, WireMockServer> serverEntry) {
    log.info("Bean name->{}, Bean->{}", serverEntry.getKey(), serverEntry.getValue());
    context.getBeanFactory().registerSingleton(serverEntry.getKey(), serverEntry.getValue());
  }

  private void setFeignServiceClientUrlProperties(Map.Entry<String, WireMockServer> serverEntry) {
    String property =
        FEIGN_URL_PROPERTY_FORMAT.formatted(serverEntry.getKey(), serverEntry.getValue().port());
    log.info("Property is overridden -> {}", property);
    TestPropertySourceUtils.addInlinedPropertiesToEnvironment(context, property);
  }

  private void registerWireMockServersStopListener(Map.Entry<String, WireMockServer> serverEntry) {
    context.addApplicationListener(
        event -> {
          if (event instanceof ContextClosedEvent) {
            serverEntry.getValue().stop();
          }
        });
  }
}

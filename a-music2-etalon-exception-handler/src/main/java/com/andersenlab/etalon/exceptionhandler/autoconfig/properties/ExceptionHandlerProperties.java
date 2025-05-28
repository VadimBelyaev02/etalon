package com.andersenlab.etalon.exceptionhandler.autoconfig.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "exception.handler")
public record ExceptionHandlerProperties(boolean enabled) {}

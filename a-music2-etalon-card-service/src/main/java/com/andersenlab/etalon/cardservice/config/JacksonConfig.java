package com.andersenlab.etalon.cardservice.config;

import com.andersenlab.etalon.cardservice.util.BigDecimalSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {
  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();

    SimpleModule bigDecimalModule = new SimpleModule();
    bigDecimalModule.addSerializer(BigDecimal.class, new BigDecimalSerializer());
    objectMapper.registerModule(bigDecimalModule);

    JavaTimeModule javaTimeModule = new JavaTimeModule();
    javaTimeModule.addSerializer(
        ZonedDateTime.class, new ZonedDateTimeSerializer(DateTimeFormatter.ISO_INSTANT));
    objectMapper.registerModule(javaTimeModule);

    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    return objectMapper;
  }
}

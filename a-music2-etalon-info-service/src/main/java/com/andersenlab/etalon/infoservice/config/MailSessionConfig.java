package com.andersenlab.etalon.infoservice.config;

import jakarta.mail.Session;
import java.util.Properties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MailSessionConfig {

  @Bean
  public Session session() {
    return Session.getDefaultInstance(new Properties());
  }
}

package com.andersenlab.etalon.infoservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableSqs
public class InfoServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(InfoServiceApplication.class, args);
  }
}

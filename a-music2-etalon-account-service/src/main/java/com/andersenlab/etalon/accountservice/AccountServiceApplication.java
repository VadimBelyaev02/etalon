package com.andersenlab.etalon.accountservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
// @Import(com.andersenlab.etalon.exceptionhandler.config.FeignConfig.class)

public class AccountServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(AccountServiceApplication.class, args);
  }
}

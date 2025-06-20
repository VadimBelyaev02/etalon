package com.andersenlab.etalon.loanservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class LoanServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(LoanServiceApplication.class, args);
    System.out.println("Loan Service Application started successfully.");
    System.out.println("Ah");
    System.out.println("Ahf");
  }
}

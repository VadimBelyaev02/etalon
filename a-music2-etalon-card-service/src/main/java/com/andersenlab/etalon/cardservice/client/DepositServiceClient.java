package com.andersenlab.etalon.cardservice.client;

import com.andersenlab.etalon.cardservice.config.AuthenticationContextFeignConfig;
import com.andersenlab.etalon.cardservice.dto.deposit.DepositStatus;
import com.andersenlab.etalon.cardservice.util.filter.PaginatedDepositResponse;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "deposit-service",
    url = "${feign.deposit-service.url}",
    configuration = AuthenticationContextFeignConfig.class,
    path = "/deposit")
public interface DepositServiceClient {
  @GetMapping("/api/v2/deposits")
  PaginatedDepositResponse getFilteredDepositsByUserId(
      @RequestParam("accountNumber") String accountNumber,
      @RequestParam("statusList") List<DepositStatus> statusList);
}

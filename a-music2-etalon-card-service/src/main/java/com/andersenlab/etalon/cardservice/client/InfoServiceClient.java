package com.andersenlab.etalon.cardservice.client;

import com.andersenlab.etalon.cardservice.config.AuthenticationContextFeignConfig;
import com.andersenlab.etalon.cardservice.dto.info.response.BankBranchesResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "info-service",
    url = "${feign.info-service.url}",
    configuration = AuthenticationContextFeignConfig.class,
    path = "/info")
public interface InfoServiceClient {
  String API_V1_URI = "/api/v1";
  String INFO_URI = "/info";
  String BRANCHES_URI = "/branches";
  String BRANCH_ID_PATH = "/{bankBranchId}";

  String BANK_BRANCHES_URL = API_V1_URI + INFO_URI + BRANCHES_URI;

  @GetMapping(BANK_BRANCHES_URL + BRANCH_ID_PATH)
  BankBranchesResponseDto getBankBranch(@PathVariable Long bankBranchId);
}

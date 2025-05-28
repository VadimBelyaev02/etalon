package com.andersenlab.etalon.transactionservice.client;

import com.andersenlab.etalon.transactionservice.config.AuthenticationContextFeignConfig;
import com.andersenlab.etalon.transactionservice.dto.account.response.UserDataResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "user-service",
    url = "${feign.user-service.url}",
    configuration = AuthenticationContextFeignConfig.class,
    path = "/user")
public interface UserServiceClient {

  String API_V1_URI = "/api/v1";
  String USERS_URI = "/users";
  String DETAILS_URI = "/details";
  String USER_ID_PATH = "/{userId}";
  String USER_DETAILS_URL = API_V1_URI + USERS_URI + USER_ID_PATH + DETAILS_URI;

  @GetMapping(USER_DETAILS_URL)
  UserDataResponseDto getUserData(@PathVariable("userId") final String userId);
}

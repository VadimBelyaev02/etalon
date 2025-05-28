package com.andersenlab.etalon.cardservice.client;

import com.andersenlab.etalon.cardservice.config.AuthenticationContextFeignConfig;
import com.andersenlab.etalon.cardservice.dto.user.response.UserDataResponseDto;
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

  String USERS_URL = API_V1_URI + USERS_URI;
  String USER_DETAILS_URL = USERS_URL + USER_ID_PATH + DETAILS_URI;

  @GetMapping(USER_DETAILS_URL)
  UserDataResponseDto getUserData(@PathVariable("userId") final String userId);
}

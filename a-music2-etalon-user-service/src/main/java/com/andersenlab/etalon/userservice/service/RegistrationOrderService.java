package com.andersenlab.etalon.userservice.service;

import com.andersenlab.etalon.userservice.dto.user.request.UpdateRegistrationOrderStatusRequestDto;
import com.andersenlab.etalon.userservice.dto.user.response.RegistrationOrderResponseDto;
import com.andersenlab.etalon.userservice.entity.RegistrationOrder;

public interface RegistrationOrderService {
  RegistrationOrder updateRegistrationOrder(RegistrationOrder registrationOrder);

  RegistrationOrderResponseDto updateRegistrationOrderStatus(
      UpdateRegistrationOrderStatusRequestDto updateRegistrationOrderStatusRequestDto);

  RegistrationOrder getRegistrationOrderByRegistrationId(String id);

  RegistrationOrder getRegistrationOrderById(long id);
}

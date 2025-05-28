package com.andersenlab.etalon.userservice.service.impl;

import com.andersenlab.etalon.userservice.dto.user.request.UpdateRegistrationOrderStatusRequestDto;
import com.andersenlab.etalon.userservice.dto.user.response.RegistrationOrderResponseDto;
import com.andersenlab.etalon.userservice.entity.RegistrationOrder;
import com.andersenlab.etalon.userservice.exception.BusinessException;
import com.andersenlab.etalon.userservice.mapper.RegistrationOrderMapper;
import com.andersenlab.etalon.userservice.repository.RegistrationOrderRepository;
import com.andersenlab.etalon.userservice.service.RegistrationOrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RegistrationOrderServiceImpl implements RegistrationOrderService {
  private final RegistrationOrderRepository registrationOrderRepository;
  private final RegistrationOrderMapper registrationOrderMapper;

  @Override
  public RegistrationOrder updateRegistrationOrder(RegistrationOrder registrationOrder) {
    RegistrationOrder existingRegistrationOrder =
        getRegistrationOrderByRegistrationId(registrationOrder.getRegistrationId());
    return registrationOrderRepository.save(
        new RegistrationOrder(
            existingRegistrationOrder.getId(),
            registrationOrder.getPesel(),
            registrationOrder.getOrderStatus(),
            registrationOrder.getRegistrationId()));
  }

  @Override
  public RegistrationOrderResponseDto updateRegistrationOrderStatus(
      UpdateRegistrationOrderStatusRequestDto updateRegistrationOrderStatusRequestDto) {
    RegistrationOrder existingRegistrationOrder =
        getRegistrationOrderByRegistrationId(
            updateRegistrationOrderStatusRequestDto.registrationOrderId());
    existingRegistrationOrder.setOrderStatus(updateRegistrationOrderStatusRequestDto.orderStatus());
    RegistrationOrder updatedRegistrationOrder = updateRegistrationOrder(existingRegistrationOrder);
    return registrationOrderMapper.toDto(updatedRegistrationOrder);
  }

  @Override
  public RegistrationOrder getRegistrationOrderByRegistrationId(String id) {
    return registrationOrderRepository
        .findByRegistrationId(id)
        .orElseThrow(
            () ->
                new BusinessException(
                    HttpStatus.NOT_FOUND, "Registration order with id-%s not found".formatted(id)));
  }

  @Override
  public RegistrationOrder getRegistrationOrderById(long id) {
    return registrationOrderRepository
        .findById(id)
        .orElseThrow(
            () ->
                new BusinessException(
                    HttpStatus.NOT_FOUND, "No registration order found by id-%s".formatted(id)));
  }
}

package com.andersenlab.etalon.userservice.mapper;

import com.andersenlab.etalon.userservice.dto.user.response.RegistrationOrderResponseDto;
import com.andersenlab.etalon.userservice.entity.RegistrationOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RegistrationOrderMapper {

  @Mapping(source = "pesel", target = "pesel")
  @Mapping(source = "orderStatus", target = "orderStatus")
  @Mapping(source = "registrationId", target = "registrationId")
  RegistrationOrderResponseDto toDto(RegistrationOrder registrationOrder);
}

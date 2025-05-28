package com.andersenlab.etalon.userservice.mapper;

import com.andersenlab.etalon.userservice.dto.user.request.AddressDto;
import com.andersenlab.etalon.userservice.entity.AddressEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {

  AddressDto toAddressDto(AddressEntity address);
}

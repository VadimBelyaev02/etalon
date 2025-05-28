package com.andersenlab.etalon.userservice.mapper;

import com.andersenlab.etalon.userservice.dto.user.request.UserRequestDto;
import com.andersenlab.etalon.userservice.dto.user.response.UserDataResponseDto;
import com.andersenlab.etalon.userservice.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
  UserEntity toEntity(final UserRequestDto dto);

  UserDataResponseDto toDto(final UserEntity entity);
}

package com.andersenlab.etalon.userservice.dto.user.response;

import com.andersenlab.etalon.userservice.dto.user.request.AddressDto;
import lombok.Builder;

@Builder
public record PersonalInfoDto(
    String registrationId,
    String firstName,
    String lastName,
    String email,
    String phoneNumber,
    AddressDto addressDto) {}

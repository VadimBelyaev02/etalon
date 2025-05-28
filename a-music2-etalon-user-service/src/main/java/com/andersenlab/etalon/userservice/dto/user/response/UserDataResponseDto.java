package com.andersenlab.etalon.userservice.dto.user.response;

import com.andersenlab.etalon.userservice.dto.user.request.AddressDto;
import com.andersenlab.etalon.userservice.dto.user.request.EmploymentDataDto;
import java.time.ZonedDateTime;
import lombok.Builder;

@Builder
public record UserDataResponseDto(
    String id,
    String email,
    String firstName,
    String lastName,
    String pesel,
    String phoneNumber,
    AddressDto address,
    EmploymentDataDto employmentData,
    ZonedDateTime createAt,
    ZonedDateTime updateAt) {}

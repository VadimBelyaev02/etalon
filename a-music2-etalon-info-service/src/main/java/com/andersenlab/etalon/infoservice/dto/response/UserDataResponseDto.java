package com.andersenlab.etalon.infoservice.dto.response;

import java.time.ZonedDateTime;
import lombok.Builder;

@Builder(toBuilder = true)
public record UserDataResponseDto(
    String id,
    String email,
    String firstName,
    String lastName,
    String pesel,
    ZonedDateTime createAt,
    ZonedDateTime updateAt) {}

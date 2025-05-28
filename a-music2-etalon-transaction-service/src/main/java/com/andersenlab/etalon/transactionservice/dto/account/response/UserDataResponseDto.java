package com.andersenlab.etalon.transactionservice.dto.account.response;

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

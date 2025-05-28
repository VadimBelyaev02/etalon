package com.andersenlab.etalon.cardservice.dto.user.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder(toBuilder = true)
public record UserDataResponseDto(String id, String firstName, String lastName) {}

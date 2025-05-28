package com.andersenlab.etalon.accountservice.dto.user.response;

import lombok.Builder;

@Builder(toBuilder = true)
public record UserDataResponseDto(String id, String firstName, String lastName) {
  public String getFullName() {
    return String.format("%s %s", firstName, lastName);
  }
}

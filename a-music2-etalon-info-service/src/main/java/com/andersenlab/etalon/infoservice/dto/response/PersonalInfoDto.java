package com.andersenlab.etalon.infoservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class PersonalInfoDto {
  private String registrationId;
  private String firstName;
  private String lastName;
  private String email;
  private String phoneNumber;
}

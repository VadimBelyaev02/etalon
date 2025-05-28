package com.andersenlab.etalon.infoservice.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonTypeName("PASSWORD_RESET_CONFIRMATION")
public class PasswordResetEmailRequestDto extends BaseEmailRequestDto {
  private String resetToken;
}

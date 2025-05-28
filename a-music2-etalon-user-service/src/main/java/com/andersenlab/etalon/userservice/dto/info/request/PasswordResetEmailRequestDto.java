package com.andersenlab.etalon.userservice.dto.info.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@JsonTypeName("PASSWORD_RESET_CONFIRMATION")
public class PasswordResetEmailRequestDto extends BaseEmailRequestDto {
  private String resetToken;
}

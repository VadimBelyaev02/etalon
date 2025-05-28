package com.andersenlab.etalon.infoservice.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("CONFIRMATION")
public class ConfirmationEmailRequestDto extends BaseEmailRequestDto {
  private String verificationCode;
}

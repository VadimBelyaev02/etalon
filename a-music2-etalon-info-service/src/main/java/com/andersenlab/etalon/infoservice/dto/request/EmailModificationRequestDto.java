package com.andersenlab.etalon.infoservice.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("EMAIL_MODIFY_CONFIRMATION")
public class EmailModificationRequestDto extends ConfirmationEmailRequestDto {
  private String newEmail;
}

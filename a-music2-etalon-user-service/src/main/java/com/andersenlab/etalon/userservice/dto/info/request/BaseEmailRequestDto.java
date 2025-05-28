package com.andersenlab.etalon.userservice.dto.info.request;

import com.andersenlab.etalon.userservice.dto.genericemail.EmailModificationSendRequestDto;
import com.andersenlab.etalon.userservice.util.EmailType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(
      value = PasswordResetEmailRequestDto.class,
      name = "PASSWORD_RESET_CONFIRMATION"),
  @JsonSubTypes.Type(
      value = EmailModificationSendRequestDto.class,
      name = "EMAIL_MODIFY_CONFIRMATION")
})
@Data
@NoArgsConstructor
public class BaseEmailRequestDto implements Serializable {
  private String toEmail;
  private String subject;
  @NotNull private EmailType type;
}

package com.andersenlab.etalon.infoservice.dto.request;

import com.andersenlab.etalon.infoservice.util.enums.EmailType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = ConfirmationEmailRequestDto.class, name = "CONFIRMATION"),
  @JsonSubTypes.Type(value = RegistrationEmailRequestDto.class, name = "REGISTRATION"),
  @JsonSubTypes.Type(value = EmailModificationRequestDto.class, name = "EMAIL_MODIFY_CONFIRMATION"),
  @JsonSubTypes.Type(
      value = PasswordResetEmailRequestDto.class,
      name = "PASSWORD_RESET_CONFIRMATION"),
  @JsonSubTypes.Type(value = SharingTransferReceiptDto.class, name = "SHARING_TRANSFER_RECEIPT"),
  @JsonSubTypes.Type(value = ConfirmationStatementDto.class, name = "CONFIRMATION_STATEMENT")
})
@Data
@NoArgsConstructor
public class BaseEmailRequestDto implements Serializable {
  private String toEmail;
  private String subject;

  @NotNull private EmailType type;
}

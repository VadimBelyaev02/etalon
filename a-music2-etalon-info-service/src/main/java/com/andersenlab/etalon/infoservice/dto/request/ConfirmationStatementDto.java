package com.andersenlab.etalon.infoservice.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("CONFIRMATION_STATEMENT")
public class ConfirmationStatementDto extends BaseEmailRequestDto {
  Long transactionId;
  String locale;
}

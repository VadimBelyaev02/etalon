package com.andersenlab.etalon.infoservice.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("SHARING_TRANSFER_RECEIPT")
public class SharingTransferReceiptDto extends BaseEmailRequestDto {
  private Long transferId;
}

package com.andersenlab.etalon.transactionservice.dto.transaction.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.EqualsAndHashCode;

@Schema(
    name = "TransactionExtendedPageResponseDto",
    description = "A list of TransactionExtendedResponseDto with pagination")
@EqualsAndHashCode(callSuper = true)
public class TransactionExtendedPageResponseDto
    extends CustomPageDto<TransactionExtendedResponseDto> {

  public TransactionExtendedPageResponseDto(
      List<TransactionExtendedResponseDto> content,
      int pageNumber,
      int pageSize,
      long totalElements) {
    super(content, pageNumber, pageSize, totalElements);
  }
}

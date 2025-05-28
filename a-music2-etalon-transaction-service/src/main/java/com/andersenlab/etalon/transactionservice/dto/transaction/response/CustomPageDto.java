package com.andersenlab.etalon.transactionservice.dto.transaction.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomPageDto<T> {
  private List<T> content;
  private int pageNumber;
  private int pageSize;
  private long totalElements;
}

package com.andersenlab.etalon.cardservice.util.filter;

import com.andersenlab.etalon.cardservice.dto.deposit.DepositResponseDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaginatedDepositResponse {
  private List<DepositResponseDto> content;
  private long totalElements;
}

package com.andersenlab.etalon.infoservice.util.filter;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.springdoc.core.annotations.ParameterObject;

@Value
@Builder(toBuilder = true)
@AllArgsConstructor
@ParameterObject
public class TransactionFilter {
  @Parameter(name = "accountNumber")
  String accountNumber;

  @Parameter(name = "type")
  String type;

  @Parameter(name = "startDate")
  String startDate;

  @Parameter(name = "endDate")
  String endDate;

  @Parameter(name = "pageNo")
  Integer pageNo;

  @Parameter(name = "pageSize")
  Integer pageSize;

  @Parameter(name = "sortBy")
  String sortBy;

  @Parameter(name = "orderBy")
  String orderBy;
}

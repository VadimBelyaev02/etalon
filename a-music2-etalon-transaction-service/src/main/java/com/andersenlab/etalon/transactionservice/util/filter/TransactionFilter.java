package com.andersenlab.etalon.transactionservice.util.filter;

import io.swagger.v3.oas.annotations.Parameter;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Setter;
import lombok.Value;
import org.springdoc.core.annotations.ParameterObject;

@Value
@Setter
@Builder
@AllArgsConstructor
@ParameterObject
public class TransactionFilter {
  @Parameter(name = "accountNumber")
  List<String> accountNumber;

  @Parameter(name = "type")
  String type;

  @Parameter(name = "startDate")
  String startDate;

  @Parameter(name = "endDate")
  String endDate;

  @Parameter(name = "transactionGroup")
  String transactionGroup;

  @Parameter(name = "transactionStatus")
  String transactionStatus;

  @Parameter(name = "amountFrom")
  BigDecimal amountFrom;

  @Parameter(name = "amountTo")
  BigDecimal amountTo;

  @Parameter(name = "pageNo")
  Integer pageNo;

  @Parameter(name = "pageSize")
  Integer pageSize;

  @Parameter(name = "sortBy")
  String sortBy;

  @Parameter(name = "orderBy")
  String orderBy;

  @Parameter(name = "withEvents")
  Boolean withEvents;
}

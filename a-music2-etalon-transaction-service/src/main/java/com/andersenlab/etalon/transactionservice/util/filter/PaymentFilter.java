package com.andersenlab.etalon.transactionservice.util.filter;

import io.swagger.v3.oas.annotations.Parameter;
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
public class PaymentFilter {
  @Parameter String type;
}

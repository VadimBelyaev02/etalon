package com.andersenlab.etalon.loanservice.util;

import com.andersenlab.etalon.loanservice.dto.loan.request.GuarantorRequestDto;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LoanOrderUtils {
  public static Set<String> collectPeselsFromGuarantors(
      Set<GuarantorRequestDto> guarantorRequestDtos) {
    return guarantorRequestDtos.stream()
        .map(GuarantorRequestDto::pesel)
        .collect(Collectors.toSet());
  }
}

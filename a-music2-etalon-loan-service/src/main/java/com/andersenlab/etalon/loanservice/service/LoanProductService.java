package com.andersenlab.etalon.loanservice.service;

import com.andersenlab.etalon.loanservice.dto.loan.response.LoanProductResponseDto;
import java.util.List;

public interface LoanProductService {

  List<LoanProductResponseDto> getAllLoanProducts();
}

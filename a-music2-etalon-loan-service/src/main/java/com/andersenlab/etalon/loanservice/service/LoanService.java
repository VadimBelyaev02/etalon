package com.andersenlab.etalon.loanservice.service;

import com.andersenlab.etalon.loanservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.loanservice.dto.loan.request.CollectLoanRequestDto;
import com.andersenlab.etalon.loanservice.dto.loan.request.LoanPaymentRequestDto;
import com.andersenlab.etalon.loanservice.dto.loan.response.LoanDetailedResponseDto;
import com.andersenlab.etalon.loanservice.dto.loan.response.LoanResponseDto;
import com.andersenlab.etalon.loanservice.util.enums.LoanStatus;
import java.util.List;

public interface LoanService {

  List<LoanResponseDto> getAllLoans(String id, LoanStatus status);

  LoanDetailedResponseDto getDetailedLoan(Long loanId, String userId);

  MessageResponseDto openNewLoan(final String userId, final CollectLoanRequestDto dto);

  MessageResponseDto makeLoanPayment(Long loanId, final LoanPaymentRequestDto requestDto);
}

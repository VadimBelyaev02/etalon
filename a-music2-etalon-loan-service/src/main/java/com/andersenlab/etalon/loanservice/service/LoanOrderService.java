package com.andersenlab.etalon.loanservice.service;

import com.andersenlab.etalon.loanservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.loanservice.dto.loan.request.LoanOrderRequestDto;
import com.andersenlab.etalon.loanservice.dto.loan.response.LoanOrderDetailedResponseDto;
import com.andersenlab.etalon.loanservice.dto.loan.response.LoanOrderResponseDto;
import java.util.List;

public interface LoanOrderService {

  List<LoanOrderResponseDto> getAllLoanOrdersByUserId(String userId);

  LoanOrderDetailedResponseDto getLoanOrderDetailed(Long orderId, String userId);

  MessageResponseDto createNewLoanOrder(String userId, LoanOrderRequestDto dto);
}

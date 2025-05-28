package com.andersenlab.etalon.loanservice.controller.impl;

import com.andersenlab.etalon.loanservice.controller.LoanController;
import com.andersenlab.etalon.loanservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.loanservice.dto.loan.annotation.LoanPaymentRequest;
import com.andersenlab.etalon.loanservice.dto.loan.request.CollectLoanRequestDto;
import com.andersenlab.etalon.loanservice.dto.loan.request.LoanPaymentRequestDto;
import com.andersenlab.etalon.loanservice.dto.loan.response.LoanDetailedResponseDto;
import com.andersenlab.etalon.loanservice.dto.loan.response.LoanResponseDto;
import com.andersenlab.etalon.loanservice.interceptor.AuthenticationHolder;
import com.andersenlab.etalon.loanservice.service.LoanService;
import com.andersenlab.etalon.loanservice.util.enums.LoanStatus;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(LoanController.LOANS_URL)
@RequiredArgsConstructor
@Tag(name = "Loan")
public class LoanControllerImpl implements LoanController {

  private final LoanService loanService;
  private final AuthenticationHolder authenticationHolder;

  @GetMapping
  public List<LoanResponseDto> getAllLoans(
      @RequestParam(value = "status", required = false) LoanStatus status) {
    return loanService.getAllLoans(authenticationHolder.getUserId(), status);
  }

  @GetMapping({LOAN_ID_URI})
  public LoanDetailedResponseDto getDetailedLoan(@PathVariable Long loanId) {
    return loanService.getDetailedLoan(loanId, authenticationHolder.getUserId());
  }

  @PostMapping()
  public MessageResponseDto openNewLoan(@RequestBody CollectLoanRequestDto dto) {
    return loanService.openNewLoan(authenticationHolder.getUserId(), dto);
  }

  @PostMapping(LOAN_ID_URI)
  public MessageResponseDto makeLoanPayment(
      @PathVariable Long loanId, @LoanPaymentRequest @Valid LoanPaymentRequestDto requestDto) {
    return loanService.makeLoanPayment(loanId, requestDto);
  }
}

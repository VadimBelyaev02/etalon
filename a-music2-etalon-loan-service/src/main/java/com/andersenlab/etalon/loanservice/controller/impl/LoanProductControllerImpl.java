package com.andersenlab.etalon.loanservice.controller.impl;

import com.andersenlab.etalon.loanservice.controller.LoanProductController;
import com.andersenlab.etalon.loanservice.dto.loan.response.LoanProductResponseDto;
import com.andersenlab.etalon.loanservice.service.LoanProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(LoanProductController.PRODUCTS_URL)
@RequiredArgsConstructor
@Tag(name = "Loan Product")
public class LoanProductControllerImpl implements LoanProductController {

  private final LoanProductService loanProductService;

  @GetMapping()
  public List<LoanProductResponseDto> getAllLoanProducts() {
    return loanProductService.getAllLoanProducts();
  }
}

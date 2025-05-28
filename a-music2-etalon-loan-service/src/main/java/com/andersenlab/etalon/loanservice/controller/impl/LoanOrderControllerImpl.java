package com.andersenlab.etalon.loanservice.controller.impl;

import static com.andersenlab.etalon.loanservice.controller.LoanOrderController.LOAN_ORDERS_URL;
import static com.andersenlab.etalon.loanservice.controller.LoanOrderController.ORDER_ID_PATH;

import com.andersenlab.etalon.loanservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.loanservice.dto.loan.request.LoanOrderRequestDto;
import com.andersenlab.etalon.loanservice.dto.loan.response.LoanOrderDetailedResponseDto;
import com.andersenlab.etalon.loanservice.dto.loan.response.LoanOrderResponseDto;
import com.andersenlab.etalon.loanservice.interceptor.AuthenticationHolder;
import com.andersenlab.etalon.loanservice.service.LoanOrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(LOAN_ORDERS_URL)
@RequiredArgsConstructor
@Tag(name = "Loan Order")
public class LoanOrderControllerImpl {

  private final LoanOrderService loanOrderService;
  private final AuthenticationHolder authenticationHolder;

  @GetMapping
  public List<LoanOrderResponseDto> getAllLoanOrders() {
    return loanOrderService.getAllLoanOrdersByUserId(authenticationHolder.getUserId());
  }

  @GetMapping(ORDER_ID_PATH)
  public LoanOrderDetailedResponseDto getLoanOrderDetailed(@PathVariable("orderId") Long orderId) {
    return loanOrderService.getLoanOrderDetailed(orderId, authenticationHolder.getUserId());
  }

  @PostMapping()
  public MessageResponseDto createLoanOrder(@RequestBody LoanOrderRequestDto dto) {
    return loanOrderService.createNewLoanOrder(authenticationHolder.getUserId(), dto);
  }
}

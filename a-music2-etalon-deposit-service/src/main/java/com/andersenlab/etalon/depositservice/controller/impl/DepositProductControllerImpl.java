package com.andersenlab.etalon.depositservice.controller.impl;

import static com.andersenlab.etalon.depositservice.controller.DepositProductController.PRODUCTS_URL;

import com.andersenlab.etalon.depositservice.dto.deposit.response.DepositProductResponseDto;
import com.andersenlab.etalon.depositservice.service.business.DepositProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(PRODUCTS_URL)
@RequiredArgsConstructor
public class DepositProductControllerImpl {

  private final DepositProductService depositProductService;

  @GetMapping
  public List<DepositProductResponseDto> getAllDepositProducts() {
    return depositProductService.getAllDepositProducts();
  }
}

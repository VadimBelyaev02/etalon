package com.andersenlab.etalon.cardservice.controller.impl;

import com.andersenlab.etalon.cardservice.controller.CardProductController;
import com.andersenlab.etalon.cardservice.dto.card.response.CardProductResponseDto;
import com.andersenlab.etalon.cardservice.service.CardProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(CardProductController.CARD_PRODUCTS_URL)
@RequiredArgsConstructor
public class CardProductControllerImpl implements CardProductController {

  private final CardProductService cardProductService;

  @GetMapping
  public List<CardProductResponseDto> getAllCardProducts() {
    return cardProductService.getAllCardProducts();
  }
}

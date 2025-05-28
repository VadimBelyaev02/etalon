package com.andersenlab.etalon.cardservice.service;

import com.andersenlab.etalon.cardservice.dto.card.response.CardProductResponseDto;
import java.util.List;

public interface CardProductService {

  List<CardProductResponseDto> getAllCardProducts();

  CardProductResponseDto getCardProductById(Long id);
}

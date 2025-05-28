package com.andersenlab.etalon.cardservice.service.impl;

import com.andersenlab.etalon.cardservice.dto.card.response.CardProductResponseDto;
import com.andersenlab.etalon.cardservice.entity.CardProductEntity;
import com.andersenlab.etalon.cardservice.exception.BusinessException;
import com.andersenlab.etalon.cardservice.mapper.ProductMapper;
import com.andersenlab.etalon.cardservice.repository.ProductRepository;
import com.andersenlab.etalon.cardservice.service.CardProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardProductServiceImpl implements CardProductService {

  private final ProductMapper productMapper;
  private final ProductRepository productRepository;

  @Override
  public List<CardProductResponseDto> getAllCardProducts() {
    return productMapper.toDtoList(productRepository.findAll());
  }

  @Override
  public CardProductResponseDto getCardProductById(Long id) {
    CardProductEntity cardProduct =
        productRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new BusinessException(
                        HttpStatus.NOT_FOUND,
                        String.format(BusinessException.CARD_PRODUCT_NOT_FOUND, id)));
    return productMapper.toDto(cardProduct);
  }
}

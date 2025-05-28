package com.andersenlab.etalon.cardservice.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.andersenlab.etalon.cardservice.MockData;
import com.andersenlab.etalon.cardservice.dto.card.response.CardProductResponseDto;
import com.andersenlab.etalon.cardservice.entity.CardProductEntity;
import com.andersenlab.etalon.cardservice.entity.CurrencyEntity;
import com.andersenlab.etalon.cardservice.mapper.ProductMapper;
import com.andersenlab.etalon.cardservice.mapper.ProductMapperImpl;
import com.andersenlab.etalon.cardservice.repository.ProductRepository;
import com.andersenlab.etalon.cardservice.service.impl.CardProductServiceImpl;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CardProductServiceTest {

  private CardProductEntity cardProductEntity;
  @Mock private ProductRepository productRepository;

  @Spy private ProductMapper mapper = new ProductMapperImpl();
  @InjectMocks private CardProductServiceImpl underTest;

  @BeforeEach
  void setUp() {
    cardProductEntity = MockData.getValidCardProductEntity();
  }

  @Test
  void whenGetAllCardProducts_shouldSuccess() {
    // given
    when(productRepository.findAll()).thenReturn(List.of(cardProductEntity));

    // when
    final List<CardProductResponseDto> result = underTest.getAllCardProducts();

    // then
    verify(productRepository, times(1)).findAll();
    assertEquals(cardProductEntity.getId(), result.get(0).id());
    assertEquals(cardProductEntity.getProductType(), result.get(0).productType());
    assertEquals(cardProductEntity.getName(), result.get(0).name());
    assertEquals(cardProductEntity.getIssuer(), result.get(0).issuer());
    assertEquals(
        cardProductEntity.getAvailableCurrencies().stream()
            .map(CurrencyEntity::getCurrencyCode)
            .toList(),
        result.get(0).availableCurrencies());
    assertEquals(cardProductEntity.getValidity(), result.get(0).validity());
    assertEquals(cardProductEntity.getIssuanceFee(), result.get(0).issuanceFee());
    assertEquals(cardProductEntity.getMaintenanceFee(), result.get(0).maintenanceFee());
    assertEquals(cardProductEntity.getApr(), result.get(0).apr());
    assertEquals(cardProductEntity.getCashback(), result.get(0).cashback());
  }

  @Test
  void whenGetEmptyListOfProducts_shouldSuccess() {
    // given
    when(productRepository.findAll()).thenReturn(List.of());

    // when
    final List<CardProductResponseDto> result = underTest.getAllCardProducts();

    // then
    verify(productRepository, times(1)).findAll();
    assertEquals(List.of(), result);
  }
}

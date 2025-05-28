package com.andersenlab.etalon.cardservice.mapper;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

import com.andersenlab.etalon.cardservice.dto.card.response.CardProductResponseDto;
import com.andersenlab.etalon.cardservice.entity.CardProductEntity;
import com.andersenlab.etalon.cardservice.entity.CurrencyEntity;
import com.andersenlab.etalon.cardservice.util.enums.Currency;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ProductMapper {

  @Mapping(
      source = "availableCurrencies",
      target = "availableCurrencies",
      qualifiedByName = "currencyEntitiesToCurrencies")
  CardProductResponseDto toDto(CardProductEntity cardProductEntity);

  @Mapping(
      source = "availableCurrencies",
      target = "availableCurrencies",
      qualifiedByName = "currenciesToCurrencyEntities")
  CardProductEntity toEntity(CardProductResponseDto cardProductResponseDto);

  @Mapping(target = "availableCurrencies", qualifiedByName = "currencyEntitiesToCurrencies")
  List<CardProductResponseDto> toDtoList(List<CardProductEntity> cardProductEntities);

  @Named("currencyEntitiesToCurrencies")
  default List<Currency> currencyEntitiesToCurrencies(List<CurrencyEntity> currencyEntities) {
    return emptyIfNull(currencyEntities).stream().map(CurrencyEntity::getCurrencyCode).toList();
  }

  @Named("currenciesToCurrencyEntities")
  default List<CurrencyEntity> currenciesToCurrencyEntities(List<Currency> currencies) {
    return emptyIfNull(currencies).stream()
        .map(currency -> CurrencyEntity.builder().currencyCode(currency).build())
        .collect(Collectors.toList());
  }
}

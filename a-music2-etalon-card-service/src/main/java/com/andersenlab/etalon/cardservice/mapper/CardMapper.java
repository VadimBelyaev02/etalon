package com.andersenlab.etalon.cardservice.mapper;

import com.andersenlab.etalon.cardservice.dto.account.response.AccountDetailedResponseDto;
import com.andersenlab.etalon.cardservice.dto.account.response.AccountResponseDto;
import com.andersenlab.etalon.cardservice.dto.card.response.CardDetailedResponseDto;
import com.andersenlab.etalon.cardservice.dto.card.response.CardResponseDto;
import com.andersenlab.etalon.cardservice.dto.card.response.ShortCardInfoDto;
import com.andersenlab.etalon.cardservice.entity.CardEntity;
import java.util.Collections;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CardMapper {

  @Mapping(target = "balance", ignore = true)
  @Mapping(target = "currency", ignore = true)
  @Mapping(target = "accounts", ignore = true)
  @Mapping(source = "product.productType", target = "productType")
  @Mapping(source = "product.name", target = "productName")
  @Mapping(source = "product.issuer", target = "issuer")
  CardResponseDto toDto(final CardEntity cardEntity);

  @Mapping(source = "product.productType", target = "productType")
  @Mapping(source = "product.name", target = "productName")
  @Mapping(source = "product.issuer", target = "issuer")
  @Mapping(target = "balance", ignore = true)
  @Mapping(target = "currency", ignore = true)
  @Mapping(target = "accounts", ignore = true)
  CardDetailedResponseDto toDetailedDto(final CardEntity cardEntity);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "product.name", target = "cardProductName")
  @Mapping(source = "number", target = "maskedCardNumber", qualifiedByName = "maskCardNumber")
  ShortCardInfoDto toShortCardDto(final CardEntity cardEntity);

  default CardResponseDto mapToDtoWithAccountData(
      CardEntity cardEntity, AccountDetailedResponseDto account) {
    CardResponseDto cardResponseDto = toDto(cardEntity);
    return cardResponseDto.toBuilder()
        .balance(account.balance())
        .currency(account.currency())
        .accounts(Collections.singletonMap(account.iban(), account.currency()))
        .build();
  }

  @InheritConfiguration(name = "toDetailedDto")
  default CardDetailedResponseDto mapToDetailedDtoWithAccountData(
      CardEntity cardEntity, AccountDetailedResponseDto account) {
    CardDetailedResponseDto cardDetailedResponseDto = toDetailedDto(cardEntity);
    return cardDetailedResponseDto.toBuilder()
        .balance(account.balance())
        .currency(account.currency())
        .accounts(Collections.singletonMap(account.iban(), account.currency()))
        .build();
  }

  @InheritConfiguration(name = "toDetailedDto")
  default CardDetailedResponseDto mapToDetailedDtoWithAccountData(
      CardEntity cardEntity, AccountResponseDto account) {
    CardDetailedResponseDto cardDetailedResponseDto = toDetailedDto(cardEntity);
    return cardDetailedResponseDto.toBuilder()
        .balance(account.balance())
        .currency(account.currency())
        .accounts(Collections.singletonMap(account.iban(), account.currency()))
        .build();
  }

  @Named("maskCardNumber")
  static String maskCardNumber(String number) {
    if (number == null || number.length() < 4) {
      return "****";
    }
    return "*" + number.substring(number.length() - 4);
  }
}

package com.andersenlab.etalon.infoservice.mapper;

import static com.andersenlab.etalon.infoservice.exception.TechnicalException.LOCALE_NOT_FOUND;

import com.andersenlab.etalon.infoservice.dto.response.BankContactsResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.BankOperationModeResponseDto;
import com.andersenlab.etalon.infoservice.entity.BankAddressTranslationsEntity;
import com.andersenlab.etalon.infoservice.entity.BankContactsEntity;
import com.andersenlab.etalon.infoservice.entity.BankContactsOperationModeEntity;
import com.andersenlab.etalon.infoservice.exception.TechnicalException;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.http.HttpStatus;

@Mapper(componentModel = "spring")
public interface BankContactsMapper {

  @Mapping(target = "operationModes", source = "entity.bankContactsOperationModes")
  @Mapping(target = "address", source = "entity", qualifiedByName = "mapAddress")
  BankContactsResponseDto toDto(final BankContactsEntity entity, @Context String locale);

  @Named("mapAddress")
  default String mapAddress(final BankContactsEntity entity, @Context String locale) {
    return entity.getBankAddressTranslations().stream()
        .filter(address -> address.getLocale().equals(locale))
        .map(BankAddressTranslationsEntity::getValue)
        .findFirst()
        .orElseThrow(
            () ->
                new TechnicalException(
                    HttpStatus.INTERNAL_SERVER_ERROR, LOCALE_NOT_FOUND + locale));
  }

  BankOperationModeResponseDto toDto(final BankContactsOperationModeEntity entity);
}

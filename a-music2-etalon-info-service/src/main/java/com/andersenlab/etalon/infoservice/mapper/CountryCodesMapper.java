package com.andersenlab.etalon.infoservice.mapper;

import com.andersenlab.etalon.infoservice.dto.response.CountryCodesResponseDto;
import com.andersenlab.etalon.infoservice.entity.CountryCodesEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = ImageUrlMapperHelper.class)
public interface CountryCodesMapper {

  @Mapping(target = "countryName", source = "countryNameEn")
  @Mapping(target = "imageUrl", source = "imageKey", qualifiedByName = "mapImageUrl")
  CountryCodesResponseDto toDtoEn(final CountryCodesEntity countryCodesEntity);

  @Mapping(target = "countryName", source = "countryNamePl")
  @Mapping(target = "imageUrl", source = "imageKey", qualifiedByName = "mapImageUrl")
  CountryCodesResponseDto toDtoPl(final CountryCodesEntity countryCodesEntity);
}

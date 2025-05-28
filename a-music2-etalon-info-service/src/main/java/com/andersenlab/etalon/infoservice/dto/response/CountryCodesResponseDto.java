package com.andersenlab.etalon.infoservice.dto.response;

import lombok.Builder;

@Builder(toBuilder = true)
public record CountryCodesResponseDto(
    Long id, String countryName, String phoneCode, String imageUrl) {}

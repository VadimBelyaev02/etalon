package com.andersenlab.etalon.infoservice.dto;

import lombok.Builder;
import org.springframework.core.io.ByteArrayResource;

@Builder(toBuilder = true)
public record FileDto(String fileName, ByteArrayResource resource, Long size) {}

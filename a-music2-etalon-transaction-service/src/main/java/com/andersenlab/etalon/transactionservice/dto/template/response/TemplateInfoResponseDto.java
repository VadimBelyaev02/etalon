package com.andersenlab.etalon.transactionservice.dto.template.response;

import com.andersenlab.etalon.transactionservice.util.enums.TemplateType;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record TemplateInfoResponseDto(
    Long templateId,
    Long productId,
    String templateName,
    String description,
    BigDecimal amount,
    String source,
    String destination,
    TemplateType templateType,
    ZonedDateTime createAt) {}

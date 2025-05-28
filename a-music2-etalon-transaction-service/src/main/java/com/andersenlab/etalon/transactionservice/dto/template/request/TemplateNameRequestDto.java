package com.andersenlab.etalon.transactionservice.dto.template.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record TemplateNameRequestDto(
    @Schema(example = "Transfer to another account") String templateName) {}

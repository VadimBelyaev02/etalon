package com.andersenlab.etalon.exceptionhandler.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ValidationDto(@NotBlank(message = "must not be blank") String information) {}

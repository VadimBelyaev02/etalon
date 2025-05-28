package com.andersenlab.etalon.infoservice.dto.response;

import com.andersenlab.etalon.infoservice.util.enums.DayOfWeek;
import java.io.Serializable;
import java.time.LocalTime;

public record BankOperationModeResponseDto(
    DayOfWeek dayOfWeek, LocalTime openingTime, LocalTime closingTime) implements Serializable {}

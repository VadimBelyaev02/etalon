package com.andersenlab.etalon.infoservice.service.impl;

import com.andersenlab.etalon.infoservice.config.TimeProvider;
import com.andersenlab.etalon.infoservice.dto.response.DateTimeResponseDto;
import com.andersenlab.etalon.infoservice.service.InfoService;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InfoServiceImpl implements InfoService {
  private final TimeProvider timeProvider;

  @Override
  public DateTimeResponseDto getCurrentDate() {
    ZonedDateTime currentDate =
        timeProvider.getCurrentZonedDateTime().truncatedTo(ChronoUnit.MINUTES);
    return new DateTimeResponseDto(currentDate);
  }
}

package com.andersenlab.etalon.accountservice.config;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class TimeProvider {
  @Value("${app.default.timezone}")
  private String zoneId;

  public ZoneId getZone() {
    return ZoneId.of(zoneId);
  }

  public ZonedDateTime getCurrentZonedDateTime() {
    return ZonedDateTime.now(getZone());
  }
}

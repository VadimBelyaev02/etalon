package com.andersenlab.etalon.infoservice.entity;

import com.andersenlab.etalon.infoservice.util.enums.DayOfWeek;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class AbstractOperationModeEntity {

  @Enumerated(EnumType.STRING)
  @Column(name = "day_of_week", nullable = false)
  private DayOfWeek dayOfWeek;

  @Column(name = "opening_time", nullable = false)
  private LocalTime openingTime;

  @Column(name = "closing_time", nullable = false)
  private LocalTime closingTime;
}

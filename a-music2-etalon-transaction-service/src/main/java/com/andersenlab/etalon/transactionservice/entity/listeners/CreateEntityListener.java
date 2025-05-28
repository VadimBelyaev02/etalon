package com.andersenlab.etalon.transactionservice.entity.listeners;

import com.andersenlab.etalon.transactionservice.config.TimeProvider;
import com.andersenlab.etalon.transactionservice.entity.CreatableEntity;
import jakarta.persistence.PrePersist;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class CreateEntityListener {

  @Autowired private TimeProvider timeProvider;

  @PrePersist
  public void prePersist(final CreatableEntity entity) {
    entity.setCreateAt(timeProvider.getCurrentZonedDateTime());
  }
}

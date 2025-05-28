package com.andersenlab.etalon.infoservice.entity.listeners;

import com.andersenlab.etalon.infoservice.config.TimeProvider;
import com.andersenlab.etalon.infoservice.entity.UpdatableEntity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class UpdateEntityListener {
  @Autowired private TimeProvider timeProvider;

  @PrePersist
  public void prePersist(final UpdatableEntity entity) {
    entity.setCreateAt(timeProvider.getCurrentZonedDateTime());
    entity.setUpdateAt(timeProvider.getCurrentZonedDateTime());
  }

  @PreUpdate
  public void preUpdate(final UpdatableEntity entity) {
    entity.setUpdateAt(timeProvider.getCurrentZonedDateTime());
  }
}

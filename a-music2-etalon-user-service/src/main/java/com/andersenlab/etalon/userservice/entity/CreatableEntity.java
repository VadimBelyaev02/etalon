package com.andersenlab.etalon.userservice.entity;

import com.andersenlab.etalon.userservice.entity.listeners.CreateEntityListener;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@MappedSuperclass
@EntityListeners(CreateEntityListener.class)
public abstract class CreatableEntity {
  @Column(name = "created_at", nullable = false, updatable = false)
  private ZonedDateTime createAt;
}

package com.andersenlab.etalon.depositservice.entity;

import com.andersenlab.etalon.depositservice.entity.listeners.UpdateEntityListener;
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
@EntityListeners(UpdateEntityListener.class)
public abstract class UpdatableEntity extends CreatableEntity {
  @Column(name = "updated_at", nullable = false)
  private ZonedDateTime updateAt;
}

package com.andersenlab.etalon.transactionservice.entity;

import com.andersenlab.etalon.transactionservice.entity.listeners.UpdateEntityListener;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@MappedSuperclass
@EntityListeners(UpdateEntityListener.class)
public abstract class UpdatableEntity extends CreatableEntity {
  @Column(name = "updated_at", nullable = false)
  private ZonedDateTime updateAt;
}

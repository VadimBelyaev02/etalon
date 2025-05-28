package com.andersenlab.etalon.userservice.repository;

import com.andersenlab.etalon.userservice.entity.ResetPasswordEntity;
import com.andersenlab.etalon.userservice.util.ResetPasswordStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResetPasswordRepository extends JpaRepository<ResetPasswordEntity, Long> {
  Optional<ResetPasswordEntity> findByTokenAndStatus(UUID token, ResetPasswordStatus status);
}

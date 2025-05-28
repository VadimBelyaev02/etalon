package com.andersenlab.etalon.userservice.repository;

import com.andersenlab.etalon.userservice.entity.EmailModificationEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailModificationRepository extends JpaRepository<EmailModificationEntity, Long> {
  Optional<List<EmailModificationEntity>> findEmailModificationEntitiesByUserIdAndNewEmail(
      String userId, String newEmail);
}

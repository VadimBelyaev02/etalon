package com.andersenlab.etalon.userservice.repository;

import com.andersenlab.etalon.userservice.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
  boolean existsByEmail(final String email);

  boolean existsByPesel(final String pesel);

  boolean existsByPhoneNumber(String phoneNumber);

  boolean existsById(String cognitoId);

  Optional<UserEntity> findUserEntityByPhoneNumber(final String phoneNumber);

  Optional<UserEntity> findUserEntityByPesel(final String pesel);

  Optional<UserEntity> findByEmail(String email);
}

package com.andersenlab.etalon.userservice.repository;

import com.andersenlab.etalon.userservice.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {}

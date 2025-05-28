package com.andersenlab.etalon.depositservice.repository;

import com.andersenlab.etalon.depositservice.entity.DepositEntity;
import com.andersenlab.etalon.depositservice.util.enums.DepositStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DepositRepository
    extends JpaRepository<DepositEntity, Long>, JpaSpecificationExecutor<DepositEntity> {

  Page<DepositEntity> findAllByUserId(Pageable pageable, String userId);

  Optional<DepositEntity> findByIdAndUserId(Long depositId, String userId);

  List<DepositEntity> findAllByStatusNot(DepositStatus status);
}

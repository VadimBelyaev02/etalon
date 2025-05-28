package com.andersenlab.etalon.transactionservice.repository;

import com.andersenlab.etalon.transactionservice.entity.ScheduledTransferEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduledTransferRepository
    extends JpaRepository<ScheduledTransferEntity, Long>,
        JpaSpecificationExecutor<ScheduledTransferEntity> {}

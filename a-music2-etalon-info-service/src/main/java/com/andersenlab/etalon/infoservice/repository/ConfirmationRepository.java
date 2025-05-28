package com.andersenlab.etalon.infoservice.repository;

import com.andersenlab.etalon.infoservice.entity.ConfirmationEntity;
import com.andersenlab.etalon.infoservice.util.enums.Operation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfirmationRepository extends JpaRepository<ConfirmationEntity, Long> {

  List<ConfirmationEntity> findByTargetIdAndOperation(Long targetId, Operation operation);
}

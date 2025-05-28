package com.andersenlab.etalon.loanservice.repository;

import com.andersenlab.etalon.loanservice.entity.LoanEntity;
import com.andersenlab.etalon.loanservice.util.enums.LoanStatus;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends JpaRepository<LoanEntity, Long> {
  List<LoanEntity> findAllByUserId(String userId);

  Optional<LoanEntity> findByIdAndUserId(Long id, String userId);

  Optional<LoanEntity> findFirstByOrderByIdDesc();

  List<LoanEntity> findAllByStatusAndNextPaymentDateBefore(
      LoanStatus status, ZonedDateTime dateTime);
}

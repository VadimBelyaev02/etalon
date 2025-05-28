package com.andersenlab.etalon.loanservice.repository;

import com.andersenlab.etalon.loanservice.entity.LoanEntity;
import com.andersenlab.etalon.loanservice.entity.LoanPaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanPaymentRepository extends JpaRepository<LoanPaymentEntity, Long> {
  long countByLoan(LoanEntity loan);
}

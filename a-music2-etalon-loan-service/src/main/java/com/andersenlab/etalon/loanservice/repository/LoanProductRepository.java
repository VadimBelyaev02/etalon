package com.andersenlab.etalon.loanservice.repository;

import com.andersenlab.etalon.loanservice.entity.LoanProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanProductRepository extends JpaRepository<LoanProductEntity, Long> {}

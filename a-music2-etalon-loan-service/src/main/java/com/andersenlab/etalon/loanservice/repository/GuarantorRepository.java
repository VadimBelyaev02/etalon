package com.andersenlab.etalon.loanservice.repository;

import com.andersenlab.etalon.loanservice.entity.GuarantorEntity;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuarantorRepository extends JpaRepository<GuarantorEntity, Long> {
  Set<GuarantorEntity> findAllByPeselIn(Set<String> pesels);
}

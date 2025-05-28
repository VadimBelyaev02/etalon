package com.andersenlab.etalon.infoservice.repository;

import com.andersenlab.etalon.infoservice.entity.BankContactsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankContactsRepository extends JpaRepository<BankContactsEntity, Long> {

  BankContactsEntity findFirstByOrderByIdAsc();
}

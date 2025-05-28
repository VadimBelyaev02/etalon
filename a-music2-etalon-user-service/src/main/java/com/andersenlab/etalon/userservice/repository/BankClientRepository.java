package com.andersenlab.etalon.userservice.repository;

import com.andersenlab.etalon.userservice.entity.BankClient;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankClientRepository extends JpaRepository<BankClient, Long> {
  Optional<BankClient> findByPesel(String pesel);
}

package com.andersenlab.etalon.infoservice.repository;

import com.andersenlab.etalon.infoservice.entity.BankBranchEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankBranchRepository extends JpaRepository<BankBranchEntity, Long> {

  List<BankBranchEntity> findAllByCity(final String city);
}

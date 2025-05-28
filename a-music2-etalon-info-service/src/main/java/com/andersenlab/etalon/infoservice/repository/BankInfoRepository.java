package com.andersenlab.etalon.infoservice.repository;

import com.andersenlab.etalon.infoservice.entity.BankInfoEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankInfoRepository extends JpaRepository<BankInfoEntity, Long> {

  Optional<BankInfoEntity> getBankInfoEntityByBankDetails_Bin(String bin);

  Optional<BankInfoEntity> getBankInfoEntityByBankDetails_BankCode(String bankCode);
}

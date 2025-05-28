package com.andersenlab.etalon.accountservice.repository;

import com.andersenlab.etalon.accountservice.entity.AccountEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
  Optional<AccountEntity> findFirstByOrderByIdDesc();

  Optional<AccountEntity> findAccountEntityByIban(String iban);

  Optional<AccountEntity> findAccountEntityByIbanAndUserId(String iban, String userId);

  List<AccountEntity> findAllByIbanIn(List<String> accountsNumbers);

  List<AccountEntity> findAllByIbanInAndUserId(List<String> accountsNumbers, String userId);

  List<AccountEntity> findAllByUserId(String userId);
}

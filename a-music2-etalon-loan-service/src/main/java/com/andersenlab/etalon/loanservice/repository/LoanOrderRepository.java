package com.andersenlab.etalon.loanservice.repository;

import com.andersenlab.etalon.loanservice.entity.LoanOrderEntity;
import com.andersenlab.etalon.loanservice.util.enums.OrderStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanOrderRepository extends JpaRepository<LoanOrderEntity, Long> {

  List<LoanOrderEntity> findAllByUserId(String id);

  Optional<List<LoanOrderEntity>> findAllByStatus(OrderStatus orderStatus);

  Optional<LoanOrderEntity> findByIdAndUserId(Long id, String userId);
}

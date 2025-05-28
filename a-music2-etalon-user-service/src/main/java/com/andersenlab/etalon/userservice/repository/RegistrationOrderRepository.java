package com.andersenlab.etalon.userservice.repository;

import com.andersenlab.etalon.userservice.entity.RegistrationOrder;
import com.andersenlab.etalon.userservice.util.OrderStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationOrderRepository extends JpaRepository<RegistrationOrder, Long> {
  boolean existsByPeselAndOrderStatusIn(String pesel, List<OrderStatus> orderStatuses);

  Optional<RegistrationOrder> findByRegistrationId(String registrationId);

  Optional<RegistrationOrder> findByPesel(String pesel);
}

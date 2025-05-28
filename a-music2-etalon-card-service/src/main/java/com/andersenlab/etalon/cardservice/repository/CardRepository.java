package com.andersenlab.etalon.cardservice.repository;

import com.andersenlab.etalon.cardservice.entity.CardEntity;
import com.andersenlab.etalon.cardservice.util.enums.CardStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository
    extends JpaRepository<CardEntity, Long>, JpaSpecificationExecutor<CardEntity> {
  List<CardEntity> findAllByIdIn(List<Long> id);

  CardEntity findByAccountNumberAndStatus(String accountNumber, CardStatus status);

  List<CardEntity> findAllByUserId(String userId);

  Optional<CardEntity> findByIdAndUserId(Long cardId, String userId);

  Optional<CardEntity> findFirstByOrderByIdDesc();

  Optional<CardEntity> findCardEntityByNumber(String cardNumber);
}

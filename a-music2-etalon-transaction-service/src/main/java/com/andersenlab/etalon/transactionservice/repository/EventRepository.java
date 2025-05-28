package com.andersenlab.etalon.transactionservice.repository;

import com.andersenlab.etalon.transactionservice.entity.EventEntity;
import com.andersenlab.etalon.transactionservice.util.enums.Details;
import com.andersenlab.etalon.transactionservice.util.enums.TransactionStatus;
import com.andersenlab.etalon.transactionservice.util.enums.Type;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long> {

  @Query(
      """
    SELECT e
    FROM EventEntity e
    LEFT JOIN FETCH e.transactionEntity te
    WHERE e.accountNumber in :accountNumbers AND
    e.type in :type AND
     e.eventType != 'FEE' AND
    te.createAt BETWEEN :startDate AND :endDate
        AND (:transactionGroup IS NULL OR te.details = :transactionGroup)
              AND (:transactionStatus IS NULL OR te.status = :transactionStatus)
              AND (:amountFrom IS NULL OR te.amount >= :amountFrom)
              AND (:amountTo IS NULL OR te.amount <= :amountTo)
    """)
  List<EventEntity> findAllByAccountNumbersWithFilters(
      @Param("accountNumbers") List<String> accountNumbers,
      @Param("type") List<Type> type,
      @Param("startDate") ZonedDateTime startDate,
      @Param("endDate") ZonedDateTime endDate,
      @Param("transactionGroup") Details transactionGroup,
      @Param("transactionStatus") TransactionStatus transactionStatus,
      @Param("amountFrom") BigDecimal amountFrom,
      @Param("amountTo") BigDecimal amountTo,
      Pageable pageable);

  List<EventEntity> findAllByCreateAtBetweenAndAccountNumberIn(
      ZonedDateTime start, ZonedDateTime end, List<String> accountsNumbers);

  List<EventEntity> findAllByTransactionEntityId(Long transactionId);
}

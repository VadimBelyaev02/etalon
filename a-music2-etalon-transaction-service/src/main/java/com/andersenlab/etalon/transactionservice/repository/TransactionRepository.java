package com.andersenlab.etalon.transactionservice.repository;

import com.andersenlab.etalon.transactionservice.entity.TransactionEntity;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository
    extends JpaRepository<TransactionEntity, Long>, JpaSpecificationExecutor<TransactionEntity> {
  @Override
  Optional<TransactionEntity> findById(Long aLong);

  @Query(
      value =
          "select t.id from TransactionEntity t where t.processedAt between :start and :end"
              + " and t.receiverAccount in :accounts",
      countQuery =
          "select count (t) from TransactionEntity t where t.processedAt between :start and :end"
              + " and t.receiverAccount in :accounts")
  Page<Long> getTransactionIdsByProcessedDatesAndReceiverAccounts(
      @Param("start") ZonedDateTime start,
      @Param("end") ZonedDateTime end,
      List<String> accounts,
      Pageable pageable);

  @Query(
      value =
          "select distinct t from TransactionEntity t left join fetch t.eventEntityList e where t.id in :ids")
  List<TransactionEntity> getTransactionsWithEventsByIds(@Param("ids") List<Long> ids);

  @Query(
      value =
          "select t.id from TransactionEntity t where t.processedAt between :start and :end"
              + " and t.senderAccount in :accounts",
      countQuery =
          "select count (t) from TransactionEntity t where t.processedAt between :start and :end"
              + " and t.senderAccount in :accounts")
  Page<Long> getTransactionIdsByProcessedDatesAndSenderAccounts(
      @Param("start") ZonedDateTime start,
      @Param("end") ZonedDateTime end,
      List<String> accounts,
      Pageable pageable);
}

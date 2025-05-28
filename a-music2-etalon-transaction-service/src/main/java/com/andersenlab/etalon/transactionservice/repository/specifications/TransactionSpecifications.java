package com.andersenlab.etalon.transactionservice.repository.specifications;

import com.andersenlab.etalon.transactionservice.entity.TransactionEntity;
import com.andersenlab.etalon.transactionservice.util.enums.Details;
import com.andersenlab.etalon.transactionservice.util.enums.TransactionStatus;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TransactionSpecifications {

  public static Specification<TransactionEntity> hasSenderAccount(String senderAccount) {
    return (transactionEntityRoot, cq, cb) ->
        cb.equal(transactionEntityRoot.get("senderAccount"), senderAccount);
  }

  public static Specification<TransactionEntity> hasSenderAccounts(List<String> senderAccounts) {
    return (transactionEntityRoot, cq, cb) -> {
      if (CollectionUtils.isEmpty(senderAccounts)) {
        return cb.conjunction();
      }
      return transactionEntityRoot.get("senderAccount").in(senderAccounts);
    };
  }

  public static Specification<TransactionEntity> hasReceiverAccounts(
      List<String> receiverAccounts) {
    return (transactionEntityRoot, cq, cb) -> {
      if (CollectionUtils.isEmpty(receiverAccounts)) {
        return cb.conjunction();
      }
      return transactionEntityRoot.get("receiverAccount").in(receiverAccounts);
    };
  }

  public static Specification<TransactionEntity> hasProcessedBetween(
      ZonedDateTime processedAtStart, ZonedDateTime processedAtEnd) {
    return (transactionEntityRoot, cq, cb) ->
        cb.between(transactionEntityRoot.get("processedAt"), processedAtStart, processedAtEnd);
  }

  public static Specification<TransactionEntity> hasAmountBetween(
      BigDecimal amountFrom, BigDecimal amountTo) {
    return (transactionEntityRoot, cq, cb) -> {
      Predicate predicate = cb.conjunction();

      if (Objects.nonNull(amountFrom)) {
        predicate =
            cb.and(
                predicate,
                cb.greaterThanOrEqualTo(transactionEntityRoot.get("amount"), amountFrom));
      }
      if (Objects.nonNull(amountTo)) {
        predicate =
            cb.and(predicate, cb.lessThanOrEqualTo(transactionEntityRoot.get("amount"), amountTo));
      }

      return predicate;
    };
  }

  public static Specification<TransactionEntity> hasStatus(TransactionStatus status) {
    return (transactionEntityRoot, cq, cb) -> {
      if (Objects.isNull(status)) {
        return cb.conjunction();
      }
      return cb.equal(transactionEntityRoot.get("status"), status);
    };
  }

  public static Specification<TransactionEntity> hasDetails(Details details) {
    return (transactionEntityRoot, cq, cb) -> {
      if (Objects.isNull(details)) {
        return cb.conjunction();
      }
      return cb.equal(transactionEntityRoot.get("details"), details);
    };
  }
}

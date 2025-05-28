package com.andersenlab.etalon.transactionservice.repository.specifications;

import com.andersenlab.etalon.transactionservice.entity.ScheduledTransferEntity;
import com.andersenlab.etalon.transactionservice.util.filter.ScheduledTransferFilter;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ScheduledTransferSpecification {

  public static Specification<ScheduledTransferEntity> withFilters(
      String userId, ScheduledTransferFilter filter) {
    return ((root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      predicates.add(criteriaBuilder.equal(root.get("userId"), userId));
      if (Objects.nonNull(filter.status())) {
        predicates.add(criteriaBuilder.equal(root.get("status"), filter.status()));
      }

      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    });
  }
}

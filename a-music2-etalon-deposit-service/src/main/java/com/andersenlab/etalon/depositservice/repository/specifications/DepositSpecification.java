package com.andersenlab.etalon.depositservice.repository.specifications;

import com.andersenlab.etalon.depositservice.entity.DepositEntity;
import com.andersenlab.etalon.depositservice.util.enums.DepositStatus;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class DepositSpecification {

  public static Specification<DepositEntity> hasUserId(String userId) {
    return (depositEntityRoot, cq, cb) -> cb.equal(depositEntityRoot.get("userId"), userId);
  }

  public static Specification<DepositEntity> hasAccountNumber(String accountNumber) {
    return (depositEntityRoot, cq, cb) -> {
      if (ObjectUtils.isEmpty(accountNumber)) {
        return cb.conjunction();
      }
      return cb.or(
          cb.like(depositEntityRoot.get("interestAccountNumber"), "%" + accountNumber + "%"),
          cb.like(depositEntityRoot.get("finalTransferAccountNumber"), "%" + accountNumber + "%"));
    };
  }

  public static Specification<DepositEntity> hasStatus(List<DepositStatus> statusList) {
    return (depositEntityRoot, cq, cb) -> {
      if (CollectionUtils.isEmpty(statusList)) {
        return cb.conjunction();
      }
      return depositEntityRoot.get("status").in(statusList);
    };
  }
}

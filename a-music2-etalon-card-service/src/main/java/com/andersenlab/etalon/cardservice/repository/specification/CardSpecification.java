package com.andersenlab.etalon.cardservice.repository.specification;

import com.andersenlab.etalon.cardservice.entity.CardEntity;
import com.andersenlab.etalon.cardservice.entity.CardProductEntity;
import com.andersenlab.etalon.cardservice.util.enums.CardStatus;
import com.andersenlab.etalon.cardservice.util.enums.Issuer;
import com.andersenlab.etalon.cardservice.util.enums.ProductType;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Objects;
import org.springframework.data.jpa.domain.Specification;

public class CardSpecification {

  private CardSpecification() {}

  public static Specification<CardEntity> filterByIssuer(Issuer issuer) {
    return (Root<CardEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
      if (Objects.isNull(issuer)) {
        return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
      }
      Join<CardProductEntity, CardEntity> cardIssuer = root.join("product");
      return criteriaBuilder.equal(cardIssuer.<Issuer>get("issuer"), issuer);
    };
  }

  public static Specification<CardEntity> filterByStatus(CardStatus status) {
    return (Root<CardEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
      if (Objects.isNull(status)) {
        return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
      }
      return criteriaBuilder.equal(root.<CardStatus>get("status"), status);
    };
  }

  public static Specification<CardEntity> filterByProductType(ProductType productType) {
    return (Root<CardEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
      if (Objects.isNull(productType)) {
        return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
      }
      Join<CardProductEntity, CardEntity> cardProductType = root.join("product");
      return criteriaBuilder.equal(cardProductType.<ProductType>get("productType"), productType);
    };
  }

  public static Specification<CardEntity> filterByAccountNumber(String accountNumber) {
    return (Root<CardEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
      if (Objects.isNull(accountNumber)) {
        return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
      }
      return criteriaBuilder.equal(root.<String>get("accountNumber"), accountNumber);
    };
  }

  public static Specification<CardEntity> filterByUserId(String userId) {
    return (Root<CardEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
        criteriaBuilder.equal(root.<String>get("userId"), userId);
  }

  public static Specification<CardEntity> hasAccountNumberIn(List<String> accountNumbers) {
    return (Root<CardEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
        root.get("accountNumber").in(accountNumbers);
  }

  public static Specification<CardEntity> hasUserId(String userId) {
    return (Root<CardEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
        criteriaBuilder.equal(root.<String>get("userId"), userId);
  }
}

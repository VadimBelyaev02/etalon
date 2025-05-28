package com.andersenlab.etalon.transactionservice.util;

import com.andersenlab.etalon.transactionservice.dto.transaction.response.TransactionExtendedResponseDto;
import com.andersenlab.etalon.transactionservice.util.enums.Type;
import com.andersenlab.etalon.transactionservice.util.filter.TransactionFilter;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@UtilityClass
public class TransactionUtils {
  public static final Integer DEFAULT_PAGE_NO = 0;
  public static final Integer DEFAULT_PAGE_SIZE = 500;
  public static final String DEFAULT_PAGE_ORDER = "desc";
  public static final String DEFAULT_PAGE_SORT = "createAt";
  public static final String START_DATE = "startDate";
  public static final String END_DATE = "endDate";

  public static List<Type> getEventTypesFromFilter(TransactionFilter filter) {
    return Objects.isNull(filter.getType())
        ? Arrays.stream(Type.values()).toList()
        : Arrays.stream(Type.values())
            .filter(type -> type.equals(EnumUtils.getEnumValue(filter.getType(), Type.class)))
            .toList();
  }

  public static Map<String, ZonedDateTime> checkFilterDates(TransactionFilter filter) {
    ZonedDateTime endDate =
        StringUtils.isBlank(filter.getEndDate())
            ? ZonedDateTime.now()
            : ZonedDateTime.parse(filter.getEndDate());
    ZonedDateTime startDate =
        StringUtils.isBlank(filter.getStartDate())
            ? ZonedDateTime.from(endDate).minusWeeks(1)
            : ZonedDateTime.parse(filter.getStartDate());

    return Map.of(END_DATE, endDate, START_DATE, startDate);
  }

  public static List<String> checkFilterAccountNumber(
      TransactionFilter filter, List<String> accountNumbers) {
    return Objects.isNull(filter.getAccountNumber()) ? accountNumbers : filter.getAccountNumber();
  }

  public static Boolean getWithEventsConditionFromFilter(TransactionFilter filter) {
    return !Objects.isNull(filter.getWithEvents()) && filter.getWithEvents();
  }

  public static PageRequest getPageRequestFromFilter(TransactionFilter filter) {
    String orderBy =
        StringUtils.isBlank(filter.getOrderBy()) ? DEFAULT_PAGE_ORDER : filter.getOrderBy();
    String sortBy =
        StringUtils.isBlank(filter.getSortBy()) ? DEFAULT_PAGE_SORT : filter.getSortBy();
    Integer pageNo = Objects.isNull(filter.getPageNo()) ? DEFAULT_PAGE_NO : filter.getPageNo();
    Integer pageSize =
        Objects.isNull(filter.getPageSize()) ? DEFAULT_PAGE_SIZE : filter.getPageSize();
    return PageRequest.of(
        pageNo,
        pageSize,
        orderBy.equalsIgnoreCase(DEFAULT_PAGE_ORDER)
            ? Sort.by(sortBy).descending()
            : Sort.by(sortBy).ascending());
  }

  public Comparator<TransactionExtendedResponseDto> getSortComparator(Sort sort) {
    return sort.stream()
        .map(
            order -> {
              Comparator<TransactionExtendedResponseDto> comparator =
                  getFieldComparator(order.getProperty());
              return order.isAscending() ? comparator : comparator.reversed();
            })
        .reduce(Comparator::thenComparing)
        .orElse(Comparator.comparing(TransactionExtendedResponseDto::createdAt));
  }

  private Comparator<TransactionExtendedResponseDto> getFieldComparator(String fieldName) {
    return switch (fieldName) {
      case "id" -> Comparator.comparing(TransactionExtendedResponseDto::id);
      case "transactionName" -> Comparator.comparing(TransactionExtendedResponseDto::name);
      case "status" -> Comparator.comparing(TransactionExtendedResponseDto::status);
      case "amount" -> Comparator.comparing(TransactionExtendedResponseDto::totalAmount);
      case "currency" -> Comparator.comparing(TransactionExtendedResponseDto::currency);
      case DEFAULT_PAGE_SORT -> Comparator.comparing(TransactionExtendedResponseDto::createdAt);
      default -> throw new IllegalArgumentException("Invalid sort field: " + fieldName);
    };
  }
}

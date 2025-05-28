package com.andersenlab.etalon.transactionservice.util;

import com.andersenlab.etalon.transactionservice.dto.transaction.response.CustomPageDto;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;

@UtilityClass
public class CustomPageDtoUtils {

  public static <T> CustomPageDto<T> toCustomPageDto(Page<T> page) {
    return new CustomPageDto<>(
        page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements());
  }
}

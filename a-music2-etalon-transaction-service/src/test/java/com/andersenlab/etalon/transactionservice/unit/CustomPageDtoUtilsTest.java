package com.andersenlab.etalon.transactionservice.unit;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.andersenlab.etalon.transactionservice.dto.transaction.response.CustomPageDto;
import com.andersenlab.etalon.transactionservice.util.CustomPageDtoUtils;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

class CustomPageDtoUtilsTest {

  @Test
  void testToCustomPageDto_success() {
    List<String> content = List.of("Item 1", "Item 2", "Item 3");
    Page<String> page = new PageImpl<>(content);

    CustomPageDto<String> customPageDto = CustomPageDtoUtils.toCustomPageDto(page);

    Assertions.assertEquals(content, customPageDto.getContent());
    Assertions.assertEquals(0, customPageDto.getPageNumber());
    Assertions.assertEquals(3, customPageDto.getPageSize());
    Assertions.assertEquals(3L, customPageDto.getTotalElements());
  }

  @Test
  void testPrivateConstructor() throws Exception {
    Constructor<CustomPageDtoUtils> constructor = CustomPageDtoUtils.class.getDeclaredConstructor();
    constructor.setAccessible(true);
    assertThrows(InvocationTargetException.class, constructor::newInstance);
  }
}

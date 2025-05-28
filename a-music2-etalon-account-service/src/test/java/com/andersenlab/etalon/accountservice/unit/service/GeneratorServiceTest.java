package com.andersenlab.etalon.accountservice.unit.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.andersenlab.etalon.accountservice.service.impl.GeneratorServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GeneratorServiceTest {
  private static final String COUNTRY_PREFIX = "PL";
  private static final String BANK_CODE = "234";
  private static final String BRANCH_CODE = "5678";
  private static final String COUNTRY_CODE = "4";
  private static final Long ACCOUNT_ID = 1082L;

  @InjectMocks private GeneratorServiceImpl generatorService;

  @Test
  void whenGenerateIBAN_thenSuccess() {
    // when
    final String result = generatorService.generateIban(ACCOUNT_ID);

    // then
    assertTrue(result.contains(COUNTRY_PREFIX));
    assertTrue(result.contains(BANK_CODE));
    assertTrue(result.contains(BRANCH_CODE));
    assertTrue(result.contains(COUNTRY_CODE));
  }
}

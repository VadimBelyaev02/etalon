package com.andersenlab.etalon.cardservice.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.andersenlab.etalon.cardservice.service.impl.GeneratorServiceImpl;
import com.andersenlab.etalon.cardservice.util.enums.Issuer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GeneratorServiceTest {
  private static final Long SERIAL_NUMBER = 1L;
  private static final int CARD_NUMBER_LENGTH = 16;
  private static final String BIN = "2467";
  private static final String VISA = "4";
  private static final String MASTERCARD = "5";
  private static final int CVV_LENGTH = 3;

  @InjectMocks private GeneratorServiceImpl generator;

  @Test
  void whenGenerateVisaCardNumber_thenReturnVisaCardNumber() {
    // when
    String result = generator.generateCardNumber(Issuer.VISA, SERIAL_NUMBER);

    // then
    assertTrue(result.contains(BIN));
    assertTrue(result.startsWith(VISA));
    assertEquals(CARD_NUMBER_LENGTH, result.length());
  }

  @Test
  void whenGenerateMASTERCARDCardNumber_thenReturnMASTERCARDCardNumber() {
    // when
    String result = generator.generateCardNumber(Issuer.MASTERCARD, SERIAL_NUMBER);

    // then
    assertTrue(result.contains(BIN));
    assertTrue(result.startsWith(MASTERCARD));
    assertEquals(CARD_NUMBER_LENGTH, result.length());
  }

  @Test
  void whenGenerateCVV_thenReturnCVV() {
    // when
    Integer result = generator.generateCVV();

    // then
    assertEquals(CVV_LENGTH, result.toString().length());
  }
}

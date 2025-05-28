package com.andersenlab.etalon.cardservice.unit.util;

import static org.mockito.Mockito.verify;

import com.andersenlab.etalon.cardservice.util.BigDecimalSerializer;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

class BigDecimalSerializerTest {

  private BigDecimalSerializer serializer;
  private JsonGenerator jsonGenerator;
  private SerializerProvider serializerProvider;

  @BeforeEach
  void setUp() {
    serializer = new BigDecimalSerializer();
    jsonGenerator = Mockito.mock(JsonGenerator.class);
    serializerProvider = Mockito.mock(SerializerProvider.class);
  }

  @Test
  void shouldSerializeNullValueAsZero() throws IOException {

    // when
    serializer.serialize(null, jsonGenerator, serializerProvider);

    // then
    verify(jsonGenerator).writeNull();
  }

  @ParameterizedTest
  @CsvSource({
    "0, 0",
    "0.0000001, 0.00",
    "123.456, 123.46",
    "1000, 1000",
    "999.999, 1000.00",
    "1000.5, 1000.50",
    "1000000000000000.9999, 1000000000000001.00"
  })
  void shouldSerializeBigDecimalValues(BigDecimal value, String expected) throws IOException {
    // when
    serializer.serialize(value, jsonGenerator, serializerProvider);

    // then
    verify(jsonGenerator).writeNumber(expected);
  }
}

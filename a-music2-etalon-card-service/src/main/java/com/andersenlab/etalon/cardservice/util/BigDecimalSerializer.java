package com.andersenlab.etalon.cardservice.util;

import static com.andersenlab.etalon.cardservice.util.Constants.DEFAULT_SCALE;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BigDecimalSerializer extends StdScalarSerializer<BigDecimal> {

  public BigDecimalSerializer() {
    super(BigDecimal.class);
  }

  @Override
  public void serialize(
      BigDecimal value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
      throws IOException {
    if (Objects.isNull(value)) {
      jsonGenerator.writeNull();
      return;
    }
    BigDecimal roundedValue =
        value.scale() > 0 ? value.setScale(DEFAULT_SCALE, RoundingMode.HALF_EVEN) : value;
    jsonGenerator.writeNumber(roundedValue.toPlainString());
  }
}

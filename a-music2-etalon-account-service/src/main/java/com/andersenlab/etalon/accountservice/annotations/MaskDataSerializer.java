package com.andersenlab.etalon.accountservice.annotations;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import java.io.IOException;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor
@AllArgsConstructor
public class MaskDataSerializer extends JsonSerializer<String> implements ContextualSerializer {

  private String replaceRegExp;
  private String replaceWith;

  @Override
  public void serialize(String value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException {
    String maskedValue = maskValue(value);
    gen.writeString(maskedValue);
  }

  @Override
  public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) {
    if (Objects.nonNull(property) && Objects.nonNull(property.getAnnotation(JsonMask.class))) {
      JsonMask maskData = property.getAnnotation(JsonMask.class);

      return new MaskDataSerializer(maskData.replaceRegExp(), maskData.replaceWith());
    }
    return this;
  }

  private String maskValue(String value) {
    if (Objects.isNull(value)) return null;

    if (!StringUtils.isEmpty(replaceRegExp)) {
      return value.replaceAll(replaceRegExp, replaceWith);
    }
    return value;
  }
}

package com.andersenlab.etalon.infoservice.parser.impl;

import com.andersenlab.etalon.infoservice.parser.Parser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ExchangeRatesParserImpl implements Parser {
  public static final String TARGET_NODE_VALUE = "/dataSets/0/series";
  public static final String TARGET_NODE_CURRENCY = "/structure/dimensions/series/1/values";
  private final ObjectMapper objectMapper;

  @Override
  public Map<String, Double> parse(String json) throws JsonProcessingException {

    Map<String, Double> values = new HashMap<>();

    JsonNode jsonNode = objectMapper.readTree(json);
    JsonNode jsonTargetNodeValues = jsonNode.at(TARGET_NODE_VALUE);
    JsonNode jsonTargetNodeCurrencies = jsonNode.at(TARGET_NODE_CURRENCY);

    if (Objects.isNull(jsonTargetNodeValues) || Objects.isNull(jsonTargetNodeCurrencies))
      return Map.of();

    Iterator<Map.Entry<String, JsonNode>> jsonTargetNodeValuesIterator =
        jsonTargetNodeValues.fields();
    Iterator<JsonNode> jsonTargetNodeCurrenciesIterator = jsonTargetNodeCurrencies.elements();

    Map.Entry<String, JsonNode> nextValue;
    JsonNode nextCurrency;

    while (jsonTargetNodeValuesIterator.hasNext() && jsonTargetNodeCurrenciesIterator.hasNext()) {
      nextValue = jsonTargetNodeValuesIterator.next();
      nextCurrency = jsonTargetNodeCurrenciesIterator.next();
      Iterator<JsonNode> valueNode = nextValue.getValue().get("observations").elements();

      JsonNode valueNodeIterator = null;
      while (valueNode.hasNext()) {
        valueNodeIterator = valueNode.next();
      }
      if (Objects.nonNull(valueNodeIterator)) {
        values.put(nextCurrency.at("/id").asText(), valueNodeIterator.at("/0").asDouble());
      }
    }
    return values;
  }
}

package com.andersenlab.etalon.infoservice.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Map;

public interface Parser {
  Map<String, Double> parse(String json) throws JsonProcessingException;
}

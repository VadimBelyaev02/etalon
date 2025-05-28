package authorizers.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Iterator;
import java.util.Map;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JsonDataExtractor {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static JsonNode getJsonNode(Map<String, Object> requestParameters) {
    try {
      String jsonString = objectMapper.writeValueAsString(requestParameters);
      return objectMapper.readTree(jsonString);
    } catch (JsonProcessingException e) {
      LogPrinter.toPrint("{getJsonNode} Failed to parse JSON from request parameters: " + e, false);
      throw new RuntimeException("Forbidden");
    }
  }

  public static String getParameterValue(
      Map<String, Object> requestParameters, String... pathComponents) {
    JsonNode currentNode = getJsonNode(requestParameters);

    for (String part : pathComponents) {
      currentNode = currentNode.path(part);
      if (currentNode.isMissingNode()) {
        LogPrinter.toPrint(
            "{getParameterValue} No parameter value found for part -> " + part, false);
        throw new RuntimeException("Forbidden");
      }
    }

    return currentNode.asText();
  }

  public String getParameterKey(Map<String, Object> requestParameters, String pathComponent) {
    JsonNode rootNode = getJsonNode(requestParameters);
    JsonNode stageVariablesNode = rootNode.path(pathComponent);
    if (stageVariablesNode.isObject()) {
      Iterator<String> fieldNames = stageVariablesNode.fieldNames();
      if (fieldNames.hasNext()) {
        return fieldNames.next();
      }
    }
    LogPrinter.toPrint(
        "{getParameterKey} No key value found for parameter -> " + pathComponent, false);
    throw new RuntimeException("Forbidden");
  }
}

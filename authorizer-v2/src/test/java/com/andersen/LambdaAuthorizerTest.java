package com.andersen;

import static org.junit.jupiter.api.Assertions.*;

import authorizers.entity.ApiInfo;
import authorizers.service.ApiInfoService;
import authorizers.utility.JsonDataExtractor;
import com.andersen.testdata.TestDataProvider;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class LambdaAuthorizerTest {

  static ApiInfoService apiInfoService;
  static List<ApiInfo> apiInfos;

  @BeforeAll
  public static void setUp() {
    apiInfoService = new ApiInfoService();
    apiInfos = apiInfoService.createApiInfoList();
  }

  @Test
  void testCreateApiInfoList() {
    assertNotNull(apiInfos, "API info list should not be null");
    assertFalse(apiInfos.isEmpty(), "API info list should not be empty");

    System.out.println("APIs definition from csv:");
    apiInfos.forEach(System.out::println);
  }

  @Test
  void testCreateRequestedApiInfo() {

    Map<String, Object> requestParameters = TestDataProvider.getRequestParameters();

    ApiInfo receivedApiInfo = apiInfoService.createRequestedApiInfo(requestParameters, apiInfos);

    assertNotNull(receivedApiInfo, "Received API info should not be null");

    System.out.println("Received API request:");
    System.out.println(receivedApiInfo);

    String accessToken =
        JsonDataExtractor.getParameterValue(requestParameters, "headers", "authorization");

    assertEquals("api/v1/loans", receivedApiInfo.getResource(), "API path should match");
    assertEquals("GET", receivedApiInfo.getMethod(), "API method should match");
    assertEquals("<token>", accessToken, "API token should match");
  }
}

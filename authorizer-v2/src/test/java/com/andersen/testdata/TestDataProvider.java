package com.andersen.testdata;

import java.util.Map;

public class TestDataProvider {

  public static Map<String, Object> getRequestParameters() {
    return Map.of(
        "version", "2.0",
        "type", "REQUEST",
        "routeArn",
            "arn:aws:execute-api:us-east-1:<accountId>:<serviceID>/$default/GET/api/v1/loans",
        "identitySource", "",
        "routeKey", "ANY /{proxy+}",
        "rawPath", "/api/v1/loans",
        "headers", Map.of("authorization", "<token>"),
        "requestContext",
            Map.of(
                "accountId", "<accountId>",
                "apiId", "<serviceID>",
                "domainName", "api-dev.etalon.andersenlab.dev",
                "domainPrefix", "api-dev",
                "http",
                    Map.of(
                        "method", "GET",
                        "path", "/api/v1/loans",
                        "protocol", "HTTP/1.1",
                        "sourceIp", "178.120.209.10",
                        "userAgent",
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36 Edg/132.0.0.0"),
                "requestId", "E2QjghJ_oAMEbQg=",
                "routeKey", "ANY /{proxy+}",
                "stage", "$default",
                "time", "23/Jan/2025:15:33:29 +0000",
                "timeEpoch", "1737646409213"),
        "pathParameters", Map.of("proxy", "api/v1/loans"),
        "stageVariables", Map.of("LOAN_API_ID", "<serviceID>"));
  }
}

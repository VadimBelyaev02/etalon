package authorizers.service;

import authorizers.LambdaAuthorizer;
import authorizers.entity.ApiInfo;
import authorizers.enums.Scope;
import authorizers.enums.ServiceApiId;
import authorizers.utility.JsonDataExtractor;
import authorizers.utility.LogPrinter;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class ApiInfoService {
  private static final String FILE_NAME = "api.csv";

  public List<ApiInfo> createApiInfoList() {
    List<ApiInfo> apiInfoList = new ArrayList<>();

    try (Reader reader =
        new FileReader(
            Objects.requireNonNull(ApiInfoService.class.getResource("/" + FILE_NAME)).getFile())) {
      getCsvRecords(apiInfoList, CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader));
    } catch (Exception e) {
      LogPrinter.toPrint(
          "{createApiInfoList} An error occurred during getting csv file -> " + e, false);
      throw new RuntimeException("Forbidden");
    }
    return apiInfoList;
  }

  private void getCsvRecords(List<ApiInfo> apiInfoList, CSVParser parse) {
    Iterable<CSVRecord> csvRecords;
    csvRecords = parse;
    for (CSVRecord csvRecord : csvRecords) {
      ApiInfo apiInfo =
          new ApiInfo(
              Scope.valueOf(csvRecord.get("scope")),
              csvRecord.get("method"),
              csvRecord.get("service"),
              csvRecord.get("url"),
              "");
      apiInfoList.add(apiInfo);
    }
  }

  public ApiInfo createRequestedApiInfo(
      Map<String, Object> requestParameters, List<ApiInfo> apiInfos) {
    String serviceIdName = JsonDataExtractor.getParameterKey(requestParameters, "stageVariables");
    String resourcePath =
        JsonDataExtractor.getParameterValue(requestParameters, "pathParameters", "proxy");
    String httpMethod =
        JsonDataExtractor.getParameterValue(requestParameters, "requestContext", "http", "method");
    String routeArn = JsonDataExtractor.getParameterValue(requestParameters, "routeArn");
    String serviceName = getServiceName(serviceIdName);

    Scope scope = defineScope(serviceName, httpMethod, resourcePath, apiInfos);
    return new ApiInfo(scope, httpMethod, serviceName, resourcePath, routeArn);
  }

  private String getServiceName(String serviceIdName) {
    LogPrinter.toPrint(
        "{getServiceName} serviceIdName -> " + serviceIdName, LambdaAuthorizer.LOGS_ENABLED);
    return Arrays.stream(ServiceApiId.values())
        .filter(s -> s.serviceIdName.equalsIgnoreCase(serviceIdName))
        .findFirst()
        .orElseThrow(
            () -> {
              LogPrinter.toPrint(
                  "{getServiceName} Couldn't find serviceId by name -> " + serviceIdName, false);
              throw new RuntimeException("Forbidden");
            })
        .name();
  }

  private Scope defineScope(
      String serviceName, String method, String resourcePath, List<ApiInfo> apiInfos) {
    if (resourcePath.contains("api-int")) {
      return Scope.INTERNAL;
    }
    for (ApiInfo apiInfo : apiInfos) {
      if (apiInfo.getService().equalsIgnoreCase(serviceName)
          && apiInfo.getMethod().equalsIgnoreCase(method)
          && resourcePath.startsWith(apiInfo.getResource().replace("\\*", ""))) {
        return apiInfo.getScope();
      }
    }
    return Scope.PRIVATE;
  }
}

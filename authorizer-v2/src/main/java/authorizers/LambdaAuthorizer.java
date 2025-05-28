package authorizers;

import authorizers.entity.ApiInfo;
import authorizers.service.ApiInfoService;
import authorizers.service.LambdaAuthorizerService;
import authorizers.utility.LogPrinter;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.IamPolicyResponse;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.Getter;

@Getter
public class LambdaAuthorizer implements RequestHandler<Map<String, Object>, IamPolicyResponse> {

  public static final boolean LOGS_ENABLED = false;
  private final List<ApiInfo> apiInfos;
  private final ApiInfoService apiInfoService;
  private final LambdaAuthorizerService lambdaAuthorizerService;

  public LambdaAuthorizer() {
    LogPrinter.toPrint("in constructor", LOGS_ENABLED);
    this.apiInfoService = new ApiInfoService();
    this.lambdaAuthorizerService = new LambdaAuthorizerService();
    this.apiInfos = apiInfoService.createApiInfoList();
  }

  public IamPolicyResponse handleRequest(
      Map<String, Object> requestParameters, final Context context) {

    ApiInfo receivedApiInfo = apiInfoService.createRequestedApiInfo(requestParameters, apiInfos);
    LogPrinter.toPrint("receivedApiInfo: " + receivedApiInfo, LOGS_ENABLED);
    switch (receivedApiInfo.getScope()) {
      case INTERNAL -> {
        LogPrinter.toPrint("request to internal API -> " + receivedApiInfo, LOGS_ENABLED);
        throw new RuntimeException("Unauthorized");
      }
      case PUBLIC -> {
        LogPrinter.toPrint("request to public API -> " + receivedApiInfo, LOGS_ENABLED);
        return lambdaAuthorizerService.generateAccessPolicy(
            receivedApiInfo.getArn(), Collections.emptyMap());
      }
      case PRIVATE -> {
        LogPrinter.toPrint("request to private API -> " + receivedApiInfo, LOGS_ENABLED);
        return lambdaAuthorizerService.proceedPrivateApi(
            requestParameters, receivedApiInfo.getArn());
      }
    }
    LogPrinter.toPrint("request to private resource, without token", LOGS_ENABLED);
    throw new RuntimeException("Unauthorized");
  }
}

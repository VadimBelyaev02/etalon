package authorizers.service;

import static com.amazonaws.services.lambda.runtime.events.IamPolicyResponse.ALLOW;
import static com.amazonaws.services.lambda.runtime.events.IamPolicyResponse.EXECUTE_API_INVOKE;

import authorizers.LambdaAuthorizer;
import authorizers.utility.JsonDataExtractor;
import authorizers.utility.LogPrinter;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.GetUserRequest;
import com.amazonaws.services.cognitoidp.model.GetUserResult;
import com.amazonaws.services.lambda.runtime.events.IamPolicyResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LambdaAuthorizerService {
  private static final AWSCognitoIdentityProvider cognitoClient =
      AWSCognitoIdentityProviderClientBuilder.standard().build();

  private static Map<String, Object> getContextMap(String sub) {
    Map<String, Object> contextMap = new HashMap<>();
    contextMap.put("authenticatedUserId", sub);
    return contextMap;
  }

  public IamPolicyResponse proceedPrivateApi(
      Map<String, Object> requestParameters, String receivedArn) {

    String accessToken =
        JsonDataExtractor.getParameterValue(requestParameters, "headers", "authorization");
    if (Objects.nonNull(accessToken)) {
      if (accessToken.contains("Bearer ")) {
        accessToken = accessToken.replace("Bearer ", "");
      }
      GetUserRequest getUserRequest = new GetUserRequest().withAccessToken(accessToken);
      try {
        GetUserResult getUserResult = cognitoClient.getUser(getUserRequest);
        Map<String, Object> contextMap = getContextMap(getUserResult.getUsername());
        return generateAccessPolicy(receivedArn, contextMap);
      } catch (Exception ex) {
        LogPrinter.toPrint(
            "request to private resource, exception occurred -> " + ex,
            LambdaAuthorizer.LOGS_ENABLED);
        throw new RuntimeException("Unauthorized");
      }
    }
    LogPrinter.toPrint("request to private resource, without token", LambdaAuthorizer.LOGS_ENABLED);
    throw new RuntimeException("Unauthorized");
  }

  public IamPolicyResponse generateAccessPolicy(String arn, Map<String, Object> context) {
    IamPolicyResponse iamPolicyResponse =
        IamPolicyResponse.builder()
            .withPrincipalId("*")
            .withPolicyDocument(
                IamPolicyResponse.PolicyDocument.builder()
                    .withVersion(IamPolicyResponse.VERSION_2012_10_17)
                    .withStatement(
                        List.of(
                            IamPolicyResponse.Statement.builder()
                                .withAction(EXECUTE_API_INVOKE)
                                .withEffect(ALLOW)
                                .withResource(List.of(arn))
                                .build()))
                    .build())
            .withContext(context)
            .build();
    LogPrinter.toPrint("iamPolicyResponse: " + iamPolicyResponse, LambdaAuthorizer.LOGS_ENABLED);
    return iamPolicyResponse;
  }
}

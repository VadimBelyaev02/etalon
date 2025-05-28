Lambda authorizer is used to validate the token contained in the incoming request to our backend system.
Validation occurs through Cognito. Upon successful validation, a policy granting access to all our endpoints is returned.
If the token is missing or validation is unsuccessful, a policy granting access to a limited set of public endpoints is returned.

To return the policy, environment variables (env) are used, which are automatically set through Terraform during the creation of APIs.
You can view existing APIs at the following link:
https://us-east-1.console.aws.amazon.com/apigateway/main/apis?region=us-east-1

The list of APIs looks like this:
![APIs list](images/APIs%20list.png)

For example, let's choose the API info-service-dev. Navigate to Deploy -> Stages.
![Info-service API](images/Info-service%20API.png)

Select the default stage, and on the right-hand side, you will see the "Stage variables" section with the corresponding ID of this API. 
This variable is used during the generation of the returned policy, and in urgent cases, it can be manually set.
![Stage variables example](images/Stage%20variables%20example.png)

To validate the token in the Cognito client, the scope aws.cognito.signin.user.admin must be set.
![Cognito client scopes](images/Cognito%20client%20scopes.png)

To manually set the scope if needed, navigate to Amazon Cognito service. Then, go to the "User pools" -> "App integration" -> select your client.
![Cognito client list](images/Cognito%20client%20list.png)

In the Hosted UI section set the OpenID Connect scopes.

To modify the Lambda function:
1. Create a standard Java project using maven-archetype-quickstart. In the main->java folder, create a package named
   authorizers.
2. Create a class LambdaAuthorizer in the authorizers package and paste the code from the LambdaAuthorizer file.
3. Replace the pom.xml file with the pom file in the current directory.
4. Make any changes to the code as needed.
5. Build the modified project using maven package and place the created JAR file at
   https://us-east-1.console.aws.amazon.com/lambda/home?region=us-east-1#/functions/authorizeRequest?tab=code
   Click on "Upload from" and select .zip or .jar file.
6. Click the "Save" button.

If for any reason you decide to change the package name or class name, you also need to update the handler inside the AWS Lambda service accordingly. 
It should follow the pattern: packageName.className::handleRequest. For example: authorizers.LambdaAuthorizer::handleRequest
![Lambda function runtime settings](images/Lambda%20function%20runtime%20settings.png)

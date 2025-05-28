package com.andersenlab.etalon.userservice.service.impl;

import com.andersenlab.etalon.userservice.exception.BusinessException;
import com.andersenlab.etalon.userservice.service.CognitoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminSetUserPasswordRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminUpdateUserAttributesRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersResponse;

@Service
@Slf4j
@RequiredArgsConstructor
public class CognitoServiceImpl implements CognitoService {
  private static final String EMAIL = "email";
  private static final String EMAIL_VERIFIED = "email_verified";

  @Value("${aws.user.pool-id:user-pool-id}")
  private String userPoolId;

  private final CognitoIdentityProviderClient cognitoIdentityProviderClient;

  @Override
  public void updateUserEmail(String userId, String newEmail) {
    AttributeType emailAttribute = AttributeType.builder().name(EMAIL).value(newEmail).build();
    AttributeType emailVerifiedAttribute =
        AttributeType.builder().name(EMAIL_VERIFIED).value(Boolean.TRUE.toString()).build();
    cognitoIdentityProviderClient.adminUpdateUserAttributes(
        AdminUpdateUserAttributesRequest.builder()
            .userPoolId(userPoolId)
            .username(userId)
            .userAttributes(emailAttribute, emailVerifiedAttribute)
            .build());
  }

  @Override
  public String registerUser(String email, String password) {
    log.info(
        "{registerUser} user registration request in AWS Cognito received, with email-%s"
            .formatted(email));
    AdminCreateUserResponse adminCreateUserResponse;
    try {
      AdminCreateUserRequest adminCreateUserRequest =
          AdminCreateUserRequest.builder()
              .userPoolId(userPoolId)
              .username(email)
              .userAttributes(
                  AttributeType.builder().name(EMAIL).value(email).build(),
                  AttributeType.builder().name(EMAIL_VERIFIED).value("true").build())
              .temporaryPassword(password)
              .messageAction("SUPPRESS")
              .build();

      adminCreateUserResponse =
          cognitoIdentityProviderClient.adminCreateUser(adminCreateUserRequest);

      AdminSetUserPasswordRequest adminSetUserPasswordRequest =
          AdminSetUserPasswordRequest.builder()
              .userPoolId(userPoolId)
              .username(email)
              .password(password)
              .permanent(true)
              .build();
      cognitoIdentityProviderClient.adminSetUserPassword(adminSetUserPasswordRequest);
    } catch (CognitoIdentityProviderException e) {
      throw new BusinessException(
          HttpStatus.BAD_REQUEST, "Error registering user: " + e.getMessage());
    }
    return adminCreateUserResponse.user().username();
  }

  @Override
  public boolean userExists(String email) {
    log.info(
        "{checkUserExistence} checking user existence with email-%s in AWS Cognito DB"
            .formatted(email));
    String filter = "email = \"" + email + "\"";
    ListUsersRequest listUsersRequest =
        ListUsersRequest.builder().userPoolId(userPoolId).filter(filter).build();
    ListUsersResponse listUsersResult = cognitoIdentityProviderClient.listUsers(listUsersRequest);
    if (!listUsersResult.users().isEmpty()) {
      log.info("{checkUserExistence} the following users found by filter criteria");
      listUsersResult.users().forEach(user -> log.info("User: {}", user));
    }
    return !listUsersResult.users().isEmpty();
  }

  @Override
  public boolean updateUserPassword(String email, String newPassword) {

    try {
      if (!userExists(email)) {
        log.warn("{resetUserPassword} user with email-{} does not exist", email);
        return false;
      }

      AdminSetUserPasswordRequest resetPasswordRequest =
          AdminSetUserPasswordRequest.builder()
              .userPoolId(userPoolId)
              .username(email)
              .password(newPassword)
              .permanent(true)
              .build();

      cognitoIdentityProviderClient.adminSetUserPassword(resetPasswordRequest);

      log.info("{resetUserPassword} password reset successful for user with email-{}", email);
      return true;
    } catch (CognitoIdentityProviderException e) {
      log.error(
          "{resetUserPassword} error resetting password for user with email-{}: {}",
          email,
          e.getMessage());
      return false;
    }
  }
}

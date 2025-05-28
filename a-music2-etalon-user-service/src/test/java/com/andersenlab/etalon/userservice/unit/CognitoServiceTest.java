package com.andersenlab.etalon.userservice.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.andersenlab.etalon.userservice.exception.BusinessException;
import com.andersenlab.etalon.userservice.service.impl.CognitoServiceImpl;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminSetUserPasswordRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminSetUserPasswordResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminUpdateUserAttributesRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserType;

@ExtendWith(MockitoExtension.class)
class CognitoServiceTest {
  private static final String EMAIL = "test@test.com";
  private static final String NEW_EMAIL = "new_test@test.com";
  private static final String USER_ID = "testId";
  private static final String PASSWORD = "testPassword";
  private static final String USERNAME = "testuser";
  private static final String EMAIL_ATTRIBUTE = "email";
  private static final String EMAIL_VERIFIED_ATTRIBUTE = "email_verified";

  @Mock private CognitoIdentityProviderClient cognitoIdentityProviderClient;

  @InjectMocks private CognitoServiceImpl cognitoService;

  @Test
  void updateUserEmail_successfulUpdate() {

    cognitoService.updateUserEmail(USER_ID, NEW_EMAIL);

    AttributeType emailAttribute =
        AttributeType.builder().name(EMAIL_ATTRIBUTE).value(NEW_EMAIL).build();
    AttributeType emailVerifiedAttribute =
        AttributeType.builder()
            .name(EMAIL_VERIFIED_ATTRIBUTE)
            .value(Boolean.TRUE.toString())
            .build();
    AdminUpdateUserAttributesRequest adminUpdateUserAttributesRequest =
        AdminUpdateUserAttributesRequest.builder()
            .userPoolId(null)
            .username(USER_ID)
            .userAttributes(emailAttribute, emailVerifiedAttribute)
            .build();

    verify(cognitoIdentityProviderClient)
        .adminUpdateUserAttributes(adminUpdateUserAttributesRequest);
  }

  @Test
  void updateUserEmail_updateFails() {
    doThrow(CognitoIdentityProviderException.builder().message("Error updating email").build())
        .when(cognitoIdentityProviderClient)
        .adminUpdateUserAttributes(any(AdminUpdateUserAttributesRequest.class));

    CognitoIdentityProviderException thrown =
        assertThrows(
            CognitoIdentityProviderException.class,
            () -> {
              cognitoService.updateUserEmail(USER_ID, NEW_EMAIL);
            });

    assertTrue(thrown.getMessage().contains("Error updating email"));
  }

  @Test
  void registerUser_successfulRegistration() {
    AdminCreateUserResponse adminCreateUserResponse =
        AdminCreateUserResponse.builder().user(UserType.builder().username(EMAIL).build()).build();

    when(cognitoIdentityProviderClient.adminCreateUser(any(AdminCreateUserRequest.class)))
        .thenReturn(adminCreateUserResponse);

    AdminSetUserPasswordResponse adminSetUserPasswordResponse =
        AdminSetUserPasswordResponse.builder().build();
    when(cognitoIdentityProviderClient.adminSetUserPassword(any(AdminSetUserPasswordRequest.class)))
        .thenReturn(adminSetUserPasswordResponse);

    String result = cognitoService.registerUser(EMAIL, PASSWORD);

    assertEquals(EMAIL, result);
    verify(cognitoIdentityProviderClient).adminCreateUser(any(AdminCreateUserRequest.class));
    verify(cognitoIdentityProviderClient)
        .adminSetUserPassword(any(AdminSetUserPasswordRequest.class));
  }

  @Test
  void registerUser_registrationFails() {
    when(cognitoIdentityProviderClient.adminCreateUser(any(AdminCreateUserRequest.class)))
        .thenThrow(CognitoIdentityProviderException.builder().message("Error").build());

    BusinessException thrown =
        assertThrows(
            BusinessException.class,
            () -> {
              cognitoService.registerUser(EMAIL, PASSWORD);
            });

    assertEquals(HttpStatus.BAD_REQUEST, thrown.getHttpStatus());
    assertTrue(thrown.getMessage().contains("Error registering user"));
  }

  @Test
  void testUserExists_userFound() {
    UserType user =
        UserType.builder()
            .username(USERNAME)
            .attributes(AttributeType.builder().name(EMAIL_ATTRIBUTE).value(EMAIL).build())
            .build();

    ListUsersResponse mockResponse = ListUsersResponse.builder().users(user).build();

    when(cognitoIdentityProviderClient.listUsers(any(ListUsersRequest.class)))
        .thenReturn(mockResponse);

    boolean result = cognitoService.userExists(EMAIL);

    assertTrue(result);
    verify(cognitoIdentityProviderClient, times(1)).listUsers(any(ListUsersRequest.class));
  }

  @Test
  void testUserExists_userNotFound() {
    ListUsersResponse mockResponse =
        ListUsersResponse.builder().users(Collections.emptyList()).build();

    when(cognitoIdentityProviderClient.listUsers(any(ListUsersRequest.class)))
        .thenReturn(mockResponse);

    boolean result = cognitoService.userExists(EMAIL);

    assertFalse(result);
    verify(cognitoIdentityProviderClient, times(1)).listUsers(any(ListUsersRequest.class));
  }

  @Test
  void testRegisterUser_failure_dueToCognitoException() {
    when(cognitoIdentityProviderClient.adminCreateUser(any(AdminCreateUserRequest.class)))
        .thenThrow(
            CognitoIdentityProviderException.builder().message("Error creating user").build());

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> {
              cognitoService.registerUser(EMAIL, PASSWORD);
            });

    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    assertTrue(exception.getMessage().contains("Error registering user"));
  }
}

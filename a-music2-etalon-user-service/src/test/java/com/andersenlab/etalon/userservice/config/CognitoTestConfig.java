package com.andersenlab.etalon.userservice.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminSetUserPasswordRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminSetUserPasswordResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserType;

@TestConfiguration
public class CognitoTestConfig {
  private static final String USER_POOL_ID = "USER_POOL_ID";

  @Bean
  @Primary
  public CognitoIdentityProviderClient cognitoIdentityProviderClientTest() {
    CognitoIdentityProviderClient mockClient = Mockito.mock(CognitoIdentityProviderClient.class);

    ListUsersRequest userNotExistRequest =
        ListUsersRequest.builder()
            .userPoolId(USER_POOL_ID)
            .filter("email = \"rafalrostworowski@proton.me\"")
            .build();
    ListUsersResponse userNotExistResponse =
        ListUsersResponse.builder().users(Collections.emptyList()).build();
    when(mockClient.listUsers(userNotExistRequest)).thenReturn(userNotExistResponse);

    ListUsersRequest userExists =
        ListUsersRequest.builder()
            .userPoolId(USER_POOL_ID)
            .filter("email = \"krzysztofczartoryski@proton.me\"")
            .build();
    ListUsersResponse userExistsResponse =
        ListUsersResponse.builder()
            .users(UserType.builder().username("krzysztofczartoryski@proton.me").build())
            .build();
    when(mockClient.listUsers(userExists)).thenReturn(userExistsResponse);

    ListUsersRequest userWithConfirmedCodeRequest =
        ListUsersRequest.builder()
            .userPoolId(USER_POOL_ID)
            .filter("email = \"olgamycielska@proton.me\"")
            .build();
    when(mockClient.listUsers(userWithConfirmedCodeRequest)).thenReturn(userNotExistResponse);

    AdminCreateUserRequest createNonExistentUser =
        AdminCreateUserRequest.builder()
            .userPoolId(USER_POOL_ID)
            .username("NozhKuhonny@proton.me")
            .temporaryPassword("123456Te$t")
            .messageAction("SUPPRESS")
            .userAttributes(
                AttributeType.builder().name("email").value("NozhKuhonny@proton.me").build(),
                AttributeType.builder().name("email_verified").value("true").build())
            .build();
    AdminCreateUserResponse createNonExistentUserResponse =
        AdminCreateUserResponse.builder()
            .user(UserType.builder().username("NozhKuhonny@proton.me").build())
            .build();
    when(mockClient.adminCreateUser(createNonExistentUser))
        .thenReturn(createNonExistentUserResponse);

    AdminSetUserPasswordRequest setUserPasswordRequest =
        AdminSetUserPasswordRequest.builder()
            .userPoolId(USER_POOL_ID)
            .username("NozhKuhonny@proton.me")
            .password("123456Te$t")
            .permanent(true)
            .build();
    AdminSetUserPasswordResponse setUserPasswordResponse =
        AdminSetUserPasswordResponse.builder().build();
    when(mockClient.adminSetUserPassword(setUserPasswordRequest))
        .thenReturn(setUserPasswordResponse);

    ListUsersRequest resetPasswordExistingEmailRequest =
        ListUsersRequest.builder()
            .userPoolId(USER_POOL_ID)
            .filter("email = \"test01@gmail.com\"")
            .build();
    ListUsersResponse resetPasswordExistingEmailResponse =
        ListUsersResponse.builder()
            .users(List.of(UserType.builder().username("test01@gmail.com").build()))
            .build();
    when(mockClient.listUsers(resetPasswordExistingEmailRequest))
        .thenReturn(resetPasswordExistingEmailResponse);

    ListUsersRequest resetPasswordNonExistingEmailRequest =
        ListUsersRequest.builder()
            .userPoolId(USER_POOL_ID)
            .filter("email = \"non-existing@example.com\"")
            .build();
    ListUsersResponse resetPasswordNonExistingEmailResponse =
        ListUsersResponse.builder().users(Collections.emptyList()).build();
    when(mockClient.listUsers(resetPasswordNonExistingEmailRequest))
        .thenReturn(resetPasswordNonExistingEmailResponse);

    when(mockClient.adminSetUserPassword(any(AdminSetUserPasswordRequest.class)))
        .thenReturn(AdminSetUserPasswordResponse.builder().build());

    return mockClient;
  }
}

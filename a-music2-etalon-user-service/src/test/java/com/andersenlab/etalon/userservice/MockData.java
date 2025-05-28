package com.andersenlab.etalon.userservice;

import com.andersenlab.etalon.userservice.dto.info.request.CreateConfirmationRequestDto;
import com.andersenlab.etalon.userservice.dto.info.response.ConfirmationResponseDto;
import com.andersenlab.etalon.userservice.dto.info.response.CreateConfirmationResponseDto;
import com.andersenlab.etalon.userservice.dto.modification.confirmations.EmailModificationConfirmationRequestDto;
import com.andersenlab.etalon.userservice.dto.modification.request.UserEmailModificationRequestDto;
import com.andersenlab.etalon.userservice.dto.reset.confirmation.ResetPasswordConfirmationRequestDto;
import com.andersenlab.etalon.userservice.dto.reset.request.ResetPasswordRequestDto;
import com.andersenlab.etalon.userservice.dto.user.request.AddressDto;
import com.andersenlab.etalon.userservice.dto.user.request.CompleteRegistrationRequestDto;
import com.andersenlab.etalon.userservice.dto.user.request.EmploymentDataDto;
import com.andersenlab.etalon.userservice.dto.user.request.InitiateRegistrationRequestDto;
import com.andersenlab.etalon.userservice.dto.user.request.UpdateRegistrationOrderStatusRequestDto;
import com.andersenlab.etalon.userservice.dto.user.request.UserPatchRequestDto;
import com.andersenlab.etalon.userservice.dto.user.request.UserRequestDto;
import com.andersenlab.etalon.userservice.dto.user.response.InitiateRegistrationResponseDto;
import com.andersenlab.etalon.userservice.dto.user.response.PersonalInfoDto;
import com.andersenlab.etalon.userservice.dto.user.response.UserDataResponseDto;
import com.andersenlab.etalon.userservice.entity.*;
import com.andersenlab.etalon.userservice.util.*;
import java.time.ZonedDateTime;
import java.util.UUID;

public class MockData {

  public static EmailModificationConfirmationRequestDto
      getValidEmailModificationConfirmationRequestDto() {
    return new EmailModificationConfirmationRequestDto(1L, "123456");
  }

  public static ConfirmationResponseDto getValidConfirmationResponseDto() {
    return ConfirmationResponseDto.builder()
        .confirmationId(1L)
        .confirmationStatus(ConfirmationStatus.CREATED)
        .build();
  }

  public static CreateConfirmationResponseDto getValidCreateConfirmationResponseDto() {
    return new CreateConfirmationResponseDto(1L);
  }

  public static UserEmailModificationRequestDto getValidEmailModificationRequestDto() {
    return new UserEmailModificationRequestDto("test@gmail.com");
  }

  public static EmailModificationEntity getValidEmailModificationEntity() {
    return EmailModificationEntity.builder()
        .id(1L)
        .userId("user")
        .newEmail("test@gmail.com")
        .status(EmailModificationStatus.CREATED)
        .build();
  }

  public static CreateConfirmationRequestDto getConfirmationWithoutTargetId() {
    return CreateConfirmationRequestDto.builder()
        .confirmationMethod(ConfirmationMethod.EMAIL)
        .operation(Operation.USER_REGISTRATION)
        .isRegistration(true)
        .build();
  }

  public static CreateConfirmationRequestDto getValidConfirmationForRegistration() {
    return CreateConfirmationRequestDto.builder()
        .confirmationMethod(ConfirmationMethod.EMAIL)
        .operation(Operation.USER_REGISTRATION)
        .targetId(1L)
        .isRegistration(true)
        .build();
  }

  public static CompleteRegistrationRequestDto getValidRegistrationRequestDto() {
    return CompleteRegistrationRequestDto.builder()
        .password("testPassword")
        .repeatedPassword("testPassword")
        .build();
  }

  public static BankClient getValidBankClient() {
    return BankClient.builder()
        .id(1L)
        .pesel("96070642313")
        .email("jackson@gmail.com")
        .firstName("Michael")
        .lastName("Jackson")
        .phoneNumber("+496143643167")
        .address(
            AddressEntity.builder()
                .voivodeship("def-voivodeship")
                .city("def-city")
                .street("def-street")
                .building("def-building")
                .apartment("def-apartment")
                .postCode("12-345")
                .build())
        .build();
  }

  public static UpdateRegistrationOrderStatusRequestDto getValidUpdateRegistrationOrderStatus() {
    return UpdateRegistrationOrderStatusRequestDto.builder()
        .registrationOrderId("36c50b72-ada5-4407-b896-20649ba633a2")
        .orderStatus(OrderStatus.COMPLETED)
        .build();
  }

  public static RegistrationOrder getValidSavedRegistrationOrder() {
    return RegistrationOrder.builder()
        .id(1L)
        .registrationId("36c50b72-ada5-4407-b896-20649ba633a2")
        .pesel("96070642313")
        .orderStatus(OrderStatus.COMPLETED)
        .build();
  }

  public static RegistrationOrder getValidUpdatedRegistrationOrder() {
    return RegistrationOrder.builder()
        .id(null)
        .registrationId("36c50b72-ada5-4407-b896-20649ba633a2")
        .pesel("96070642313")
        .orderStatus(OrderStatus.COMPLETED)
        .build();
  }

  public static RegistrationOrder getValidRegistrationOrder() {
    return RegistrationOrder.builder()
        .id(1L)
        .registrationId("36c50b72-ada5-4407-b896-20649ba633a2")
        .pesel("96070642313")
        .orderStatus(OrderStatus.CREATED)
        .build();
  }

  public static UserEntity getValidUserEntity() {
    return UserEntity.builder()
        .id("user")
        .email("test01@gmail.com")
        .pesel("11111111111")
        .firstName("User")
        .lastName("Test")
        .phoneNumber("48226215575")
        .build();
  }

  public static UserDataResponseDto getValidUserData() {
    return UserDataResponseDto.builder()
        .id("user")
        .email("test01@gmail.com")
        .pesel("11111111111")
        .firstName("User")
        .lastName("Test")
        .createAt(ZonedDateTime.parse("2023-06-30T17:48:26.729730Z"))
        .updateAt(ZonedDateTime.parse("2023-06-30T17:48:26.839090Z"))
        .phoneNumber("48226215575")
        .build();
  }

  public static UserRequestDto getValidUserDto() {
    return UserRequestDto.builder()
        .id("user2")
        .firstName("User2")
        .lastName("Test2")
        .email("test201@gmail.com")
        .pesel("11111111119")
        .build();
  }

  public static UserRequestDto getAlreadyExistsUserDto() {
    return UserRequestDto.builder()
        .id("user")
        .firstName("User")
        .lastName("Test")
        .email("test01@gmail.com")
        .pesel("11111111111")
        .build();
  }

  public static UserDataResponseDto getFulfilledUserData() {
    return UserDataResponseDto.builder()
        .id("full-user")
        .firstName("User")
        .lastName("Test")
        .email("test02@gmail.com")
        .pesel("11111111112")
        .phoneNumber("48111222333")
        .address(
            AddressDto.builder()
                .voivodeship("def-voivodeship")
                .city("def-city")
                .street("def-street")
                .building("def-building")
                .apartment("def-apartment")
                .postCode("12-345")
                .build())
        .employmentData(
            EmploymentDataDto.builder().position("def-position").placeOfWork("def-place").build())
        .build();
  }

  public static UserPatchRequestDto getUserPatchRequestDto() {
    return UserPatchRequestDto.builder()
        .employmentData(
            EmploymentDataDto.builder().position("position").placeOfWork("Place").build())
        .build();
  }

  public static UserPatchRequestDto getUserPatchRequestWithWrongDataDto() {
    return UserPatchRequestDto.builder()
        .employmentData(EmploymentDataDto.builder().position("4").placeOfWork("5").build())
        .build();
  }

  public static UserPatchRequestDto getUserPatchRequestWithWrongPositionDto() {
    return UserPatchRequestDto.builder()
        .employmentData(EmploymentDataDto.builder().position("4").build())
        .build();
  }

  public static UserPatchRequestDto getUserPatchRequestWithInvalidCharacter() {
    return UserPatchRequestDto.builder()
        .employmentData(EmploymentDataDto.builder().position("Developer=").build())
        .build();
  }

  public static UserPatchRequestDto getUserPatchRequestStartingWithHyphen() {
    return UserPatchRequestDto.builder()
        .employmentData(EmploymentDataDto.builder().position("-Developer").build())
        .build();
  }

  public static UserPatchRequestDto getUserPatchRequestDtoWithNullFields() {
    return UserPatchRequestDto.builder()
        .employmentData(EmploymentDataDto.builder().position("Position").build())
        .build();
  }

  public static AddressDto getValidAddressDto() {
    return AddressDto.builder()
        .voivodeship("Voivodeship")
        .city("City")
        .street("Street")
        .building("12E")
        .apartment("12E")
        .postCode("55-555")
        .build();
  }

  public static EmploymentDataDto getValidEmploymentDataDto() {
    return EmploymentDataDto.builder().position("Position").placeOfWork("Place").build();
  }

  public static InitiateRegistrationResponseDto getInitiateRegistrationResponseDto() {
    return InitiateRegistrationResponseDto.builder()
        .confirmationId(1L)
        .confirmationMethod(ConfirmationMethod.EMAIL)
        .maskedEmail("r****@proton.me")
        .build();
  }

  public static InitiateRegistrationRequestDto getValidInitiateRegistrationRequestIntegration() {
    return InitiateRegistrationRequestDto.builder()
        .pesel("14050143693")
        .isPrivacyPolicyAccepted(true)
        .build();
  }

  public static InitiateRegistrationRequestDto getInitiateRegistrationRequestWithCodeConfirmed() {
    return InitiateRegistrationRequestDto.builder()
        .pesel("85060243694")
        .isPrivacyPolicyAccepted(true)
        .build();
  }

  public static InitiateRegistrationRequestDto getValidInitiateRegistrationRequestDto() {
    return InitiateRegistrationRequestDto.builder()
        .pesel("96070642313")
        .isPrivacyPolicyAccepted(true)
        .build();
  }

  public static InitiateRegistrationRequestDto getExtraDigitInitiateRegistrationRequestDto() {
    return InitiateRegistrationRequestDto.builder()
        .pesel("1405014369O")
        .isPrivacyPolicyAccepted(true)
        .build();
  }

  public static InitiateRegistrationRequestDto getInvalidInitiateRegistrationRequestDto() {
    return InitiateRegistrationRequestDto.builder()
        .pesel("96070643690")
        .isPrivacyPolicyAccepted(true)
        .build();
  }

  public static InitiateRegistrationRequestDto
      getAlreadyRegisteredInitiateRegistrationRequestDto() {
    return InitiateRegistrationRequestDto.builder()
        .pesel("96070643692")
        .isPrivacyPolicyAccepted(true)
        .build();
  }

  public static PersonalInfoDto getPersonalInfoDto() {
    return PersonalInfoDto.builder()
        .registrationId(UUID.randomUUID().toString())
        .firstName("Krzysztof")
        .lastName("Czartoryski")
        .email("krzysztofczartoryski@proton.me")
        .phoneNumber("48336733004")
        .build();
  }

  public static CompleteRegistrationRequestDto getValidCompleteRegistrationRequestDto() {
    return CompleteRegistrationRequestDto.builder()
        .password("123456Te$t")
        .repeatedPassword("123456Te$t")
        .build();
  }

  public static ResetPasswordRequestDto getValidResetPasswordRequestDto() {
    return ResetPasswordRequestDto.builder().email("test01@gmail.com").build();
  }

  public static ResetPasswordRequestDto getNonExistingEmailResetPasswordRequestDto() {
    return ResetPasswordRequestDto.builder().email("non-existing@example.com").build();
  }

  public static ResetPasswordConfirmationRequestDto getValidResetPasswordConfirmationRequestDto() {
    return ResetPasswordConfirmationRequestDto.builder()
        .token("11111111-1111-1111-1111-111111111111")
        .newPassword("NewSecurePassword123@")
        .build();
  }

  public static ResetPasswordConfirmationRequestDto
      getInvalidTokenResetPasswordConfirmationRequestDto() {
    return ResetPasswordConfirmationRequestDto.builder()
        .token(UUID.randomUUID().toString())
        .newPassword("NewSecurePassword123@")
        .build();
  }

  public static ResetPasswordConfirmationRequestDto
      getExpiredTokenResetPasswordConfirmationRequestDto() {
    return ResetPasswordConfirmationRequestDto.builder()
        .token("00000000-0000-0000-0000-000000000000")
        .newPassword("NewSecurePassword123@")
        .build();
  }
}

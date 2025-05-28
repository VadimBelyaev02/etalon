package com.andersenlab.etalon.userservice.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.andersenlab.etalon.userservice.MockData;
import com.andersenlab.etalon.userservice.client.InfoServiceClient;
import com.andersenlab.etalon.userservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.userservice.dto.info.request.CreateConfirmationRequestDto;
import com.andersenlab.etalon.userservice.dto.info.response.ConfirmationResponseDto;
import com.andersenlab.etalon.userservice.dto.info.response.CreateConfirmationResponseDto;
import com.andersenlab.etalon.userservice.dto.user.request.AddressDto;
import com.andersenlab.etalon.userservice.dto.user.request.CompleteRegistrationRequestDto;
import com.andersenlab.etalon.userservice.dto.user.request.InitiateRegistrationRequestDto;
import com.andersenlab.etalon.userservice.dto.user.response.InitiateRegistrationResponseDto;
import com.andersenlab.etalon.userservice.dto.user.response.PersonalInfoDto;
import com.andersenlab.etalon.userservice.dto.user.response.UserDataResponseDto;
import com.andersenlab.etalon.userservice.entity.BankClient;
import com.andersenlab.etalon.userservice.entity.RegistrationOrder;
import com.andersenlab.etalon.userservice.entity.UserEntity;
import com.andersenlab.etalon.userservice.exception.BusinessException;
import com.andersenlab.etalon.userservice.exception.NotFoundException;
import com.andersenlab.etalon.userservice.exception.SecurityException;
import com.andersenlab.etalon.userservice.interceptor.AuthenticationHolder;
import com.andersenlab.etalon.userservice.mapper.AddressMapper;
import com.andersenlab.etalon.userservice.mapper.UserMapper;
import com.andersenlab.etalon.userservice.repository.BankClientRepository;
import com.andersenlab.etalon.userservice.repository.RegistrationOrderRepository;
import com.andersenlab.etalon.userservice.repository.UserRepository;
import com.andersenlab.etalon.userservice.service.CognitoService;
import com.andersenlab.etalon.userservice.service.RegistrationOrderService;
import com.andersenlab.etalon.userservice.service.impl.UserServiceImpl;
import com.andersenlab.etalon.userservice.util.ConfirmationMethod;
import com.andersenlab.etalon.userservice.util.ConfirmationStatus;
import com.andersenlab.etalon.userservice.util.Operation;
import com.andersenlab.etalon.userservice.util.OrderStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private RegistrationOrderService registrationOrderService;
  @Mock private BankClientRepository bankClientRepository;
  @Mock private RegistrationOrderRepository registrationOrderRepository;
  @Mock private CognitoService cognitoService;
  @Mock private AuthenticationHolder authenticationHolder;
  @Mock private AddressMapper addressMapper;
  @Mock private InfoServiceClient infoServiceClient;
  @Spy private UserMapper mapper = Mappers.getMapper(UserMapper.class);
  @InjectMocks private UserServiceImpl underTest;
  private UserEntity entity;
  private UserDataResponseDto userData;
  private AddressDto addressDto;
  private RegistrationOrder registrationOrder;
  private BankClient bankClient;
  private CompleteRegistrationRequestDto registrationRequestDto;
  private InitiateRegistrationRequestDto invalidRegistrationRequestDto;
  private InitiateRegistrationRequestDto validRegistrationRequestDto;
  private InitiateRegistrationRequestDto alreadyRegisteredRegistrationRequestDto;
  private CreateConfirmationRequestDto validConfirmationRequestDto;
  private CreateConfirmationRequestDto confirmationRequestWithoutTargetId;
  private static final String REGISTRATION_ID = "36c50b72-ada5-4407-b896-20649ba633a2";
  private static final Long TARGET_ID = 1L;

  @BeforeEach
  void setUp() {
    entity = MockData.getValidUserEntity();
    userData = MockData.getValidUserData();
    registrationOrder = MockData.getValidRegistrationOrder();
    bankClient = MockData.getValidBankClient();
    registrationRequestDto = MockData.getValidRegistrationRequestDto();
    addressDto = MockData.getValidAddressDto();
    invalidRegistrationRequestDto = MockData.getInvalidInitiateRegistrationRequestDto();
    validRegistrationRequestDto = MockData.getValidInitiateRegistrationRequestDto();
    alreadyRegisteredRegistrationRequestDto =
        MockData.getAlreadyRegisteredInitiateRegistrationRequestDto();
    validConfirmationRequestDto = MockData.getValidConfirmationForRegistration();
    confirmationRequestWithoutTargetId = MockData.getConfirmationWithoutTargetId();
  }

  @Test
  void testInitiateRegistration_CreatesNewOrder() {
    when(bankClientRepository.findByPesel(registrationOrder.getPesel()))
        .thenReturn(Optional.of(bankClient));
    when(cognitoService.userExists(bankClient.getEmail())).thenReturn(false);
    when(registrationOrderRepository.findByPesel(registrationOrder.getPesel()))
        .thenReturn(Optional.empty());

    when(registrationOrderRepository.save(any(RegistrationOrder.class)))
        .thenReturn(registrationOrder);
    when(infoServiceClient.createConfirmation(confirmationRequestWithoutTargetId))
        .thenReturn(new CreateConfirmationResponseDto(1L));

    InitiateRegistrationResponseDto response =
        underTest.initiateRegistration(validRegistrationRequestDto);

    verify(registrationOrderRepository, times(2)).save(any(RegistrationOrder.class));

    assertNotNull(response);
    assertEquals(1L, response.confirmationId());
    assertEquals(ConfirmationMethod.EMAIL, response.confirmationMethod());
    assertEquals("j****@gmail.com", response.maskedEmail());
    assertEquals(OrderStatus.PENDING_CONFIRMATION, response.orderStatus());
  }

  @Test
  void initiateRegistration_ShouldReturnPendingConfirmation_WhenOrderStatusPendingConfirmation() {
    registrationOrder.setOrderStatus(OrderStatus.PENDING_CONFIRMATION);
    when(bankClientRepository.findByPesel(registrationOrder.getPesel()))
        .thenReturn(Optional.of(bankClient));
    when(cognitoService.userExists(bankClient.getEmail())).thenReturn(false);
    when(registrationOrderRepository.findByPesel(registrationOrder.getPesel()))
        .thenReturn(Optional.of(registrationOrder));
    when(infoServiceClient.createConfirmation(validConfirmationRequestDto))
        .thenReturn(new CreateConfirmationResponseDto(TARGET_ID));

    InitiateRegistrationResponseDto response =
        underTest.initiateRegistration(validRegistrationRequestDto);

    assertEquals(OrderStatus.PENDING_CONFIRMATION, response.orderStatus());
    assertEquals("j****@gmail.com", response.maskedEmail());
    assertEquals(1L, response.confirmationId());
  }

  @Test
  void initiateRegistration_ShouldReturnConfirmed_WhenOrderStatusConfirmed() {
    registrationOrder.setOrderStatus(OrderStatus.CONFIRMED);
    ConfirmationResponseDto confirmationResponseDto =
        ConfirmationResponseDto.builder()
            .confirmationId(1L)
            .confirmationStatus(ConfirmationStatus.CONFIRMED)
            .build();
    when(infoServiceClient.getConfirmationsByOperationAndTargetId(
            Operation.USER_REGISTRATION, registrationOrder.getId()))
        .thenReturn(List.of(confirmationResponseDto));

    when(infoServiceClient.createConfirmation(validConfirmationRequestDto))
        .thenReturn(new CreateConfirmationResponseDto(TARGET_ID));
    when(bankClientRepository.findByPesel(registrationOrder.getPesel()))
        .thenReturn(Optional.of(bankClient));
    when(cognitoService.userExists(bankClient.getEmail())).thenReturn(false);
    when(registrationOrderRepository.findByPesel(registrationOrder.getPesel()))
        .thenReturn(Optional.of(registrationOrder));

    InitiateRegistrationResponseDto response =
        underTest.initiateRegistration(validRegistrationRequestDto);

    assertEquals(OrderStatus.CONFIRMED, response.orderStatus());
    assertEquals("j****@gmail.com", response.maskedEmail());
  }

  @Test
  void initiateRegistration_ShouldReturnCompleted_WhenOrderStatusCompleted() {
    registrationOrder.setOrderStatus(OrderStatus.COMPLETED);
    when(bankClientRepository.findByPesel(registrationOrder.getPesel()))
        .thenReturn(Optional.of(bankClient));
    when(cognitoService.userExists(bankClient.getEmail())).thenReturn(false);
    when(registrationOrderRepository.findByPesel(registrationOrder.getPesel()))
        .thenReturn(Optional.of(registrationOrder));

    InitiateRegistrationResponseDto response =
        underTest.initiateRegistration(validRegistrationRequestDto);

    assertEquals(OrderStatus.COMPLETED, response.orderStatus());
    assertEquals("j****@gmail.com", response.maskedEmail());
  }

  @Test
  void initiateRegistration_ShouldThrowNotFoundException_WhenBankClientNotFound() {

    when(bankClientRepository.findByPesel(invalidRegistrationRequestDto.pesel()))
        .thenReturn(Optional.empty());

    NotFoundException exception =
        assertThrows(
            NotFoundException.class,
            () -> underTest.initiateRegistration(invalidRegistrationRequestDto));

    assertEquals("Client with provided pesel not found", exception.getMessage());
  }

  @Test
  void initiateRegistration_ShouldThrowBusinessException_WhenUserExistsInCognito() {
    when(bankClientRepository.findByPesel(alreadyRegisteredRegistrationRequestDto.pesel()))
        .thenReturn(Optional.of(bankClient));
    when(cognitoService.userExists(bankClient.getEmail())).thenReturn(true);

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> underTest.initiateRegistration(alreadyRegisteredRegistrationRequestDto));

    assertEquals(HttpStatus.CONFLICT, exception.getHttpStatus());
    assertTrue(
        exception
            .getMessage()
            .contains("User with email-jackson@gmail.com is already registered in Cognito system"));
  }

  @Test
  void initiateRegistration_ShouldReturnPendingConfirmation_WhenOrderStatusCreated() {
    when(bankClientRepository.findByPesel(validRegistrationRequestDto.pesel()))
        .thenReturn(Optional.of(bankClient));
    when(cognitoService.userExists(bankClient.getEmail())).thenReturn(false);
    when(registrationOrderRepository.findByPesel(validRegistrationRequestDto.pesel()))
        .thenReturn(Optional.of(registrationOrder));
    when(infoServiceClient.createConfirmation(validConfirmationRequestDto))
        .thenReturn(new CreateConfirmationResponseDto(TARGET_ID));

    InitiateRegistrationResponseDto response =
        underTest.initiateRegistration(validRegistrationRequestDto);

    assertEquals(OrderStatus.PENDING_CONFIRMATION, response.orderStatus());
    assertEquals("j****@gmail.com", response.maskedEmail());
  }

  @Test
  void processUserRegistration_ShouldProcessSuccessfully() {
    when(registrationOrderService.getRegistrationOrderById(TARGET_ID))
        .thenReturn(registrationOrder);
    when(bankClientRepository.findByPesel("96070642313"))
        .thenReturn(Optional.ofNullable(bankClient));
    when(registrationOrderRepository.save(any(RegistrationOrder.class)))
        .thenAnswer(
            invocation -> {
              RegistrationOrder order = invocation.getArgument(0);
              order.setRegistrationId(REGISTRATION_ID);
              return order;
            });
    when(addressMapper.toAddressDto(bankClient.getAddress())).thenReturn(addressDto);

    PersonalInfoDto response = underTest.processUserRegistration(TARGET_ID);

    assertEquals(REGISTRATION_ID, response.registrationId());
    assertEquals("Michael", response.firstName());
    assertEquals("Jackson", response.lastName());
    assertEquals("jackson@gmail.com", response.email());
    verify(registrationOrderRepository, times(1)).save(registrationOrder);
  }

  @Test
  void registerUserInCognito_ShouldRegisterSuccessfully() {
    when(registrationOrderService.getRegistrationOrderByRegistrationId(REGISTRATION_ID))
        .thenReturn(registrationOrder);
    when(bankClientRepository.findByPesel("96070642313")).thenReturn(Optional.of(bankClient));
    when(cognitoService.registerUser(bankClient.getEmail(), registrationRequestDto.password()))
        .thenReturn(userData.id());

    MessageResponseDto response =
        underTest.registerUserInCognito(UUID.fromString(REGISTRATION_ID), registrationRequestDto);

    assertEquals("Registration was completed successfully", response.message());
    verify(userRepository, times(1)).save(any(UserEntity.class));
    verify(registrationOrderService, times(1)).updateRegistrationOrder(registrationOrder);
  }

  @Test
  void registerUserInCognito_ShouldThrowException_WhenBankClientNotFound() {
    when(registrationOrderService.getRegistrationOrderByRegistrationId(REGISTRATION_ID))
        .thenReturn(registrationOrder);
    when(bankClientRepository.findByPesel("96070642313")).thenReturn(Optional.empty());
    when(authenticationHolder.getUserId()).thenReturn("testId");

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () ->
                underTest.registerUserInCognito(
                    UUID.fromString(REGISTRATION_ID), registrationRequestDto));

    assertEquals(HttpStatus.CONFLICT, exception.getHttpStatus());
    assertTrue(exception.getMessage().contains("doesn't belongs to user with id-testId"));
  }

  @Test
  void whenGetUserDataById_shouldReturnUserDataDto() {
    when(registrationOrderService.getRegistrationOrderById(1L)).thenReturn(registrationOrder);
    when(bankClientRepository.findByPesel("96070642313"))
        .thenReturn(Optional.ofNullable(bankClient));

    UserDataResponseDto result = underTest.getUserDataById(1L);

    assertNotNull(result);
    assertEquals("jackson@gmail.com", result.email());

    verify(registrationOrderService).getRegistrationOrderById(1L);
    verify(bankClientRepository).findByPesel("96070642313");
  }

  @Test
  void whenValidRequestById_shouldReturnUserDataResponseDto() {
    // given
    given(userRepository.findById(anyString())).willReturn(Optional.ofNullable(entity));

    // when
    UserDataResponseDto result = underTest.getUserDataById(entity.getId());

    // then
    then(userRepository).should(times(1)).findById(entity.getId());
    assertFieldsEquals(userData, result);
  }

  @Test
  void whenUserByIdNotExist_shouldReturnSecurityException() {
    // given
    final String expected = "User with such credentials (" + entity.getId() + ") does not exist";
    // when
    final SecurityException exception =
        assertThrows(SecurityException.class, () -> underTest.getUserDataById(entity.getId()));

    // then
    assertEquals(expected, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    verify(userRepository, times(1)).findById(entity.getId());
  }

  @Test
  void whenValidRequestByPhoneNumber_shouldReturnValidUserDataResponseDto() {
    // given
    given(userRepository.findUserEntityByPhoneNumber(anyString()))
        .willReturn(Optional.ofNullable(entity));

    // when
    UserDataResponseDto result = underTest.getUserDataByPhoneNumber(entity.getPhoneNumber());

    // then
    then(userRepository).should(times(1)).findUserEntityByPhoneNumber(entity.getPhoneNumber());
    assertFieldsEquals(userData, result);
  }

  @Test
  void whenUserByPhoneNumberNotExist_shouldReturnSecurityException() {
    // given
    final String expected =
        "User with such credentials (" + entity.getPhoneNumber() + ") does not exist";

    // when
    final SecurityException exception =
        assertThrows(
            SecurityException.class,
            () -> underTest.getUserDataByPhoneNumber(entity.getPhoneNumber()));

    // then
    assertEquals(expected, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    verify(userRepository, times(1)).findUserEntityByPhoneNumber(entity.getPhoneNumber());
  }

  private void assertFieldsEquals(
      final UserDataResponseDto user, final UserDataResponseDto result) {
    assertEquals(user.id(), result.id());
    assertEquals(user.firstName(), result.firstName());
    assertEquals(user.lastName(), result.lastName());
    assertEquals(user.email(), result.email());
    assertEquals(user.pesel(), result.pesel());
  }
}

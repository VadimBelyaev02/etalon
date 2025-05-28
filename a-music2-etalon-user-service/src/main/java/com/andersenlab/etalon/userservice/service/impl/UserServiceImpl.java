package com.andersenlab.etalon.userservice.service.impl;

import static com.andersenlab.etalon.userservice.exception.NotFoundException.CLIENT_WITH_PROVIDED_PESEL_NOT_FOUND;
import static com.andersenlab.etalon.userservice.util.Constants.REGISTRATION_WAS_COMPLETED_SUCCESSFULLY;
import static com.andersenlab.etalon.userservice.util.OrderStatus.COMPLETED;
import static com.andersenlab.etalon.userservice.util.OrderStatus.PENDING_CONFIRMATION;

import com.andersenlab.etalon.userservice.client.InfoServiceClient;
import com.andersenlab.etalon.userservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.userservice.dto.info.request.CreateConfirmationRequestDto;
import com.andersenlab.etalon.userservice.dto.info.response.ConfirmationResponseDto;
import com.andersenlab.etalon.userservice.dto.info.response.CreateConfirmationResponseDto;
import com.andersenlab.etalon.userservice.dto.user.request.CompleteRegistrationRequestDto;
import com.andersenlab.etalon.userservice.dto.user.request.EmploymentDataDto;
import com.andersenlab.etalon.userservice.dto.user.request.InitiateRegistrationRequestDto;
import com.andersenlab.etalon.userservice.dto.user.request.UserPatchRequestDto;
import com.andersenlab.etalon.userservice.dto.user.response.InitiateRegistrationResponseDto;
import com.andersenlab.etalon.userservice.dto.user.response.PersonalInfoDto;
import com.andersenlab.etalon.userservice.dto.user.response.UserDataResponseDto;
import com.andersenlab.etalon.userservice.entity.BankClient;
import com.andersenlab.etalon.userservice.entity.EmploymentDataEntity;
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
import com.andersenlab.etalon.userservice.service.UserService;
import com.andersenlab.etalon.userservice.util.ConfirmationMethod;
import com.andersenlab.etalon.userservice.util.ConfirmationStatus;
import com.andersenlab.etalon.userservice.util.EmailFormatter;
import com.andersenlab.etalon.userservice.util.Operation;
import com.andersenlab.etalon.userservice.util.OrderStatus;
import com.andersenlab.etalon.userservice.util.PerformUtils;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final BankClientRepository bankClientRepository;
  private final RegistrationOrderRepository registrationOrderRepository;
  private final InfoServiceClient infoServiceClient;
  private final RegistrationOrderService registrationOrderService;
  private final AuthenticationHolder authenticationHolder;
  private final CognitoService cognitoService;
  private final AddressMapper addressMapper;

  @Override
  public UserDataResponseDto getUserDataById(final String userId) {

    return userMapper.toDto(
        userRepository
            .findById(userId)
            .orElseThrow(
                () ->
                    new SecurityException(
                        HttpStatus.BAD_REQUEST,
                        String.format(SecurityException.USER_DOESNT_EXIST, userId))));
  }

  @Override
  public UserDataResponseDto getUserDataByPhoneNumber(String phoneNumber) {
    return userMapper.toDto(
        userRepository
            .findUserEntityByPhoneNumber(phoneNumber)
            .orElseThrow(
                () ->
                    new SecurityException(
                        HttpStatus.BAD_REQUEST,
                        String.format(SecurityException.USER_DOESNT_EXIST, phoneNumber))));
  }

  @Override
  public InitiateRegistrationResponseDto initiateRegistration(InitiateRegistrationRequestDto dto) {

    BankClient bankClient =
        bankClientRepository
            .findByPesel(dto.pesel())
            .orElseThrow(() -> new NotFoundException(CLIENT_WITH_PROVIDED_PESEL_NOT_FOUND));

    if (cognitoService.userExists(bankClient.getEmail())) {
      throw new BusinessException(
          HttpStatus.CONFLICT,
          "User with email-%s is already registered in Cognito system"
              .formatted(bankClient.getEmail()));
    }

    Optional<RegistrationOrder> registrationOrderOpt =
        registrationOrderRepository.findByPesel(dto.pesel());
    if (registrationOrderOpt.isPresent()) {
      RegistrationOrder registrationOrder = registrationOrderOpt.get();
      switch (registrationOrder.getOrderStatus()) {
        case PENDING_CONFIRMATION -> {
          CreateConfirmationResponseDto confirmationResponse =
              resendConfirmation(registrationOrder);
          return InitiateRegistrationResponseDto.builder()
              .confirmationId(confirmationResponse.confirmationId())
              .confirmationMethod(ConfirmationMethod.EMAIL)
              .maskedEmail(EmailFormatter.maskEmail(bankClient.getEmail()))
              .orderStatus(PENDING_CONFIRMATION)
              .build();
        }
        case CREATED -> {
          CreateConfirmationResponseDto confirmationResponseDto =
              createConfirmation(registrationOrder);
          return InitiateRegistrationResponseDto.builder()
              .confirmationId(confirmationResponseDto.confirmationId())
              .confirmationMethod(ConfirmationMethod.EMAIL)
              .maskedEmail(EmailFormatter.maskEmail(bankClient.getEmail()))
              .orderStatus(PENDING_CONFIRMATION)
              .build();
        }
        case CONFIRMED -> {
          CreateConfirmationResponseDto confirmationResponseDto =
              resendConfirmation(registrationOrder);
          return InitiateRegistrationResponseDto.builder()
              .confirmationId(confirmationResponseDto.confirmationId())
              .confirmationMethod(ConfirmationMethod.EMAIL)
              .maskedEmail(EmailFormatter.maskEmail(bankClient.getEmail()))
              .orderStatus(OrderStatus.CONFIRMED)
              .build();
        }
        case COMPLETED -> {
          return InitiateRegistrationResponseDto.builder()
              .confirmationMethod(ConfirmationMethod.EMAIL)
              .maskedEmail(EmailFormatter.maskEmail(bankClient.getEmail()))
              .orderStatus(COMPLETED)
              .build();
        }
      }
    }

    RegistrationOrder registrationOrder = createRegistrationOrder(dto.pesel());
    CreateConfirmationResponseDto confirmationResponseDto = createConfirmation(registrationOrder);
    updateRegistrationOrder(registrationOrder, PENDING_CONFIRMATION);

    return InitiateRegistrationResponseDto.builder()
        .confirmationId(confirmationResponseDto.confirmationId())
        .confirmationMethod(ConfirmationMethod.EMAIL)
        .maskedEmail(EmailFormatter.maskEmail(bankClient.getEmail()))
        .orderStatus(PENDING_CONFIRMATION)
        .build();
  }

  private CreateConfirmationResponseDto resendConfirmation(RegistrationOrder registrationOrder) {
    return Optional.ofNullable(
            infoServiceClient.getConfirmationsByOperationAndTargetId(
                Operation.USER_REGISTRATION, registrationOrder.getId()))
        .stream()
        .flatMap(List::stream)
        .filter(this::isResendableStatus)
        .findFirst()
        .map(
            confirmation -> infoServiceClient.resendConfirmationCode(confirmation.confirmationId()))
        .orElseGet(() -> createConfirmation(registrationOrder));
  }

  private boolean isResendableStatus(ConfirmationResponseDto confirmation) {
    return !EnumSet.of(
            ConfirmationStatus.REJECTED, ConfirmationStatus.DELETED, ConfirmationStatus.CONFIRMED)
        .contains(confirmation.confirmationStatus());
  }

  private void updateRegistrationOrder(
      RegistrationOrder registrationOrder, OrderStatus orderStatus) {
    registrationOrder.setOrderStatus(orderStatus);
    registrationOrderRepository.save(registrationOrder);
    log.info(
        "Registration order with pesel={} was updated with {} status",
        registrationOrder.getPesel(),
        orderStatus);
  }

  private RegistrationOrder createRegistrationOrder(String pesel) {
    RegistrationOrder registrationOrder = new RegistrationOrder();
    registrationOrder.setPesel(pesel);
    registrationOrder.setOrderStatus(OrderStatus.CREATED);
    registrationOrderRepository.save(registrationOrder);
    log.info("Registration order with pesel={} was created successfully", pesel);
    return registrationOrder;
  }

  private CreateConfirmationResponseDto createConfirmation(RegistrationOrder registrationOrder) {
    CreateConfirmationRequestDto createConfirmationRequestDto =
        CreateConfirmationRequestDto.builder()
            .targetId(registrationOrder.getId())
            .operation(Operation.USER_REGISTRATION)
            .confirmationMethod(ConfirmationMethod.EMAIL)
            .isRegistration(true)
            .build();
    return infoServiceClient.createConfirmation(createConfirmationRequestDto);
  }

  @Override
  public UserDataResponseDto patchUserById(UserPatchRequestDto patchRequestDto, String userId) {
    UserEntity user =
        userRepository
            .findById(userId)
            .orElseThrow(
                () ->
                    new NotFoundException(String.format(NotFoundException.USER_NOT_FOUND, userId)));

    if (Objects.isNull(user.getEmploymentData())) {
      user.setEmploymentData(new EmploymentDataEntity());
    }

    PerformUtils.performIfPresent(
        employmentData -> performEmploymentData(employmentData, user.getEmploymentData()),
        patchRequestDto.employmentData());

    return userMapper.toDto(userRepository.save(user));
  }

  private void performEmploymentData(
      final EmploymentDataDto employmentDataDto, final EmploymentDataEntity employmentDataEntity) {
    PerformUtils.performIfPresent(employmentDataEntity::setPosition, employmentDataDto.position());
    PerformUtils.performIfPresent(
        employmentDataEntity::setPlaceOfWork, employmentDataDto.placeOfWork());
  }

  @Override
  public UserEntity getUserById(String userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(
            () -> new NotFoundException(String.format(NotFoundException.USER_NOT_FOUND, userId)));
  }

  @Override
  public UserEntity updateUser(UserEntity user) {
    getUserById(user.getId());
    return userRepository.save(user);
  }

  @Override
  public boolean existsByEmail(final String email) {
    return userRepository.existsByEmail(email);
  }

  @Override
  public MessageResponseDto registerUserInCognito(
      UUID registrationId, CompleteRegistrationRequestDto registrationDto) {
    RegistrationOrder registrationOrder =
        registrationOrderService.getRegistrationOrderByRegistrationId(
            String.valueOf(registrationId));
    Optional<BankClient> bankClientOptional =
        bankClientRepository.findByPesel(registrationOrder.getPesel());
    if (bankClientOptional.isEmpty()) {
      throw new BusinessException(
          HttpStatus.CONFLICT,
          "Provided registration id-%s doesn't belongs to user with id-%s"
              .formatted(registrationId, authenticationHolder.getUserId()));
    }
    BankClient bankClient = bankClientOptional.get();
    String userId = cognitoService.registerUser(bankClient.getEmail(), registrationDto.password());
    userRepository.save(
        UserEntity.builder()
            .id(userId)
            .firstName(bankClient.getFirstName())
            .lastName(bankClient.getLastName())
            .pesel(bankClient.getPesel())
            .email(bankClient.getEmail())
            .phoneNumber(bankClient.getPhoneNumber())
            .address(bankClient.getAddress())
            .build());
    registrationOrder.setOrderStatus(COMPLETED);
    registrationOrderService.updateRegistrationOrder(registrationOrder);
    return new MessageResponseDto(REGISTRATION_WAS_COMPLETED_SUCCESSFULLY);
  }

  @Override
  public UserDataResponseDto getUserDataById(Long targetId) {
    RegistrationOrder registrationOrder =
        registrationOrderService.getRegistrationOrderById(targetId);
    BankClient bankClient = getBankClientByPesel(registrationOrder.getPesel());
    return UserDataResponseDto.builder().email(bankClient.getEmail()).build();
  }

  @Override
  public PersonalInfoDto processUserRegistration(Long targetId) {
    RegistrationOrder registrationOrder =
        registrationOrderService.getRegistrationOrderById(targetId);
    BankClient bankClient = getBankClientByPesel(registrationOrder.getPesel());
    registrationOrder.setRegistrationId(UUID.randomUUID().toString());
    registrationOrder.setOrderStatus(OrderStatus.CONFIRMED);
    RegistrationOrder savedRegistrationOrder = registrationOrderRepository.save(registrationOrder);
    return PersonalInfoDto.builder()
        .registrationId(savedRegistrationOrder.getRegistrationId())
        .firstName(bankClient.getFirstName())
        .lastName(bankClient.getLastName())
        .email(bankClient.getEmail())
        .addressDto(addressMapper.toAddressDto(bankClient.getAddress()))
        .build();
  }

  private BankClient getBankClientByPesel(String pesel) {
    return bankClientRepository
        .findByPesel(pesel)
        .orElseThrow(
            () ->
                new BusinessException(
                    HttpStatus.NOT_FOUND, "No user found by pesel-%s".formatted(pesel)));
  }
}

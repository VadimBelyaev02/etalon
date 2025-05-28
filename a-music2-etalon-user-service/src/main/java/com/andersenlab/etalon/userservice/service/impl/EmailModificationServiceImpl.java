package com.andersenlab.etalon.userservice.service.impl;

import static com.andersenlab.etalon.userservice.exception.NotFoundException.AN_EMAIL_MODIFICATION_RECORD_NOT_FOUND;

import com.andersenlab.etalon.userservice.client.InfoServiceClient;
import com.andersenlab.etalon.userservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.userservice.dto.info.request.CreateConfirmationRequestDto;
import com.andersenlab.etalon.userservice.dto.info.response.ConfirmationResponseDto;
import com.andersenlab.etalon.userservice.dto.info.response.CreateConfirmationResponseDto;
import com.andersenlab.etalon.userservice.dto.modification.request.UserEmailModificationRequestDto;
import com.andersenlab.etalon.userservice.dto.modification.responce.EmailModificationInfoResponseDto;
import com.andersenlab.etalon.userservice.dto.modification.responce.EmailModificationResponseDto;
import com.andersenlab.etalon.userservice.entity.EmailModificationEntity;
import com.andersenlab.etalon.userservice.entity.UserEntity;
import com.andersenlab.etalon.userservice.exception.BusinessException;
import com.andersenlab.etalon.userservice.exception.NotFoundException;
import com.andersenlab.etalon.userservice.interceptor.AuthenticationHolder;
import com.andersenlab.etalon.userservice.repository.EmailModificationRepository;
import com.andersenlab.etalon.userservice.service.CognitoService;
import com.andersenlab.etalon.userservice.service.EmailModificationService;
import com.andersenlab.etalon.userservice.service.UserService;
import com.andersenlab.etalon.userservice.util.ConfirmationMethod;
import com.andersenlab.etalon.userservice.util.ConfirmationStatus;
import com.andersenlab.etalon.userservice.util.EmailModificationStatus;
import com.andersenlab.etalon.userservice.util.Operation;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class EmailModificationServiceImpl implements EmailModificationService {
  public static final String AN_EMAIL_HAS_BEEN_UPDATED =
      "An email has been updated for user with id %s";
  private final EmailModificationRepository emailModificationRepository;
  private final AuthenticationHolder authenticationHolder;
  private final InfoServiceClient infoServiceClient;
  private final UserService userService;
  private final CognitoService cognitoService;

  public EmailModificationResponseDto requestEmailModification(
      UserEmailModificationRequestDto userEmailModificationRequestDto) {

    UserEntity user = userService.getUserById(authenticationHolder.getUserId());
    isEmailAvailableForModification(userEmailModificationRequestDto.email(), user);

    Optional<CreateConfirmationResponseDto> confirmationResponseDtoForResend =
        checkEmailModificationForResending(userEmailModificationRequestDto.email());
    if (confirmationResponseDtoForResend.isPresent()) {
      return EmailModificationResponseDto.builder()
          .confirmationId(confirmationResponseDtoForResend.get().confirmationId())
          .build();
    }

    EmailModificationEntity emailModificationEntity =
        emailModificationRepository.save(
            EmailModificationEntity.builder()
                .newEmail(userEmailModificationRequestDto.email())
                .userId(authenticationHolder.getUserId())
                .build());

    CreateConfirmationRequestDto createConfirmationRequestDto =
        CreateConfirmationRequestDto.builder()
            .targetId(emailModificationEntity.getId())
            .operation(Operation.EMAIL_MODIFICATION)
            .confirmationMethod(ConfirmationMethod.EMAIL)
            .build();

    CreateConfirmationResponseDto confirmationResponseDto;
    try {
      confirmationResponseDto = infoServiceClient.createConfirmation(createConfirmationRequestDto);
    } catch (Exception e) {
      throw new BusinessException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
    emailModificationEntity.setStatus(EmailModificationStatus.CREATED);
    emailModificationRepository.save(emailModificationEntity);
    return EmailModificationResponseDto.builder()
        .confirmationId(confirmationResponseDto.confirmationId())
        .build();
  }

  private Optional<CreateConfirmationResponseDto> checkEmailModificationForResending(String email) {
    return emailModificationRepository
        .findEmailModificationEntitiesByUserIdAndNewEmail(authenticationHolder.getUserId(), email)
        .stream()
        .flatMap(List::stream)
        .sorted(Comparator.comparing(EmailModificationEntity::getId).reversed())
        .filter(e -> e.getStatus() == EmailModificationStatus.CREATED)
        .map(
            e ->
                checkStatusOfConfirmationByTargetIdAndResend(
                    Operation.EMAIL_MODIFICATION, e.getId()))
        .flatMap(Optional::stream)
        .findFirst();
  }

  private Optional<CreateConfirmationResponseDto> checkStatusOfConfirmationByTargetIdAndResend(
      Operation operation, Long targetId) {
    return Optional.ofNullable(
            infoServiceClient.getConfirmationsByOperationAndTargetId(operation, targetId))
        .stream()
        .flatMap(List::stream)
        .filter(this::isResendableStatus)
        .findFirst()
        .map(
            confirmation ->
                infoServiceClient.resendConfirmationCode(confirmation.confirmationId()));
  }

  private boolean isResendableStatus(ConfirmationResponseDto confirmation) {
    return !EnumSet.of(
            ConfirmationStatus.REJECTED, ConfirmationStatus.DELETED, ConfirmationStatus.CONFIRMED)
        .contains(confirmation.confirmationStatus());
  }

  private void isEmailAvailableForModification(String providedEmail, UserEntity currentUser) {
    if (currentUser.getEmail().equals(providedEmail)) {
      throw new BusinessException(
          HttpStatus.CONFLICT, BusinessException.EMAIL_CAN_NOT_MATCH_WITH_CURRENT_ONE);
    }
    if (userService.existsByEmail(providedEmail)) {
      throw new BusinessException(
          HttpStatus.CONFLICT, BusinessException.EMAIL_IS_ALREADY_REGISTERED);
    }
  }

  @Override
  public MessageResponseDto processEmailModification(long modificationId) {
    EmailModificationEntity emailModificationEntityFromDb =
        emailModificationRepository
            .findById(modificationId)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        String.format(AN_EMAIL_MODIFICATION_RECORD_NOT_FOUND, modificationId)));
    UserEntity user = userService.getUserById(authenticationHolder.getUserId());
    user.setEmail(emailModificationEntityFromDb.getNewEmail());
    userService.updateUser(user);
    cognitoService.updateUserEmail(user.getId(), emailModificationEntityFromDb.getNewEmail());
    log.info(
        "{confirmEmailModification} request sent to AWS Cognito for email change: userId-%s; new email-%s"
            .formatted(user.getId(), emailModificationEntityFromDb.getNewEmail()));
    emailModificationEntityFromDb.setStatus(EmailModificationStatus.COMPLETED);
    emailModificationRepository.save(emailModificationEntityFromDb);
    return new MessageResponseDto(
        String.format(AN_EMAIL_HAS_BEEN_UPDATED, authenticationHolder.getUserId()));
  }

  @Override
  public EmailModificationInfoResponseDto getEmailModificationInfo(long modificationId) {
    EmailModificationEntity emailModificationEntity =
        emailModificationRepository
            .findById(modificationId)
            .orElseThrow(
                () ->
                    new BusinessException(
                        HttpStatus.NOT_FOUND,
                        "Modification entity not found by id %d".formatted(modificationId)));
    return EmailModificationInfoResponseDto.builder()
        .userId(emailModificationEntity.getUserId())
        .newEmail(emailModificationEntity.getNewEmail())
        .build();
  }
}

package com.andersenlab.etalon.infoservice.service.impl;

import static com.andersenlab.etalon.infoservice.exception.BusinessException.CONFIRMATION_REQUEST_NOT_FOUND_BY_ID;

import com.andersenlab.etalon.infoservice.dto.request.ConfirmationRequestDto;
import com.andersenlab.etalon.infoservice.dto.request.CreateConfirmationRequestDto;
import com.andersenlab.etalon.infoservice.dto.response.ConfirmationResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.CreateConfirmationResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.MessageResponseDto;
import com.andersenlab.etalon.infoservice.entity.ConfirmationEntity;
import com.andersenlab.etalon.infoservice.entity.ValidationData;
import com.andersenlab.etalon.infoservice.exception.BusinessException;
import com.andersenlab.etalon.infoservice.repository.ConfirmationRepository;
import com.andersenlab.etalon.infoservice.service.ConfirmationPostProcessorService;
import com.andersenlab.etalon.infoservice.service.ConfirmationService;
import com.andersenlab.etalon.infoservice.service.ConfirmationStrategyService;
import com.andersenlab.etalon.infoservice.service.EmailService;
import com.andersenlab.etalon.infoservice.service.TransactionStatusUpdateService;
import com.andersenlab.etalon.infoservice.service.chain.validation.ConfirmationValidationChain;
import com.andersenlab.etalon.infoservice.service.chain.validation.CreateConfirmationValidationChain;
import com.andersenlab.etalon.infoservice.service.chain.validation.ResendConfirmationValidationChain;
import com.andersenlab.etalon.infoservice.service.strategy.emailrequestpreparation.EmailRequestPreparationContext;
import com.andersenlab.etalon.infoservice.util.ConfirmationCodeGenerator;
import com.andersenlab.etalon.infoservice.util.enums.ConfirmationStatus;
import com.andersenlab.etalon.infoservice.util.enums.Operation;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConfirmationServiceImpl implements ConfirmationService {
  private static final Integer INVALID_CONFIRMATION_CODE = -1;

  private final ConfirmationPostProcessorService confirmationPostProcessorService;
  private final ConfirmationStrategyService confirmationStrategyService;
  private final ConfirmationRepository confirmationRepository;
  private final TransactionStatusUpdateService transactionStatusUpdateService;
  private final EmailService emailService;
  private final EmailRequestPreparationContext emailRequestPreparationContext;
  private final ConfirmationValidationChain confirmationValidationChain;
  private final ResendConfirmationValidationChain resendConfirmationValidationChain;
  private final CreateConfirmationValidationChain createConfirmationValidationChain;

  @Override
  public CreateConfirmationResponseDto createConfirmation(CreateConfirmationRequestDto dto) {
    log.info(
        "{ createConfirmation} create confirmation for target with id-%d received"
            .formatted(dto.targetId()));

    ValidationData validationData =
        ValidationData.builder()
            .confirmationEntity(
                ConfirmationEntity.builder()
                    .operation(dto.operation())
                    .targetId(dto.targetId())
                    .build())
            .build();
    createConfirmationValidationChain.validate(validationData);

    ConfirmationEntity newConfirmation =
        ConfirmationEntity.builder()
            .confirmationCode(INVALID_CONFIRMATION_CODE)
            .confirmationMethod(dto.confirmationMethod())
            .operation(dto.operation())
            .targetId(dto.targetId())
            .status(ConfirmationStatus.NOT_PROCESSED)
            .build();

    ConfirmationEntity savedConfirmation = confirmationRepository.save(newConfirmation);
    confirmationPostProcessorService.postProcess(savedConfirmation);
    return new CreateConfirmationResponseDto(savedConfirmation.getId(), dto.confirmationMethod());
  }

  @Override
  public List<ConfirmationResponseDto> getConfirmationsByOperationAndTargetId(
      Operation operation, Long targetId) {
    log.info(
        "{getConfirmationsByOperationAndTargetId} get confirmation for target with id-%s internal request"
            .formatted(targetId));
    return confirmationRepository.findByTargetIdAndOperation(targetId, operation).stream()
        .map(
            confirmationEntity ->
                ConfirmationResponseDto.builder()
                    .confirmationId(confirmationEntity.getId())
                    .confirmationStatus(confirmationEntity.getStatus())
                    .build())
        .collect(Collectors.toList());
  }

  @Override
  public void processConfirmation(Long confirmationId) {
    ConfirmationEntity confirmation = getConfirmationFromRepository(confirmationId);
    confirmation.setConfirmationCode(
        Integer.valueOf(ConfirmationCodeGenerator.generateConfirmationCode()));
    confirmation.setStatus(ConfirmationStatus.CREATED);
    ConfirmationEntity updatedConfirmation = confirmationRepository.save(confirmation);
    emailService.sendEmail(emailRequestPreparationContext.prepareEmail(updatedConfirmation));
  }

  @Override
  public CreateConfirmationResponseDto resendConfirmationCode(Long confirmationId) {
    log.info(
        "{resendConfirmationCode} confirmation code resend requested for confirmation with id-%d"
            .formatted(confirmationId));
    ConfirmationEntity oldConfirmation = getConfirmationFromRepository(confirmationId);

    resendConfirmationValidationChain.validate(
        ValidationData.builder().confirmationEntity(oldConfirmation).build());

    ConfirmationEntity newConfirmation =
        ConfirmationEntity.builder()
            .confirmationCode(Integer.valueOf(ConfirmationCodeGenerator.generateConfirmationCode()))
            .confirmationMethod(oldConfirmation.getConfirmationMethod())
            .operation(oldConfirmation.getOperation())
            .targetId(oldConfirmation.getTargetId())
            .status(ConfirmationStatus.CREATED)
            .build();
    emailService.sendEmail(emailRequestPreparationContext.prepareEmail(newConfirmation));

    ConfirmationEntity updatedConfirmation = confirmationRepository.save(newConfirmation);
    oldConfirmation.setStatus(ConfirmationStatus.DELETED);
    confirmationRepository.save(oldConfirmation);
    return new CreateConfirmationResponseDto(
        updatedConfirmation.getId(), updatedConfirmation.getConfirmationMethod());
  }

  @Override
  public MessageResponseDto verifyConfirmationCode(
      final Long confirmationId, final ConfirmationRequestDto dto) {
    ConfirmationEntity confirmation = getConfirmationFromRepository(confirmationId);

    confirmationValidationChain.validate(
        ValidationData.builder()
            .confirmationEntity(confirmation)
            .confirmationCode(Integer.parseInt(dto.confirmationCode()))
            .build());

    transactionStatusUpdateService.updateStatusToCodeConfirmed(confirmation);

    MessageResponseDto messageResponseDto = confirmationStrategyService.confirm(confirmation);
    confirmation.setStatus(ConfirmationStatus.CONFIRMED);
    confirmationRepository.save(confirmation);
    return messageResponseDto;
  }

  @Override
  public void rejectConfirmation(Long confirmationId) {
    ConfirmationEntity confirmation = getConfirmationFromRepository(confirmationId);
    setConfirmationStatusAndSave(confirmation, ConfirmationStatus.REJECTED);
  }

  private void setConfirmationStatusAndSave(
      ConfirmationEntity confirmation, ConfirmationStatus status) {
    log.info("Confirmation status changed to -> {}", status);
    confirmation.setStatus(status);
    confirmationRepository.save(confirmation);
  }

  private ConfirmationEntity getConfirmationFromRepository(Long confirmationId) {
    return confirmationRepository
        .findById(confirmationId)
        .orElseThrow(
            () ->
                new BusinessException(
                    HttpStatus.NOT_FOUND,
                    String.format(CONFIRMATION_REQUEST_NOT_FOUND_BY_ID, confirmationId)));
  }
}

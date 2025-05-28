package com.andersenlab.etalon.userservice.controller.internal;

import com.andersenlab.etalon.userservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.userservice.dto.modification.responce.EmailModificationInfoResponseDto;
import com.andersenlab.etalon.userservice.service.EmailModificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping(EmailModificationInternalController.URI)
@Slf4j
public class EmailModificationInternalController {
  public static final String URI = "/internal/api/v1";
  public static final String EMAIL_MODIFICATION_URI = "/email-modification";
  public static final String EMAIL_MODIFICATION_ID_PATH = "/{modificationId}";
  public static final String TARGET_ID_PATH = "/{targetId}";
  public static final String INTERNAL_API_EMAIL_MODIFICATION_URI =
      EMAIL_MODIFICATION_URI + TARGET_ID_PATH;
  public static final String INTERNAL_API_EMAIL_MODIFICATION_INFO_URI =
      EMAIL_MODIFICATION_URI + EMAIL_MODIFICATION_ID_PATH;

  private final EmailModificationService emailModificationService;

  @GetMapping(INTERNAL_API_EMAIL_MODIFICATION_INFO_URI)
  EmailModificationInfoResponseDto getEmailModificationInfo(@PathVariable long modificationId) {
    log.info(
        "{getEmailModificationInfo} get email modification with id-%d internal request"
            .formatted(modificationId));
    return emailModificationService.getEmailModificationInfo(modificationId);
  }

  @PostMapping(INTERNAL_API_EMAIL_MODIFICATION_URI)
  MessageResponseDto processEmailModification(@PathVariable long targetId) {
    log.info(
        "{processEmailModification} process Email modification with id-%d internal request"
            .formatted(targetId));
    return emailModificationService.processEmailModification(targetId);
  }
}

package com.andersenlab.etalon.infoservice.controller.internal;

import static com.andersenlab.etalon.infoservice.controller.ConfirmationController.CONFIRMATIONS_URI;

import com.andersenlab.etalon.infoservice.dto.response.ConfirmationResponseDto;
import com.andersenlab.etalon.infoservice.service.ConfirmationService;
import com.andersenlab.etalon.infoservice.util.enums.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping(ConfirmationInternalController.URI)
@Tag(name = "Confirmation Internal")
public class ConfirmationInternalController {
  public static final String URI = "/internal/api/v1";
  private final ConfirmationService confirmationService;
  public static final String OPERATION_URI = "/{operation}";
  public static final String TARGET_ID_URI = "/{targetId}";
  public static final String INTERNAL_GET_LAST_CONFIRMATIONS_BY_OPERATION_AND_TARGET_ID_URI =
      CONFIRMATIONS_URI + OPERATION_URI + TARGET_ID_URI;

  @GetMapping(INTERNAL_GET_LAST_CONFIRMATIONS_BY_OPERATION_AND_TARGET_ID_URI)
  List<ConfirmationResponseDto> getConfirmationsByOperationAndTargetId(
      @PathVariable final Operation operation, @PathVariable final Long targetId) {
    return confirmationService.getConfirmationsByOperationAndTargetId(operation, targetId);
  }
}

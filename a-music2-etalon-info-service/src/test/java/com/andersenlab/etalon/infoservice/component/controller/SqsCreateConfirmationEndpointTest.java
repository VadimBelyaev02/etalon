package com.andersenlab.etalon.infoservice.component.controller;

import static com.andersenlab.etalon.infoservice.controller.ConfirmationController.CONFIRMATIONS_URL;
import static com.andersenlab.etalon.infoservice.util.Constants.AUTHENTICATED_USER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.context.jdbc.SqlMergeMode.MergeMode.MERGE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.infoservice.MockData;
import com.andersenlab.etalon.infoservice.annotation.WithUserId;
import com.andersenlab.etalon.infoservice.annotation.WithUserIdExtension;
import com.andersenlab.etalon.infoservice.component.AbstractComponentTest;
import com.andersenlab.etalon.infoservice.component.config.SqsInitializer;
import com.andersenlab.etalon.infoservice.dto.request.CreateConfirmationRequestDto;
import com.andersenlab.etalon.infoservice.dto.sqs.CreateConfirmationMessage;
import com.andersenlab.etalon.infoservice.entity.ConfirmationEntity;
import com.andersenlab.etalon.infoservice.repository.ConfirmationRepository;
import com.andersenlab.etalon.infoservice.util.enums.ConfirmationMethod;
import com.andersenlab.etalon.infoservice.util.enums.ConfirmationStatus;
import com.andersenlab.etalon.infoservice.util.enums.Operation;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Sql(scripts = "file:src/test/resources/data/clean-up-data.sql")
@SqlMergeMode(MERGE)
class SqsCreateConfirmationEndpointTest extends AbstractComponentTest {
  @Autowired QueueMessagingTemplate queueMessagingTemplate;
  @Autowired private ConfirmationRepository confirmationRepository;

  @Test
  @WithUserId
  @Sql(
      scripts = "file:src/test/resources/data/clean-up-data.sql",
      executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
  @ExtendWith(WithUserIdExtension.class)
  void whenCreateConfirmationAndProduceMessage_shouldSuccess() throws Exception {
    CreateConfirmationRequestDto confirmationRequestDto =
        MockData.getValidConfirmationRequestRequestDto();

    mockMvc
        .perform(
            post(CONFIRMATIONS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(confirmationRequestDto)))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$.confirmationId", is(1)))
        .andExpect(jsonPath("$.confirmationMethod", is(ConfirmationMethod.EMAIL.name())));

    await()
        .atMost(Duration.ofSeconds(5))
        .with()
        .pollDelay(Duration.ofSeconds(1))
        .untilAsserted(
            () -> {
              List<ConfirmationEntity> confirmations = confirmationRepository.findAll();
              ConfirmationEntity confirmationEntity = confirmations.get(0);
              assertThat(confirmations.size()).isEqualTo(1);
              assertThat(confirmationEntity.getStatus()).isEqualTo(ConfirmationStatus.CREATED);
              assertThat(confirmationEntity.getOperation()).isEqualTo(Operation.OPEN_DEPOSIT);
              assertThat(confirmationEntity.getId()).isEqualTo(1);
            });
  }

  @Test
  @Sql(scripts = "file:src/test/resources/data/confirmations-initial-data.sql")
  @Sql(
      scripts = "file:src/test/resources/data/clean-up-data.sql",
      executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
  @WithUserId
  void whenCreateConfirmationErrorOccurred_shouldDlqLogicProcessed() {
    CreateConfirmationMessage message =
        CreateConfirmationMessage.builder().confirmationId(7).build();
    queueMessagingTemplate.convertAndSend(
        SqsInitializer.createConfirmationQueue, message, Map.of(AUTHENTICATED_USER_ID, -1L));
    await()
        .atMost(Duration.ofSeconds(120))
        .with()
        .pollDelay(Duration.ofMillis(100L))
        .untilAsserted(
            () -> {
              ConfirmationEntity confirmation =
                  confirmationRepository.findById(message.confirmationId()).get();
              assertThat(confirmation.getStatus())
                  .isEqualByComparingTo(ConfirmationStatus.REJECTED);
            });
  }
}

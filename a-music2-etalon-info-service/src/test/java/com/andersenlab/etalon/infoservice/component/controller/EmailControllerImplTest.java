package com.andersenlab.etalon.infoservice.component.controller;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import com.andersenlab.etalon.infoservice.component.AbstractComponentTest;
import com.andersenlab.etalon.infoservice.config.SqsTestConfig;
import com.andersenlab.etalon.infoservice.controller.EmailController;
import com.andersenlab.etalon.infoservice.dto.request.BaseEmailRequestDto;
import com.andersenlab.etalon.infoservice.dto.request.ConfirmationEmailRequestDto;
import com.andersenlab.etalon.infoservice.sqs.SqsMessageProducer;
import com.andersenlab.etalon.infoservice.util.enums.EmailType;
import java.time.Duration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

@Import(SqsTestConfig.class)
class EmailControllerImplTest extends AbstractComponentTest {

  public static final String TO_EMAIL = "user@example.com";
  public static final String SUBJECT = "Test";
  public static final EmailType TYPE = EmailType.CONFIRMATION;
  public static final String VERIFICATION_CODE = "TEST";

  @Autowired private SqsMessageProducer sqsMessageProducer;

  @Test
  void whenSendEmail_shouldSendToSqsQueue() throws Exception {
    // Arrange
    String requestJson =
        String.format(
            """
                {
                  "toEmail": "%s",
                  "subject": "%s",
                  "type": "%s",
                  "verificationCode": "%s"
                }
                """,
            TO_EMAIL, SUBJECT, TYPE, VERIFICATION_CODE);

    // Act
    mockMvc
        .perform(
            post(EmailController.API_V1_URI + EmailController.SEND_EMAIL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
        .andExpect(status().isAccepted());

    ArgumentCaptor<String> queueCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<BaseEmailRequestDto> payloadCaptor =
        ArgumentCaptor.forClass(BaseEmailRequestDto.class);

    await()
        .atMost(Duration.ofSeconds(5))
        .untilAsserted(
            () ->
                verify(sqsMessageProducer, times(1))
                    .sendMessage(queueCaptor.capture(), payloadCaptor.capture()));

    // Verify
    ConfirmationEmailRequestDto confirmationEmail =
        assertInstanceOf(
            ConfirmationEmailRequestDto.class,
            payloadCaptor.getValue(),
            "Payload should be an instance of ConfirmationEmailRequestDto");

    Assertions.assertEquals(TO_EMAIL, confirmationEmail.getToEmail());
    Assertions.assertEquals(SUBJECT, confirmationEmail.getSubject());
    Assertions.assertEquals(TYPE, confirmationEmail.getType());
    Assertions.assertEquals(VERIFICATION_CODE, confirmationEmail.getVerificationCode());
  }
}

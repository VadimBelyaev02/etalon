package com.andersenlab.etalon.infoservice.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.andersenlab.etalon.infoservice.MockData;
import com.andersenlab.etalon.infoservice.dto.request.BaseEmailRequestDto;
import com.andersenlab.etalon.infoservice.dto.request.ConfirmationEmailRequestDto;
import com.andersenlab.etalon.infoservice.service.EmailPostProcessorService;
import com.andersenlab.etalon.infoservice.service.EmailStrategyService;
import com.andersenlab.etalon.infoservice.service.impl.EmailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.SendRawEmailRequest;
import software.amazon.awssdk.services.ses.model.SendRawEmailResponse;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

  @InjectMocks EmailServiceImpl emailService;
  @Mock SesClient sesClient;
  @Mock EmailStrategyService emailStrategyService;

  @Mock EmailPostProcessorService emailPostProcessorService;

  private ConfirmationEmailRequestDto emailRequest;
  private SendRawEmailRequest expectedMessage;
  private SendRawEmailResponse sendEmailResponse;
  private BaseEmailRequestDto emailRequestDto;

  @BeforeEach
  void setUp() {
    emailRequest = MockData.createEmailRequest();
    expectedMessage =
        SendRawEmailRequest.builder()
            .rawMessage(r -> r.data(SdkBytes.fromByteArray(new byte[0])))
            .build();
    sendEmailResponse = SendRawEmailResponse.builder().messageId("12345").build();
  }

  @Test
  void testSendEmail() {
    when(emailStrategyService.buildMessage(emailRequest)).thenReturn(expectedMessage);
    when(sesClient.sendRawEmail(any(SendRawEmailRequest.class))).thenReturn(sendEmailResponse);
    emailService.processSendEmail(emailRequest);

    verify(emailStrategyService, times(1)).buildMessage(emailRequest);
    verify(sesClient, times(1)).sendRawEmail(expectedMessage);
    assertNotNull(sendEmailResponse, "SendEmailResponse should not be null");
    assertEquals("12345", sendEmailResponse.messageId(), "Message ID should match expected value");
  }

  @Test
  void testEmailPostProcessing() {
    emailService.sendEmail(emailRequestDto);
    verify(emailPostProcessorService, times(1)).postProcess(emailRequestDto);
  }
}

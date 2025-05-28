package com.andersenlab.etalon.infoservice.unit;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.andersenlab.etalon.infoservice.MockData;
import com.andersenlab.etalon.infoservice.dto.request.BaseEmailRequestDto;
import com.andersenlab.etalon.infoservice.dto.sqs.CreateConfirmationMessage;
import com.andersenlab.etalon.infoservice.interceptor.AuthenticationHolder;
import com.andersenlab.etalon.infoservice.service.ConfirmationService;
import com.andersenlab.etalon.infoservice.service.EmailService;
import com.andersenlab.etalon.infoservice.sqs.SqsMessageListener;
import com.andersenlab.etalon.infoservice.util.Constants;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class SqsMessageListenerTest {

  @InjectMocks SqsMessageListener sqsMessageListener;

  @Mock ConfirmationService confirmationService;

  @Mock EmailService emailService;

  @Mock AuthenticationHolder authenticationHolder;

  @Test
  void consumeCreateConfirmationTest() {
    // given
    CreateConfirmationMessage createConfirmationMessage = MockData.createConfirmationMessage();
    Map<String, String> headers = MockData.createConfirmationHeaders();
    String userId = headers.get(Constants.AUTHENTICATED_USER_ID);
    Long confirmationId = createConfirmationMessage.confirmationId();

    // when
    sqsMessageListener.consumeCreateConfirmation(createConfirmationMessage, headers);

    // then
    verify(authenticationHolder, times(1)).setUserId(userId);
    verify(confirmationService, times(1)).processConfirmation(confirmationId);
  }

  @Test
  void consumeCreateConfirmationFromDlqTest() {
    // given
    CreateConfirmationMessage createConfirmationMessage = MockData.createConfirmationMessage();
    Long confirmationId = createConfirmationMessage.confirmationId();

    // when
    sqsMessageListener.consumeCreateConfirmationFromDlq(createConfirmationMessage);

    // then
    verify(confirmationService, times(1)).rejectConfirmation(confirmationId);
  }

  @Test
  void consumeSendEmailTest() {
    // given
    BaseEmailRequestDto baseEmailRequestDto = MockData.baseEmailRequestDto();
    Map<String, String> headers = MockData.createConfirmationHeaders();
    // when
    sqsMessageListener.consumeSendEmail(baseEmailRequestDto, headers);

    // then
    verify(emailService, times(1)).processSendEmail(baseEmailRequestDto);
  }
}

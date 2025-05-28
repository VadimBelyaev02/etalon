package com.andersenlab.etalon.infoservice.unit;

import static org.mockito.Mockito.times;

import com.andersenlab.etalon.infoservice.sqs.SqsMessageProducer;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class SqsMessageProducerTest {

  @InjectMocks SqsMessageProducer sqsMessageProducer;

  @Mock QueueMessagingTemplate queueMessagingTemplate;

  private String queue;
  private String payload;

  @BeforeEach
  void setUp() {
    queue = "testQueue";
    payload = "testPayload";
  }

  @Test
  void sendMessageWithHeaderTest() {
    // given
    Map<String, Object> header = new HashMap<>();

    // when
    sqsMessageProducer.sendMessage(queue, payload, header);

    // then
    Mockito.verify(queueMessagingTemplate, times(1)).convertAndSend(queue, payload, header);
  }

  @Test
  void sendMessageWithoutHeaderTest() {
    // when
    sqsMessageProducer.sendMessage(queue, payload);

    // then
    Mockito.verify(queueMessagingTemplate, times(1)).convertAndSend(queue, payload);
  }
}

package com.andersenlab.etalon.infoservice.sqs;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class SqsMessageProducer {

  private final QueueMessagingTemplate queueMessagingTemplate;

  public <T> void sendMessage(String queue, T payload, Map<String, Object> header) {
    log.info("{SqsMessageProducer:sendMessage} message {} to be send on queue {}", payload, queue);
    queueMessagingTemplate.convertAndSend(queue, payload, header);
  }

  public <T> void sendMessage(String queue, T payload) {
    log.info("{SqsMessageProducer:sendMessage} message {} to be send on queue {}", payload, queue);
    queueMessagingTemplate.convertAndSend(queue, payload);
  }
}

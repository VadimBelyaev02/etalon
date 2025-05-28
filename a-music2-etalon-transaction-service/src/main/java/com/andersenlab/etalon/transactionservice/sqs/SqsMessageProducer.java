package com.andersenlab.etalon.transactionservice.sqs;

import com.andersenlab.etalon.transactionservice.interceptor.AuthenticationHolder;
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
  private final AuthenticationHolder authenticationHolder;

  public <T> void send(String queue, T payload, Map<String, Object> header) {
    log.info("{SqsMessageProducer:sendMessage} message {} to be send on queue {}", payload, queue);
    queueMessagingTemplate.convertAndSend(queue, payload, header);
  }
}

package com.andersenlab.etalon.depositservice.config;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.SetQueueAttributesRequest;
import jakarta.annotation.PreDestroy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.elasticmq.rest.sqs.SQSRestServer;
import org.elasticmq.rest.sqs.SQSRestServerBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SqsInitializer
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {
  private static final int RETRY_COUNT_SET = 1;
  private static final String LOCALHOST = "http://localhost:%d";
  private static final String OPEN_DEPOSIT_QUEUE_PROPERTY_NAME = "sqs.queue.open-deposit.name";
  private static final String OPEN_DEPOSIT_DLQ_PROPERTY_NAME = "sqs.queue.open-deposit-dlq.name";
  private static final String REDRIVE_AND_DLQ_ATTRIBUTES =
      "{\"maxReceiveCount\":\"%d\", \"deadLetterTargetArn\":\"%s\"}";
  private static final String REDRIVE_POLICY = "RedrivePolicy";
  private static final String QUEUE_ARN = "QueueArn";
  public static AmazonSQS amazonSQS;
  public static String openDepositQueueUri;
  public static String address;
  private SQSRestServer sqsRestServer;

  @Override
  public void initialize(@NotNull ConfigurableApplicationContext applicationContext) {
    address = startSqs();
    amazonSQS = getAmazonSQS(address);

    ConfigurableEnvironment configurableEnvironment = applicationContext.getEnvironment();
    String openDepositQueue = configurableEnvironment.getProperty(OPEN_DEPOSIT_QUEUE_PROPERTY_NAME);
    String openDepositDlq = configurableEnvironment.getProperty(OPEN_DEPOSIT_DLQ_PROPERTY_NAME);

    String openDepositDeadLetterQueueUri = createQueue(amazonSQS, openDepositDlq);
    String openDepositDlqArn = getQueueArn(amazonSQS, openDepositDeadLetterQueueUri);
    Map<String, String> redrivePolicyAttributes = createRedrivePolicyAttributes(openDepositDlqArn);
    openDepositQueueUri = createQueue(amazonSQS, openDepositQueue);
    setQueueAttributes(amazonSQS, openDepositQueueUri, redrivePolicyAttributes);
  }

  private String startSqs() {
    sqsRestServer =
        SQSRestServerBuilder.withDynamicPort()
            .withInterface("localhost")
            .withAWSRegion(Regions.US_EAST_1.getName())
            .start();
    address = String.format(LOCALHOST, sqsRestServer.waitUntilStarted().localAddress().getPort());
    log.info("SQS server started with full path -> {}", address);
    return address;
  }

  private AmazonSQS getAmazonSQS(String address) {
    return AmazonSQSClientBuilder.standard()
        .withEndpointConfiguration(
            new AwsClientBuilder.EndpointConfiguration(address, Regions.US_EAST_1.getName()))
        .build();
  }

  private String createQueue(AmazonSQS amazonSQS, String queueName) {
    return amazonSQS.createQueue(new CreateQueueRequest(queueName)).getQueueUrl();
  }

  private String getQueueArn(AmazonSQS amazonSQS, String queueUri) {
    return amazonSQS
        .getQueueAttributes(queueUri, List.of(QUEUE_ARN))
        .getAttributes()
        .get(QUEUE_ARN);
  }

  private Map<String, String> createRedrivePolicyAttributes(String dlqArn) {
    Map<String, String> attributes = new HashMap<>();
    String redrivePolicy = REDRIVE_AND_DLQ_ATTRIBUTES.formatted(RETRY_COUNT_SET, dlqArn);
    attributes.put(REDRIVE_POLICY, redrivePolicy);
    return attributes;
  }

  private void setQueueAttributes(
      AmazonSQS amazonSQS, String queueUri, Map<String, String> attributes) {
    SetQueueAttributesRequest setAttrRequest =
        new SetQueueAttributesRequest().withQueueUrl(queueUri).withAttributes(attributes);
    amazonSQS.setQueueAttributes(setAttrRequest);
  }

  @PreDestroy
  public void stop() {
    sqsRestServer.stopAndWait();
    amazonSQS.shutdown();
    log.info("SQS server stopped");
  }
}

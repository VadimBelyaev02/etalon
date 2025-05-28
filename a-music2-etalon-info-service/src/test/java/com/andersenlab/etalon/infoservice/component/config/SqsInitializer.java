package com.andersenlab.etalon.infoservice.component.config;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.SetQueueAttributesRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PreDestroy;
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
  private static final String CREATE_CONFIRMATION_QUEUE_PROPERTY_NAME =
      "sqs.queue.create-confirmation.name";
  private static final String CREATE_CONFIRMATION_DLQ_PROPERTY_NAME =
      "sqs.queue.create-confirmation-dlq.name";
  private static final String SEND_EMAIL_PROPERTY_NAME = "sqs.queue.send-email.name";
  private static final String QUEUE_ARN = "QueueArn";
  private static final String REDRIVE_AND_DLQ_ATTRIBUTES =
      "{\"maxReceiveCount\":\"%d\", \"deadLetterTargetArn\":\"%s\"}";
  private static final String REDRIVE_POLICY = "RedrivePolicy";
  private AmazonSQS amazonSQS;
  private SQSRestServer sqsRestServer;
  public static String address;
  public static String createConfirmationQueue;
  public static String createConfirmationDlqQueue;
  public static String createEmailSendQueue;

  @Override
  public void initialize(@NotNull ConfigurableApplicationContext applicationContext) {
    address = startSqs();
    amazonSQS = getAmazonSQS(address);

    ConfigurableEnvironment configurableEnvironment = applicationContext.getEnvironment();

    createConfirmationQueue =
        configurableEnvironment.getProperty(CREATE_CONFIRMATION_QUEUE_PROPERTY_NAME);
    createConfirmationDlqQueue =
        configurableEnvironment.getProperty(CREATE_CONFIRMATION_DLQ_PROPERTY_NAME);
    createEmailSendQueue = configurableEnvironment.getProperty(SEND_EMAIL_PROPERTY_NAME);

    String createConfirmationDeadLetterQueueUri =
        createQueue(amazonSQS, createConfirmationDlqQueue);
    String createConfirmationDlqArn = getQueueArn(amazonSQS, createConfirmationDeadLetterQueueUri);

    Map<String, String> redrivePolicyAttributes =
        createRedrivePolicyAttributes(createConfirmationDlqArn);

    String createConfirmationQueueUri = createQueue(amazonSQS, createConfirmationQueue);
    setQueueAttributes(amazonSQS, createConfirmationQueueUri, redrivePolicyAttributes);

    createQueue(amazonSQS, createEmailSendQueue);
  }

  public String startSqs() {
    sqsRestServer =
        SQSRestServerBuilder.withDynamicPort()
            .withInterface("localhost")
            .withAWSRegion(Regions.US_EAST_1.getName())
            .start();
    address = String.format(LOCALHOST, sqsRestServer.waitUntilStarted().localAddress().getPort());
    log.info("SQS server started with full path -> {}", address);
    return address;
  }

  public AmazonSQS getAmazonSQS(String address) {
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

package com.andersenlab.etalon.transactionservice.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.AwsRegionProvider;
import com.amazonaws.regions.DefaultAwsRegionProviderChain;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.andersenlab.etalon.transactionservice.config.properties.AwsSqsConfigProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.aws.messaging.config.QueueMessageHandlerFactory;
import org.springframework.cloud.aws.messaging.config.annotation.SqsConfiguration;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.QueueMessageHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@RequiredArgsConstructor
public class AwsSqsConfig extends SqsConfiguration {

  private final AwsSqsConfigProperties properties;

  @Bean
  @Primary
  @Profile({"!default & !test"})
  public AmazonSQSAsync amazonSQSAsync() {
    return AmazonSQSAsyncClientBuilder.standard()
        .withRegion(awsRegionProvider().getRegion())
        .build();
  }

  @Bean
  public AwsRegionProvider awsRegionProvider() {
    return new DefaultAwsRegionProviderChain();
  }

  @Bean
  @Primary
  @Profile({"default", "test"})
  public AmazonSQSAsync amazonSQSAsyncLocal() {
    return AmazonSQSAsyncClientBuilder.standard()
        .withEndpointConfiguration(
            new AwsClientBuilder.EndpointConfiguration(
                properties.getEndpointUri(), properties.getStaticRegion()))
        .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("x", "x")))
        .build();
  }

  @Bean
  public QueueMessagingTemplate queueMessagingTemplate(AmazonSQSAsync amazonSQSAsync) {
    return new QueueMessagingTemplate(amazonSQSAsync);
  }

  @Override
  public QueueMessageHandler queueMessageHandler(AmazonSQSAsync amazonSqs) {
    QueueMessageHandlerFactory factory = new QueueMessageHandlerFactory();
    factory.setAmazonSqs(amazonSqs);
    return factory.createQueueMessageHandler();
  }
}

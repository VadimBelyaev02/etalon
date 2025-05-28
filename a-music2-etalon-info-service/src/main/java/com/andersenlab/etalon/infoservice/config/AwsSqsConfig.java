package com.andersenlab.etalon.infoservice.config;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.AwsRegionProvider;
import com.amazonaws.regions.DefaultAwsRegionProviderChain;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.andersenlab.etalon.infoservice.config.properties.AwsConfigProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@RequiredArgsConstructor
public class AwsSqsConfig {
  private final AwsConfigProperties properties;

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
        .build();
  }

  @Bean
  public QueueMessagingTemplate queueMessagingTemplate(AmazonSQSAsync amazonSQSAsync) {
    return new QueueMessagingTemplate(amazonSQSAsync);
  }
}

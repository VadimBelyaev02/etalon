package com.andersenlab.etalon.depositservice.integration;

import static com.andersenlab.etalon.depositservice.client.AccountServiceClient.ACCOUNT_BY_NUMBER_URL;
import static com.andersenlab.etalon.depositservice.client.TransactionServiceClient.TRANSACTIONS_URL;
import static com.andersenlab.etalon.depositservice.util.enums.DepositStatus.CREATED;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.context.jdbc.SqlMergeMode.MergeMode.MERGE;

import com.andersenlab.etalon.depositservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.depositservice.dto.transaction.response.TransactionInfoResponseDto;
import com.andersenlab.etalon.depositservice.entity.DepositOrderEntity;
import com.andersenlab.etalon.depositservice.repository.DepositOrderRepository;
import com.andersenlab.etalon.depositservice.util.enums.DepositStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import java.time.Duration;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.NOT_SUPPORTED)
@SqlMergeMode(MERGE)
public class SqsAbstractIntegrationTest extends AbstractIntegrationTest {
  public static final String TRANSACTION_PATH = "/transaction";
  public static final String ACCOUNT_PATH = "/account";
  public static final long TRANSACTION_ID = 3L;
  public static final String ACCOUNT_NUMBER = "PL04234567840000000000000001";
  public static final String NEXT_STATE = "SECOND";
  private static final String OPEN_DEPOSIT_QUEUE_NAME = "open-deposit-queue";
  public static final String AUTHENTICATED_USER_ID = "authenticated-user-id";
  public static final String USER = "user";
  @Autowired private DepositOrderRepository depositOrderRepository;
  @Autowired QueueMessagingTemplate queueMessagingTemplate;

  @Test
  @Sql(scripts = "file:src/test/resources/data/deposit-orders-initial-data.sql")
  void whenConsumeMessageAndCreateDepositAndErrorOccurred_shouldDlqLogicProcessed() {
    String message = "Simulated Exception";
    MessageResponseDto errorMessageResponseDto = new MessageResponseDto(message);
    TransactionInfoResponseDto transactionInfoResponseDto =
        new TransactionInfoResponseDto(TRANSACTION_ID, ACCOUNT_NUMBER, CREATED.name());
    createWireMockStub(
        Scenario.STARTED, NEXT_STATE, 500, objectMapper.valueToTree(errorMessageResponseDto));
    createWireMockStub(
        "SECOND", Scenario.STARTED, 200, objectMapper.valueToTree(transactionInfoResponseDto));
    queueMessagingTemplate.convertAndSend(
        OPEN_DEPOSIT_QUEUE_NAME, TRANSACTION_ID, Map.of(AUTHENTICATED_USER_ID, USER));
    await()
        .atMost(Duration.ofSeconds(60))
        .with()
        .pollDelay(Duration.ofSeconds(1))
        .untilAsserted(
            () -> {
              DepositOrderEntity depositOrder =
                  depositOrderRepository
                      .findDepositOrderEntityByTransactionId(TRANSACTION_ID)
                      .get();
              assertThat(depositOrder.getStatus()).isEqualByComparingTo(DepositStatus.REJECTED);
              mockAccountService.verify(
                  exactly(1),
                  deleteRequestedFor(
                      urlEqualTo(
                          String.format(
                              "%s/%s", ACCOUNT_PATH + ACCOUNT_BY_NUMBER_URL, ACCOUNT_NUMBER))));
            });
  }

  private void createWireMockStub(
      String currentState, String nextState, int status, JsonNode responseBody) {
    mockTransactionService.stubFor(
        get(urlEqualTo(String.format("%s/%s", TRANSACTION_PATH + TRANSACTIONS_URL, TRANSACTION_ID)))
            .inScenario("transaction")
            .whenScenarioStateIs(currentState)
            .willSetStateTo(nextState)
            .willReturn(
                aResponse()
                    .withStatus(status)
                    .withHeader("Content-Type", "application/json")
                    .withBody(responseBody.toString())));
  }
}

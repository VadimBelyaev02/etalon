package com.andersenlab.etalon.transactionservice.integration.controller;

import static com.andersenlab.etalon.transactionservice.MockData.getValidTransactionCreateRequestDto;
import static com.andersenlab.etalon.transactionservice.util.enums.TransactionStatus.APPROVED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.context.jdbc.SqlMergeMode.MergeMode.MERGE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.andersenlab.etalon.transactionservice.annotation.WithUserId;
import com.andersenlab.etalon.transactionservice.config.SqsInitializer;
import com.andersenlab.etalon.transactionservice.controller.PaymentController;
import com.andersenlab.etalon.transactionservice.controller.TransactionController;
import com.andersenlab.etalon.transactionservice.controller.TransferController;
import com.andersenlab.etalon.transactionservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.transactionservice.dto.sqs.TransactionQueueMessage;
import com.andersenlab.etalon.transactionservice.dto.transaction.request.TransactionCreateRequestDto;
import com.andersenlab.etalon.transactionservice.entity.EventEntity;
import com.andersenlab.etalon.transactionservice.entity.PaymentEntity;
import com.andersenlab.etalon.transactionservice.entity.TransactionEntity;
import com.andersenlab.etalon.transactionservice.entity.TransferEntity;
import com.andersenlab.etalon.transactionservice.integration.AbstractIntegrationTest;
import com.andersenlab.etalon.transactionservice.repository.EventRepository;
import com.andersenlab.etalon.transactionservice.repository.PaymentRepository;
import com.andersenlab.etalon.transactionservice.repository.TransactionRepository;
import com.andersenlab.etalon.transactionservice.repository.TransferRepository;
import com.andersenlab.etalon.transactionservice.util.Constants;
import com.andersenlab.etalon.transactionservice.util.enums.Details;
import com.andersenlab.etalon.transactionservice.util.enums.PaymentStatus;
import com.andersenlab.etalon.transactionservice.util.enums.TransactionStatus;
import com.andersenlab.etalon.transactionservice.util.enums.Type;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.http.MediaType;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Sql(scripts = "file:src/test/resources/data/clean-up-data.sql")
@SqlMergeMode(MERGE)
@Slf4j
public class SqsCreateTransactionEndpointTest extends AbstractIntegrationTest {

  public static final String USER_ID = "user";
  public static final Long VALID_PAYMENT_ID = 1L;

  @Autowired private TransactionRepository transactionRepository;
  @Autowired private TransferRepository transferRepository;
  @Autowired private PaymentRepository paymentRepository;
  @Autowired private EventRepository eventRepository;
  private final AmazonSQS amazonSQS = SqsInitializer.amazonSQS;
  private final String openDepositQueueUri = SqsInitializer.openDepositQueueUri;
  private final String createTransactionQueueUri = SqsInitializer.createTransactionQueueUri;
  @Autowired QueueMessagingTemplate queueMessagingTemplate;

  @ParameterizedTest
  @CsvSource({"4,20", "5,404.00", "6,25.00"})
  @WithUserId
  @Sql(
      scripts = "file:src/test/resources/data/transfers-initial-data.sql",
      executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
  @Sql(
      scripts = "file:src/test/resources/data/clean-up-data.sql",
      executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
  void whenTransferWithDifferentCurrencies_shouldApplyRate(
      final Long transferId, final BigDecimal amountWithRatesApplied) throws Exception {

    mockMvc
        .perform(
            post(TransferController.TRANSFER_CONFIRMED_V2_URL, transferId)
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isCreated());

    await()
        .atMost(Duration.ofSeconds(20))
        .with()
        .pollDelay(Duration.ofSeconds(3))
        .untilAsserted(
            () -> {
              List<TransactionEntity> transactions = transactionRepository.findAll();
              TransactionEntity transactionEntity = transactions.get(0);
              assertThat(transactions).hasSize(1);
              assertThat(transactionEntity.getStatus().name()).isEqualTo(APPROVED.name());

              List<EventEntity> events = eventRepository.findAll();
              assertThat(
                      events.stream()
                          .filter(eventEntity -> Type.OUTCOME.equals(eventEntity.getType()))
                          .map(EventEntity::getAmount)
                          .findFirst())
                  .isEqualTo(Optional.of(transactionEntity.getAmount()));

              assertThat(
                      events.stream()
                          .filter(eventEntity -> Type.INCOME.equals(eventEntity.getType()))
                          .map(EventEntity::getAmount)
                          .findFirst())
                  .usingValueComparator(BigDecimal::compareTo)
                  .contains(amountWithRatesApplied);
            });
  }

  @Test
  @Sql(
      scripts = "file:src/test/resources/data/clean-up-data.sql",
      executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
  @WithUserId
  void shouldSuccess_WhenCreateTransactionAndProduceMessage() throws Exception {
    TransactionCreateRequestDto transactionCreateRequestDto =
        getValidTransactionCreateRequestDto().toBuilder()
            .details(Details.OPEN_DEPOSIT)
            .amount(BigDecimal.valueOf(500))
            .build();

    ReceiveMessageRequest request = new ReceiveMessageRequest(openDepositQueueUri);

    mockMvc
        .perform(
            post(TransactionController.TRANSACTIONS_V1_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(transactionCreateRequestDto)))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(
            jsonPath("$.messageResponseDto.message", is("Transaction with id 1 was created")))
        .andExpect(jsonPath("$.transactionId", is(1)))
        .andExpect(jsonPath("$.status", is(Constants.CREATED)));

    await()
        .atMost(Duration.ofSeconds(5))
        .with()
        .pollDelay(Duration.ofSeconds(1))
        .untilAsserted(
            () -> {
              List<TransactionEntity> transactions = transactionRepository.findAll();
              TransactionEntity transactionEntity = transactions.get(0);
              assertThat(transactions).hasSize(1);
              assertThat(transactionEntity.getStatus().name()).isEqualTo(Constants.APPROVED);
              assertThat(transactionEntity.getDetails().name()).isEqualTo(Constants.OPEN_DEPOSIT);
              assertThat(transactionEntity.getId()).isEqualTo(1);
              assertFalse(amazonSQS.receiveMessage(request).getMessages().isEmpty());
            });
  }

  @Test
  @Sql(
      scripts = "file:src/test/resources/data/clean-up-data.sql",
      executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
  @WithUserId
  void shouldSuccessCreateNewTransaction() throws Exception {
    TransactionCreateRequestDto transactionCreateRequestDto = getValidTransactionCreateRequestDto();

    mockMvc
        .perform(
            post(TransactionController.TRANSACTIONS_V1_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(transactionCreateRequestDto)))
        .andExpect(status().isCreated())
        .andExpect(
            jsonPath("$.messageResponseDto.message", is("Transaction with id 1 was created")))
        .andExpect(jsonPath("$.transactionId", is(1)))
        .andExpect(jsonPath("$.status", is(Constants.CREATED)));

    await()
        .atMost(Duration.ofSeconds(5))
        .with()
        .pollDelay(Duration.ofSeconds(1))
        .untilAsserted(
            () -> {
              assertThat(transactionRepository.findById(1L)).isPresent();
              assertThat(transactionRepository.findById(1L).get().getStatus()).isEqualTo(APPROVED);
            });
  }

  @Test
  @WithUserId("7")
  @Sql(
      scripts = "file:src/test/resources/data/clean-up-data.sql",
      executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
  @Sql(scripts = {"file:src/test/resources/data/payments-initial-data.sql"})
  void shouldReturnProcessingMessage_WhenPaymentConfirmationSuccessful() throws Exception {
    mockMvc
        .perform(
            post(PaymentController.PAYMENTS_URL + "/" + 2L + PaymentController.PAYMENT_CONFIRM_URI)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(
            MockMvcResultMatchers.jsonPath(
                "$.message", is(MessageResponseDto.OPERATION_IS_PROCESSING)));

    await()
        .atMost(Duration.ofSeconds(60))
        .with()
        .pollDelay(Duration.ofSeconds(2))
        .untilAsserted(
            () -> {
              Optional<PaymentEntity> optionalPayment =
                  paymentRepository.findById(VALID_PAYMENT_ID);
              assertThat(optionalPayment).isPresent();
            });
  }

  @Test
  @WithUserId(USER_ID)
  @Sql(
      scripts = "file:src/test/resources/data/clean-up-data.sql",
      executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
  @Sql(
      scripts = {"file:src/test/resources/data/payments-initial-data.sql"},
      executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
  void shouldReturnProcessingMessage_WhenPaymentTransactionConfirmationSuccessful()
      throws Exception {

    mockMvc
        .perform(
            post(TransactionController.TRANSACTIONS_V1_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    toJson(
                        TransactionCreateRequestDto.builder()
                            .accountNumberWithdrawn("PL48234567840000000000000011")
                            .amount(BigDecimal.TEN)
                            .accountNumberReplenished("PL48234567840000000000000012")
                            .transactionName("Payment for sins")
                            .details(Details.PAYMENT)
                            .isFeeProvided(false)
                            .build())))
        .andExpect(status().isCreated())
        .andExpect(
            jsonPath("$.messageResponseDto.message", is("Transaction with id 1 was created")))
        .andExpect(jsonPath("$.transactionId", is(1)))
        .andExpect(jsonPath("$.status", is(Constants.CREATED)));

    await()
        .atMost(Duration.ofSeconds(5))
        .with()
        .pollDelay(Duration.ofSeconds(1))
        .untilAsserted(
            () -> {
              Optional<PaymentEntity> optionalPayment =
                  paymentRepository.findById(VALID_PAYMENT_ID);
              assertThat(optionalPayment).isPresent();
              assertThat(optionalPayment.get().getStatus()).isEqualTo(PaymentStatus.APPROVED);
            });
  }

  @Test
  @Sql(
      scripts = "file:src/test/resources/data/clean-up-data.sql",
      executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
  @Sql(
      scripts = "file:src/test/resources/data/transfers-initial-data.sql",
      executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
  @WithUserId
  void transferShouldBeApprovedAfterNewTransactionCreation() throws Exception {
    TransactionCreateRequestDto transactionCreateRequestDto =
        getValidTransactionCreateRequestDto().toBuilder().details(Details.TRANSFER).build();

    mockMvc
        .perform(
            post(TransactionController.TRANSACTIONS_V1_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(transactionCreateRequestDto)))
        .andExpect(status().isCreated())
        .andExpect(
            jsonPath("$.messageResponseDto.message", is("Transaction with id 1 was created")))
        .andExpect(jsonPath("$.transactionId", is(1)))
        .andExpect(jsonPath("$.status", is("CREATED")));

    await()
        .atMost(Duration.ofSeconds(12))
        .with()
        .pollDelay(Duration.ofSeconds(2))
        .untilAsserted(
            () -> {
              List<TransactionEntity> transactions = transactionRepository.findAll();
              TransactionEntity transactionEntity = transactions.get(0);
              assertThat(transactions).hasSize(1);
              assertThat(transactionEntity.getStatus().name()).isEqualTo(Constants.APPROVED);
              assertThat(transactionEntity.getDetails().name()).isEqualTo(Constants.TRANSFER);
              assertThat(transactionEntity.getId()).isEqualTo(1);

              Optional<TransferEntity> optionalTransferEntity =
                  transferRepository.findByTransactionId(transactionEntity.getId());
              assertThat(optionalTransferEntity).isPresent();
              TransferEntity transferEntity = optionalTransferEntity.get();
              assertThat(transferEntity.getStatus().name()).isEqualTo(Constants.APPROVED);
              assertThat(transferEntity.getTransactionId()).isEqualTo(transactionEntity.getId());
              assertThat(transferEntity.getAmount()).isEqualTo(transactionEntity.getAmount());
              assertThat(transferEntity.getSource())
                  .isEqualTo(transactionEntity.getSenderAccount());
              assertThat(transferEntity.getDestination())
                  .isEqualTo(transactionEntity.getReceiverAccount());
            });
  }

  @Test
  @Sql(scripts = "file:src/test/resources/data/transaction-initial-data.sql")
  @WithUserId
  void whenCreateNewTransactionAndExceptionOccurred_shouldDlqLogicProcessed() {
    // Arrange
    TransactionQueueMessage transactionQueueMessage =
        TransactionQueueMessage.builder()
            .transactionId(1L)
            .loanInterestAmount(BigDecimal.valueOf(25.0))
            .build();
    Message<TransactionQueueMessage> message =
        MessageBuilder.withPayload(transactionQueueMessage).build();

    queueMessagingTemplate.convertAndSend(
        createTransactionQueueUri,
        transactionQueueMessage,
        Map.of(Constants.AUTHENTICATED_USER_ID, 1L));

    await()
        .atMost(Duration.ofSeconds(45))
        .with()
        .pollDelay(Duration.ofSeconds(1))
        .untilAsserted(
            () -> {
              long transactionId = message.getPayload().transactionId();
              TransactionEntity transaction = transactionRepository.findById(transactionId).get();
              assertThat(transaction.getStatus().name())
                  .isEqualTo(TransactionStatus.DECLINED.name());
            });
  }
}

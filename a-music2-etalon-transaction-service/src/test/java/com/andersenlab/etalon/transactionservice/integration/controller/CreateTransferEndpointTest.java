package com.andersenlab.etalon.transactionservice.integration.controller;

import static com.andersenlab.etalon.transactionservice.MockData.getValidCreateNewTransferResponse;
import static com.andersenlab.etalon.transactionservice.MockData.getValidCreateTransferRequestDto;
import static com.andersenlab.etalon.transactionservice.util.Constants.INVALID_AMOUNT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.transactionservice.annotation.WithUserId;
import com.andersenlab.etalon.transactionservice.controller.TransferController;
import com.andersenlab.etalon.transactionservice.dto.transaction.request.CreateTransferRequestDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.CreateNewTransferResponseDto;
import com.andersenlab.etalon.transactionservice.entity.TransferEntity;
import com.andersenlab.etalon.transactionservice.exception.BusinessException;
import com.andersenlab.etalon.transactionservice.integration.AbstractIntegrationTest;
import com.andersenlab.etalon.transactionservice.repository.TransferRepository;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class CreateTransferEndpointTest extends AbstractIntegrationTest {
  public static final String USER_ID = "1";
  private CreateTransferRequestDto createTransferRequestDto;
  private CreateNewTransferResponseDto createNewTransferResponseDto;
  @Autowired private TransferRepository transferRepository;

  @BeforeEach
  public void beforeEach() {
    createTransferRequestDto = getValidCreateTransferRequestDto();
    createNewTransferResponseDto = getValidCreateNewTransferResponse();
  }

  @ParameterizedTest
  @CsvSource({
    "PL48234567840000000000000222,PL48234567840000000000000111,3.8462",
    "PL48234567840000000000000111,PL48234567840000000000000222,0.2498",
    "PL48234567840000000000000222,PL48234567840000000000000333,0.8862",
    "PL48234567840000000000000333,PL48234567840000000000000222,1.0417"
  })
  @WithUserId("transfer-user")
  void whenCreateTransferBetweenUserCardsWithDifferentCurrencies_shouldGetCorrectRate(
      final String sender, final String beneficiary, final String expectedStandardRate)
      throws Exception {
    AtomicLong transferId = new AtomicLong();
    mockMvc
        .perform(
            post(TransferController.TRANSFERS_V2_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    toJson(
                        CreateTransferRequestDto.builder()
                            .sender(sender)
                            .beneficiary(beneficiary)
                            .amount(BigDecimal.valueOf(100L))
                            .description("Transfer")
                            .build())))
        .andExpect(status().isCreated())
        .andDo(
            result ->
                transferId.set(
                    objectMapper
                        .readValue(
                            result.getResponse().getContentAsString(),
                            CreateNewTransferResponseDto.class)
                        .transferId()));
    Optional<TransferEntity> optionalTransfer = transferRepository.findById(transferId.get());
    assertThat(optionalTransfer).isPresent();
    TransferEntity transfer = optionalTransfer.get();
    assertThat(transfer.getSource()).isEqualTo(sender);
    assertThat(transfer.getDestination()).isEqualTo(beneficiary);
    assertThat(transfer.getStandardRate().scale()).isBetween(0, 10);
    assertThat(transfer.getStandardRate()).isEqualByComparingTo(expectedStandardRate);
  }

  @Test
  @WithUserId
  void whenCreateTransferBetweenCardsWithSameCurrency_shouldGetNullRate() throws Exception {

    final String sender = "PL48234567840000000000000011";
    final String beneficiary = "PL48234567840000000000000012";
    AtomicLong transferId = new AtomicLong();

    mockMvc
        .perform(
            post(TransferController.TRANSFERS_V2_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    toJson(
                        CreateTransferRequestDto.builder()
                            .sender(sender)
                            .beneficiary(beneficiary)
                            .amount(BigDecimal.valueOf(100L))
                            .description("Transfer")
                            .build())))
        .andExpect(status().isCreated())
        .andDo(
            result ->
                transferId.set(
                    objectMapper
                        .readValue(
                            result.getResponse().getContentAsString(),
                            CreateNewTransferResponseDto.class)
                        .transferId()));

    Optional<TransferEntity> optionalTransfer = transferRepository.findById(transferId.get());
    assertThat(optionalTransfer).isPresent();
    TransferEntity transfer = optionalTransfer.get();
    assertThat(transfer.getSource()).isEqualTo(sender);
    assertThat(transfer.getDestination()).isEqualTo(beneficiary);
    assertThat(transfer.getStandardRate()).isNull();
  }

  @Test
  @WithUserId
  void whenCreateTransferBetweenUserAccounts_shouldSuccess() throws Exception {

    mockMvc
        .perform(
            post(TransferController.TRANSFERS_V2_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(createTransferRequestDto)))
        .andExpect(status().isCreated());
  }

  @Test
  @WithUserId
  void whenCreateTransferNotToUserAccount_shouldSuccess() throws Exception {
    CreateTransferRequestDto transfer =
        createTransferRequestDto.toBuilder().beneficiary("PL48234567840000000000000031").build();
    mockMvc
        .perform(
            post(TransferController.TRANSFERS_V2_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(transfer)))
        .andExpect(status().isCreated());
  }

  @Test
  @WithUserId
  void whenCreateTransferBetweenSameUserCards_shouldSuccess() throws Exception {
    CreateNewTransferResponseDto expectedResponse =
        createNewTransferResponseDto.toBuilder()
            .fee(BigDecimal.ZERO)
            .feeRate(null)
            .standardRate(null)
            .totalAmount(BigDecimal.valueOf(1000L))
            .build();
    mockMvc
        .perform(
            post(TransferController.TRANSFERS_V2_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(createTransferRequestDto)))
        .andExpect(status().isCreated())
        .andExpect(MockMvcResultMatchers.content().json(toJson(expectedResponse)));
  }

  @Test
  @WithUserId
  void whenCreateTransferFromNotUserAccount_shouldFail() throws Exception {
    CreateTransferRequestDto transfer =
        createTransferRequestDto.toBuilder().sender("PL48234567840000000000000031").build();
    mockMvc
        .perform(
            post(TransferController.TRANSFERS_V2_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(transfer)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", is(BusinessException.OPERATION_REJECTED_DUE_TO_SECURITY)));
  }

  @Test
  @WithUserId
  void whenCreateTransferWithLowAmount_shouldFail() throws Exception {
    CreateTransferRequestDto transfer =
        createTransferRequestDto.toBuilder().amount(BigDecimal.ZERO).build();

    mockMvc
        .perform(
            post(TransferController.TRANSFERS_V2_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(transfer)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", is(INVALID_AMOUNT)));
  }

  @Test
  @WithUserId
  void whenCreateTransferWithMoreThan2Digits_shouldFail() throws Exception {
    CreateTransferRequestDto transfer =
        createTransferRequestDto.toBuilder().amount(BigDecimal.valueOf(10.6575)).build();

    mockMvc
        .perform(
            post(TransferController.TRANSFERS_V2_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(transfer)))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath(
                "$.message",
                is(
                    BusinessException
                        .OPERATION_REJECTED_BECAUSE_AMOUNT_HAS_MORE_THAN_2_DIGITS_AFTER_DOT)));
  }

  @Test
  @WithUserId
  void whenCreateTransfer_shouldFailBecauseAccountIsBlocked() throws Exception {
    CreateTransferRequestDto transfer =
        createTransferRequestDto.toBuilder()
            .sender("PL48234567840000000000000041")
            .beneficiary("PL48234567840000000000000011")
            .build();

    mockMvc
        .perform(
            post(TransferController.TRANSFERS_V2_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(transfer)))
        .andExpect(status().isBadRequest())
        .andExpect(
            result -> assertInstanceOf(BusinessException.class, result.getResolvedException()))
        .andExpect(
            result ->
                assertEquals(
                    BusinessException.ACCOUNT_IS_BLOCKED,
                    Objects.requireNonNull(result.getResolvedException()).getMessage()));
  }
}

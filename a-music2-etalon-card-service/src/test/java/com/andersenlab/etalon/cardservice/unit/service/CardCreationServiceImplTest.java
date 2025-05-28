package com.andersenlab.etalon.cardservice.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.andersenlab.etalon.cardservice.MockData;
import com.andersenlab.etalon.cardservice.client.AccountServiceClient;
import com.andersenlab.etalon.cardservice.client.InfoServiceClient;
import com.andersenlab.etalon.cardservice.client.UserServiceClient;
import com.andersenlab.etalon.cardservice.config.TimeProvider;
import com.andersenlab.etalon.cardservice.dto.account.request.AccountCreationRequestDto;
import com.andersenlab.etalon.cardservice.dto.account.response.AccountResponseDto;
import com.andersenlab.etalon.cardservice.dto.card.request.CardCreationRequestDto;
import com.andersenlab.etalon.cardservice.dto.card.response.CardDetailedResponseDto;
import com.andersenlab.etalon.cardservice.dto.card.response.CardProductResponseDto;
import com.andersenlab.etalon.cardservice.dto.info.response.BankBranchesResponseDto;
import com.andersenlab.etalon.cardservice.dto.user.response.UserDataResponseDto;
import com.andersenlab.etalon.cardservice.entity.CardEntity;
import com.andersenlab.etalon.cardservice.entity.CurrencyEntity;
import com.andersenlab.etalon.cardservice.entity.CurrencyLimitEntity;
import com.andersenlab.etalon.cardservice.exception.BusinessException;
import com.andersenlab.etalon.cardservice.mapper.CardMapper;
import com.andersenlab.etalon.cardservice.mapper.ProductMapper;
import com.andersenlab.etalon.cardservice.repository.CardRepository;
import com.andersenlab.etalon.cardservice.repository.CurrencyLimitRepository;
import com.andersenlab.etalon.cardservice.service.CardProductService;
import com.andersenlab.etalon.cardservice.service.GeneratorService;
import com.andersenlab.etalon.cardservice.service.impl.CardCreationServiceImpl;
import com.andersenlab.etalon.cardservice.util.enums.Currency;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class CardCreationServiceImplTest {
  private static final Long VALID_CARD_PRODUCT_ID = 1L;
  private static final Long INVALID_CARD_PRODUCT_ID = 100000L;
  private static final String USER_ID = "user";
  private CardEntity card;
  private CardProductResponseDto productResponseDto;
  private BankBranchesResponseDto branch;
  private CardDetailedResponseDto expected;
  private CardCreationRequestDto request;
  private UserDataResponseDto user;
  private AccountResponseDto account;
  private AccountCreationRequestDto accountCreation;
  private CurrencyLimitEntity currencyLimit;
  @Mock private UserServiceClient userServiceClient;
  @Mock private AccountServiceClient accountServiceClient;
  @Mock private InfoServiceClient infoServiceClient;
  @Mock private CardRepository cardRepository;
  @Mock private CardProductService cardProductService;
  @Mock private GeneratorService generator;
  @Mock private TimeProvider timeProvider;
  @Mock private ProductMapper productMapper;
  @Mock private CurrencyLimitRepository currencyLimitRepository;
  @Spy private CardMapper cardMapper = Mappers.getMapper(CardMapper.class);
  @InjectMocks private CardCreationServiceImpl cardCreationService;

  @BeforeEach
  void setUp() {
    card = MockData.getValidCardCreatedEntity();
    productResponseDto = MockData.getValidCardProductResponseDto();
    branch = MockData.getValidBankBranchesResponseDto();
    account = MockData.getValidAccountResponseDto();
    accountCreation = MockData.getValidAccountCreationRequestDto();
    user = MockData.getValidUserDataResponseDto();
    expected = MockData.getValidCardDetailedResponseDto();
    request = MockData.getValidCardCreationRequestDto();
  }

  @ParameterizedTest
  @EnumSource(Currency.class)
  void whenCreateNewCard_thenSuccess(Currency currency) {
    // given
    given(infoServiceClient.getBankBranch(request.bankBranchId())).willReturn(branch);
    given(userServiceClient.getUserData(USER_ID)).willReturn(user);
    accountCreation = accountCreation.toBuilder().currency(currency).build();
    account = account.toBuilder().currency(currency).iban("IBAN_FOR_" + currency.name()).build();
    given(accountServiceClient.createAccount(accountCreation)).willReturn(account);
    currencyLimit = MockData.getDefaultCurrencyLimitEntity(currency);
    given(currencyLimitRepository.findById(currency)).willReturn(Optional.of(currencyLimit));
    request = request.toBuilder().currencies(List.of(currency)).build();
    expected =
        expected.toBuilder()
            .currency(currency)
            .accountNumber("IBAN_FOR_" + currency.name())
            .accounts(Map.of("IBAN_FOR_" + currency.name(), currency))
            .withdrawLimit(currencyLimit.getWithdrawLimit())
            .transferLimit(currencyLimit.getTransferLimit())
            .dailyExpenseLimit(currencyLimit.getDailyExpenseLimit())
            .build();
    card =
        card.toBuilder()
            .accountNumber("IBAN_FOR_" + currency.name())
            .withdrawLimit(currencyLimit.getWithdrawLimit())
            .transferLimit(currencyLimit.getTransferLimit())
            .dailyExpenseLimit(currencyLimit.getDailyExpenseLimit())
            .product(
                MockData.getValidCardProductEntity().toBuilder()
                    .availableCurrencies(
                        List.of(
                            CurrencyEntity.builder()
                                .currencyCode(currency)
                                .currencyName(currency.name())
                                .build()))
                    .build())
            .build();
    productResponseDto =
        productResponseDto.toBuilder().availableCurrencies(List.of(currency)).build();
    given(cardProductService.getCardProductById(VALID_CARD_PRODUCT_ID))
        .willReturn(productResponseDto);
    given(cardRepository.findFirstByOrderByIdDesc()).willReturn(Optional.ofNullable(card));
    given(cardRepository.save(any(CardEntity.class))).willReturn(card);

    // when
    when(timeProvider.getCurrentZonedDateTime()).thenReturn(ZonedDateTime.now());
    CardDetailedResponseDto result = cardCreationService.createUserCard(request, USER_ID);

    // then
    verify(infoServiceClient, times(1)).getBankBranch(request.bankBranchId());
    verify(userServiceClient, times(1)).getUserData(USER_ID);
    verify(accountServiceClient, times(1)).createAccount(accountCreation);
    verify(cardRepository, times(1)).findFirstByOrderByIdDesc();
    verify(cardProductService, times(1)).getCardProductById(VALID_CARD_PRODUCT_ID);
    verify(cardRepository, times(1)).save(any(CardEntity.class));

    assertEquals(expected.id(), result.id());
    assertEquals(expected.issuer(), result.issuer());
    assertEquals(expected.number(), result.number());
    assertEquals(expected.productType(), result.productType());
    assertEquals(expected.productName(), result.productName());
    assertEquals(expected.accountNumber(), result.accountNumber());
    assertEquals(expected.currency(), result.currency());
    assertEquals(expected.isBlocked(), result.isBlocked());
    assertEquals(expected.status(), result.status());
    assertEquals(expected.cardholderName(), result.cardholderName());
    assertEquals(expected.withdrawLimit(), result.withdrawLimit());
    assertEquals(expected.transferLimit(), result.transferLimit());
    assertEquals(expected.dailyExpenseLimit(), result.dailyExpenseLimit());
    assertEquals(expected.bankBranchId(), result.bankBranchId());
  }

  @Test
  void givenInvalidCardProductId_whenCreateUserCard_thenThrowBusinessException() {
    // given
    given(cardProductService.getCardProductById(INVALID_CARD_PRODUCT_ID))
        .willThrow(
            new BusinessException(
                HttpStatus.NOT_FOUND,
                String.format(BusinessException.CARD_PRODUCT_NOT_FOUND, INVALID_CARD_PRODUCT_ID)));
    List<Currency> availableCurrencies = List.of(Currency.PLN);
    CardCreationRequestDto requestWithEmptyProductId =
        CardCreationRequestDto.builder()
            .cardProductId(INVALID_CARD_PRODUCT_ID)
            .bankBranchId(request.bankBranchId())
            .currencies(availableCurrencies)
            .build();

    // when & then
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> cardCreationService.createUserCard(requestWithEmptyProductId, USER_ID));

    assertTrue(
        exception
            .getMessage()
            .contains(
                String.format(BusinessException.CARD_PRODUCT_NOT_FOUND, INVALID_CARD_PRODUCT_ID)));
  }

  @Test
  void givenEmptyCurrencies_whenCreateUserCard_thenThrowBusinessException() {
    CardCreationRequestDto requestWithEmptyCurrencies =
        CardCreationRequestDto.builder()
            .cardProductId(VALID_CARD_PRODUCT_ID)
            .bankBranchId(request.bankBranchId())
            .currencies(Collections.emptyList())
            .build();

    // when & then
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> cardCreationService.createUserCard(requestWithEmptyCurrencies, USER_ID));

    assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    assertEquals(BusinessException.CARD_CURRENCY_WAS_NOT_FOUND_IN_REQUEST, exception.getMessage());
  }

  @Test
  void givenUnavailableCurrency_whenCreateUserCard_thenThrowBusinessException() {
    // given
    given(cardProductService.getCardProductById(VALID_CARD_PRODUCT_ID))
        .willReturn(productResponseDto);
    List<Currency> unavailableCurrencies = List.of(Currency.EUR);
    CardCreationRequestDto requestWithUnavailableCurrency =
        CardCreationRequestDto.builder()
            .cardProductId(VALID_CARD_PRODUCT_ID)
            .bankBranchId(request.bankBranchId())
            .currencies(unavailableCurrencies)
            .build();

    // when & then
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> cardCreationService.createUserCard(requestWithUnavailableCurrency, USER_ID));

    assertEquals(HttpStatus.NOT_ACCEPTABLE, exception.getHttpStatus());
    assertTrue(exception.getMessage().contains(BusinessException.CURRENCIES_DOESNT_MATCH));
  }
}

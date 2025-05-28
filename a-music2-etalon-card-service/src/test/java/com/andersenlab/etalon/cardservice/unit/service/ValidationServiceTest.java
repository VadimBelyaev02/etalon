package com.andersenlab.etalon.cardservice.unit.service;

import static com.andersenlab.etalon.cardservice.MockData.getValidCardEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.andersenlab.etalon.cardservice.config.TimeProvider;
import com.andersenlab.etalon.cardservice.entity.CardEntity;
import com.andersenlab.etalon.cardservice.service.impl.ValidationServiceImpl;
import com.andersenlab.etalon.cardservice.util.enums.CardBlockingReason;
import com.andersenlab.etalon.cardservice.util.enums.CardStatus;
import java.time.Period;
import java.time.ZonedDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class ValidationServiceTest {
  @Mock private TimeProvider timeProvider;
  @InjectMocks private ValidationServiceImpl validationService;

  private static Stream<Arguments> provideCardsToCheckIfIsValidBeforeChangeCardStatus() {
    return Stream.of(
        Arguments.of(
            getValidCardEntity().toBuilder()
                .expirationDate(ZonedDateTime.now().plus(Period.ofDays(365)))
                .build(),
            "123456789",
            false),
        Arguments.of(
            getValidCardEntity().toBuilder()
                .expirationDate(ZonedDateTime.now().minus(Period.ofDays(1)))
                .build(),
            "123456789",
            false),
        Arguments.of(
            getValidCardEntity().toBuilder()
                .status(CardStatus.EXPIRED)
                .expirationDate(ZonedDateTime.now().plus(Period.ofDays(365)))
                .build(),
            "123456789",
            false),
        Arguments.of(
            getValidCardEntity().toBuilder()
                .expirationDate(ZonedDateTime.now().plus(Period.ofDays(365)))
                .build(),
            "000",
            false),
        Arguments.of(
            getValidCardEntity().toBuilder()
                .status(CardStatus.EXPIRED)
                .expirationDate(ZonedDateTime.now().plus(Period.ofDays(365)))
                .build(),
            "123456789",
            false));
  }

  private static Stream<Arguments> provideReasonToCheckIfIsValidBeforeSetBlockingCardReason() {
    return Stream.of(
        Arguments.of(CardBlockingReason.DAMAGED.name(), true),
        Arguments.of(CardBlockingReason.LOST.name(), true),
        Arguments.of(CardBlockingReason.STOLEN_FRAUD.name(), true),
        Arguments.of("BLOCK", false),
        Arguments.of("IDN", false));
  }

  private static Stream<Arguments> provideCardsToCheckIfExpired() {
    return Stream.of(
        Arguments.of(
            getValidCardEntity().toBuilder()
                .expirationDate(ZonedDateTime.now().plus(Period.ofDays(365)))
                .build()),
        Arguments.of(
            getValidCardEntity().toBuilder()
                .expirationDate(ZonedDateTime.now().plus(Period.ofDays(30)))
                .build()),
        Arguments.of(
            getValidCardEntity().toBuilder()
                .expirationDate(ZonedDateTime.now().plus(Period.ofDays(7)))
                .build()),
        Arguments.of(
            getValidCardEntity().toBuilder()
                .expirationDate(ZonedDateTime.now().plus(Period.ofDays(1)))
                .build()));
  }

  private static Stream<Arguments> provideExpiredCardsToCheckIfExpired() {
    return Stream.of(
        Arguments.of(
            getValidCardEntity().toBuilder()
                .expirationDate(ZonedDateTime.now().minus(Period.ofDays(365)))
                .build()),
        Arguments.of(
            getValidCardEntity().toBuilder()
                .expirationDate(ZonedDateTime.now().minus(Period.ofDays(30)))
                .build()),
        Arguments.of(
            getValidCardEntity().toBuilder()
                .expirationDate(ZonedDateTime.now().minus(Period.ofDays(7)))
                .build()),
        Arguments.of(
            getValidCardEntity().toBuilder()
                .expirationDate(ZonedDateTime.now().minus(Period.ofDays(1)))
                .build()),
        Arguments.of(
            getValidCardEntity().toBuilder()
                .expirationDate(ZonedDateTime.now().minusHours(1))
                .build()));
  }

  @ParameterizedTest
  @MethodSource("provideCardsToCheckIfIsValidBeforeChangeCardStatus")
  void whenBeforeChangeCardStatus_shouldCheckIsValidCard(
      CardEntity card, String userId, boolean expected) {
    assertEquals(expected, validationService.isValidCardBeforeChangeCardStatus(card, userId));
  }

  @ParameterizedTest
  @MethodSource("provideReasonToCheckIfIsValidBeforeSetBlockingCardReason")
  void whenBeforeSetBlockingCardReason_shouldIsValidBlockingCardReason(
      String blockingReason, boolean expected) {
    assertEquals(
        expected,
        validationService.isValidBlockingCardReasonBeforeSetBlockingCardReason(blockingReason));
  }

  @ParameterizedTest
  @MethodSource({"provideCardsToCheckIfExpired"})
  void whenIsCardExpired_shouldSuccess(CardEntity input) {
    when(timeProvider.getCurrentZonedDateTime()).thenReturn(ZonedDateTime.now());
    assertFalse(validationService.isCardExpired(input));
  }

  @ParameterizedTest
  @MethodSource("provideExpiredCardsToCheckIfExpired")
  void whenIsCardExpired_shouldSuccessExpiredDates(CardEntity input) {
    when(timeProvider.getCurrentZonedDateTime()).thenReturn(ZonedDateTime.now());
    assertTrue(validationService.isCardExpired(input));
  }
}

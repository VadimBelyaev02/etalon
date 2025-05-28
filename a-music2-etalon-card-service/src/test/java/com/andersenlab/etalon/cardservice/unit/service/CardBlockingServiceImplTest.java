package com.andersenlab.etalon.cardservice.unit.service;

import static com.andersenlab.etalon.cardservice.exception.BusinessException.NOT_FOUND_CARD_BY_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.andersenlab.etalon.cardservice.MockData;
import com.andersenlab.etalon.cardservice.client.AccountServiceClient;
import com.andersenlab.etalon.cardservice.dto.card.request.ChangeCardStatusRequestDto;
import com.andersenlab.etalon.cardservice.dto.card.response.CardBlockingReasonResponseDto;
import com.andersenlab.etalon.cardservice.entity.CardEntity;
import com.andersenlab.etalon.cardservice.exception.BusinessException;
import com.andersenlab.etalon.cardservice.repository.CardRepository;
import com.andersenlab.etalon.cardservice.service.CardService;
import com.andersenlab.etalon.cardservice.service.ValidationService;
import com.andersenlab.etalon.cardservice.service.impl.CardBlockingServiceImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class CardBlockingServiceImplTest {

  private static final Long CARD_ID = 1L;
  private static final String USER_ID = "user";
  private static final String REASON = "DAMAGED";
  private CardEntity card;
  private ChangeCardStatusRequestDto request;
  private List<CardBlockingReasonResponseDto> expected;
  @Mock private CardRepository cardRepository;
  @Mock private ValidationService validationService;
  @Mock private CardService cardService;
  @Mock private AccountServiceClient accountServiceClient;
  @InjectMocks private CardBlockingServiceImpl underTest;

  @BeforeEach
  void setUp() {
    card = MockData.getValidCardEntity();
    request = MockData.getValidBlockChangeCardStatusRequestDto();
    expected = MockData.getValidListCardBlockingReasonResponseDto();
  }

  @Test
  void whenGetReasonsCardBlocking_thenReturnListCardBlockingReasons() {

    List<CardBlockingReasonResponseDto> result = underTest.getReasonsCardBlocking();

    assertEquals(expected, result);
    assertEquals(expected.get(0).id(), result.get(0).id());
    assertEquals(expected.get(1).reason(), result.get(1).reason());
    assertEquals(expected.get(2).description(), result.get(2).description());
  }

  @Test
  void whenBlockUserCard_shouldSuccess() {
    // given
    when(cardRepository.findByIdAndUserId(CARD_ID, USER_ID)).thenReturn(Optional.of(card));
    when(validationService.isValidCardBeforeChangeCardStatus(card, USER_ID)).thenReturn(true);
    when(validationService.isValidBlockingCardReasonBeforeSetBlockingCardReason(REASON))
        .thenReturn(true);

    // when
    underTest.changeUserCardBlockingStatus(request, USER_ID);

    // then
    verify(cardRepository, times(1)).findByIdAndUserId(CARD_ID, USER_ID);
    verify(cardRepository, times(1)).save(card);
    verify(validationService, times(1)).isValidCardBeforeChangeCardStatus(card, USER_ID);
    verify(validationService, times(1))
        .isValidBlockingCardReasonBeforeSetBlockingCardReason(REASON);
  }

  @Test
  void whenBlockUserCard_thenCardNotFoundReturnBusinessException() {
    // given
    when(cardRepository.findByIdAndUserId(CARD_ID, USER_ID))
        .thenThrow(
            new BusinessException(
                HttpStatus.NOT_FOUND, String.format(NOT_FOUND_CARD_BY_ID, card.getId())));

    // then
    assertThrows(
        BusinessException.class, () -> underTest.changeUserCardBlockingStatus(request, USER_ID));
    verify(cardRepository, times(1)).findByIdAndUserId(CARD_ID, USER_ID);
    verify(cardRepository, never()).save(card);
  }

  @Test
  void whenBlockUserCard_thenNotValidDataReturnBusinessException() {
    // given
    when(cardRepository.findByIdAndUserId(CARD_ID, USER_ID)).thenReturn(Optional.of(card));
    when(validationService.isValidCardBeforeChangeCardStatus(card, USER_ID)).thenReturn(false);

    // then
    assertThrows(
        BusinessException.class, () -> underTest.changeUserCardBlockingStatus(request, USER_ID));
    verify(cardRepository, times(1)).findByIdAndUserId(CARD_ID, USER_ID);
    verify(cardRepository, never()).save(card);
  }
}

package com.andersenlab.etalon.cardservice.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.andersenlab.etalon.cardservice.MockData;
import com.andersenlab.etalon.cardservice.client.AccountServiceClient;
import com.andersenlab.etalon.cardservice.dto.account.response.AccountBalanceResponseDto;
import com.andersenlab.etalon.cardservice.dto.account.response.AccountDetailedResponseDto;
import com.andersenlab.etalon.cardservice.dto.card.request.CardDetailsRequestDto;
import com.andersenlab.etalon.cardservice.dto.card.request.RequestFilterDto;
import com.andersenlab.etalon.cardservice.dto.card.response.CardDetailedResponseDto;
import com.andersenlab.etalon.cardservice.dto.card.response.CardResponseDto;
import com.andersenlab.etalon.cardservice.dto.card.response.ShortCardInfoDto;
import com.andersenlab.etalon.cardservice.entity.CardEntity;
import com.andersenlab.etalon.cardservice.exception.BusinessException;
import com.andersenlab.etalon.cardservice.mapper.CardMapper;
import com.andersenlab.etalon.cardservice.repository.CardRepository;
import com.andersenlab.etalon.cardservice.service.impl.CardServiceImpl;
import com.andersenlab.etalon.cardservice.util.enums.CardStatus;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

  public static final Long CARD_ID = 1L;
  private static final String USER_ID = "user";
  private List<CardResponseDto> expectedListOfCards;
  private RequestFilterDto expectedRequestFilterDto;
  private CardEntity expectedCardEntityFromRepository;
  private CardDetailedResponseDto expectedCardDetailedDto;
  private CardEntity cardEntity;
  private CardDetailsRequestDto cardDetailsRequestDto;
  private ShortCardInfoDto shortCardInfoDto;
  private AccountBalanceResponseDto accountBalanceResponseDto;
  private AccountDetailedResponseDto accountIbanAndCurrencyResponseDto;

  @Mock private CardRepository cardRepository;
  @Mock private AccountServiceClient accountServiceClient;
  @Spy private CardMapper mapper = Mappers.getMapper(CardMapper.class);
  @InjectMocks private CardServiceImpl underTest;

  @BeforeEach
  void setUp() {
    expectedRequestFilterDto = MockData.getValidRequestFilterDto();
    expectedListOfCards = List.of(MockData.getValidCardResponseDto());
    expectedCardEntityFromRepository = MockData.getValidCardEntity();
    expectedCardDetailedDto = MockData.getValidCardDetailedResponseDto();
    cardDetailsRequestDto = MockData.getValidCardDetailsRequestDto();
    shortCardInfoDto = MockData.getValidShortCardInfoDto();
    accountIbanAndCurrencyResponseDto = MockData.getValidAccountDetailedResponseDto();
    cardEntity = MockData.getValidCardEntity();
    accountBalanceResponseDto =
        AccountBalanceResponseDto.builder().accountBalance(BigDecimal.valueOf(2.14)).build();
  }

  @Test
  void whenGetActiveUserCardByAccountNumber_thenReturnCardResponse() {
    CardEntity mockCard = new CardEntity();
    CardResponseDto mockDto = CardResponseDto.builder().build();

    when(cardRepository.findByAccountNumberAndStatus("testNumber", CardStatus.ACTIVE))
        .thenReturn(mockCard);
    when(mapper.toDto(mockCard)).thenReturn(mockDto);

    CardResponseDto result = underTest.getActiveUserCardByAccountNumber("testNumber");

    assertNotNull(result);
    assertEquals(mockDto, result);

    verify(cardRepository, times(1)).findByAccountNumberAndStatus("testNumber", CardStatus.ACTIVE);
    verify(mapper, times(1)).toDto(mockCard);
  }

  @Test
  void whenGivenUserHaveNoCardsByFilters_thenReturnEmptyArray() {
    ArgumentCaptor<Specification> specificationsCaptor =
        ArgumentCaptor.forClass(Specification.class);
    given(cardRepository.findAll(specificationsCaptor.capture(), any(Sort.class)))
        .willReturn(new ArrayList<>());

    List<CardResponseDto> result =
        underTest.findAllCardsByUserIdAndFilter(USER_ID, expectedRequestFilterDto);

    then(cardRepository).should(times(1)).findAll(specificationsCaptor.capture(), any(Sort.class));

    assertEquals(new ArrayList<>(), result);
  }

  @Test
  void whenGivenUserHaveCardsByFilters_thenReturnListOfCards() {
    ArgumentCaptor<Specification> specificationsCaptor =
        ArgumentCaptor.forClass(Specification.class);
    given(cardRepository.findAll(specificationsCaptor.capture(), any(Sort.class)))
        .willReturn(List.of(expectedCardEntityFromRepository));
    given(accountServiceClient.getAccountBalanceByAccountNumber(cardEntity.getAccountNumber()))
        .willReturn(accountBalanceResponseDto);
    given(accountServiceClient.getDetailedAccountInfoInternalCall(cardEntity.getAccountNumber()))
        .willReturn(accountIbanAndCurrencyResponseDto);

    List<CardResponseDto> result =
        underTest.findAllCardsByUserIdAndFilter(USER_ID, expectedRequestFilterDto);

    then(cardRepository).should(times(1)).findAll(specificationsCaptor.capture(), any(Sort.class));

    assertEquals(expectedListOfCards, result);
  }

  @Test
  void whenUserCardFindByIdAndUserIdExists_thenReturnCardDetailedInfo() {
    given(cardRepository.findByIdAndUserId(anyLong(), anyString()))
        .willReturn(Optional.ofNullable(expectedCardEntityFromRepository));
    given(accountServiceClient.getDetailedAccountInfoInternalCall(cardEntity.getAccountNumber()))
        .willReturn(accountIbanAndCurrencyResponseDto);

    CardDetailedResponseDto result = underTest.findCardById(1L, USER_ID);

    assertEquals(expectedCardDetailedDto, result);
  }

  @Test()
  void whenUserCardFindByIdAndUserIdNotExists_thenThrowException() {
    given(cardRepository.findByIdAndUserId(anyLong(), anyString())).willReturn(Optional.empty());

    assertThrows(BusinessException.class, () -> underTest.findCardById(1, USER_ID));
  }

  @Test
  void whenUpdateCardDetails_shouldSuccess() {
    when(cardRepository.findByIdAndUserId(CARD_ID, USER_ID)).thenReturn(Optional.of(cardEntity));

    underTest.updateCardDetails(CARD_ID, cardDetailsRequestDto, USER_ID);

    verify(cardRepository, times(1)).findByIdAndUserId(CARD_ID, USER_ID);
    verify(cardRepository, times(1)).save(cardEntity);
  }

  @Test
  void whenUpdateCardDetails_shouldFail() {
    when(cardRepository.findByIdAndUserId(CARD_ID, USER_ID))
        .thenThrow(
            new BusinessException(
                HttpStatus.NOT_FOUND,
                String.format(BusinessException.NOT_FOUND_CARD_BY_ID, CARD_ID)));

    assertThrows(
        BusinessException.class,
        () -> underTest.updateCardDetails(CARD_ID, cardDetailsRequestDto, USER_ID));
    verify(cardRepository, times(1)).findByIdAndUserId(CARD_ID, USER_ID);
    verify(cardRepository, never()).save(cardEntity);
  }

  @Test
  void whenUserCardFindByNumberExists_thenReturnCardDetailedInfo() {
    given(cardRepository.findCardEntityByNumber(anyString()))
        .willReturn(Optional.ofNullable(expectedCardEntityFromRepository));
    given(accountServiceClient.getDetailedAccountInfoInternalCall(cardEntity.getAccountNumber()))
        .willReturn(accountIbanAndCurrencyResponseDto);

    CardDetailedResponseDto result = underTest.findCardByNumber("4200000000000000");

    assertEquals(expectedCardDetailedDto, result);
  }

  @Test
  void whenUserCardFindByIdsExists_thenReturnShortCardInfo() {
    List<CardEntity> cardEntities = List.of(cardEntity);

    when(cardRepository.findAllByIdIn(anyList())).thenReturn(cardEntities);
    when(mapper.toShortCardDto(cardEntity)).thenReturn(shortCardInfoDto);

    List<ShortCardInfoDto> result = underTest.findCardsInfoByCardIds(List.of(1L));

    assertNotNull(result);
    assertEquals("Enjoy", result.get(0).cardProductName());
    assertEquals("*0019", result.get(0).maskedCardNumber());
    assertEquals(1L, result.get(0).id());

    verify(mapper).toShortCardDto(cardEntity);
  }

  @Test()
  void whenUserCardFindByNumberNotExists_thenThrowException() {
    given(cardRepository.findCardEntityByNumber(anyString())).willReturn(Optional.empty());

    assertThrows(BusinessException.class, () -> underTest.findCardByNumber("InvalidCardNumber"));
  }
}

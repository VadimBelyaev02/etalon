package com.andersenlab.etalon.cardservice.service.impl;

import static com.andersenlab.etalon.cardservice.exception.BusinessException.AVAILABLE_CURRENCIES_NOT_FOUND;
import static com.andersenlab.etalon.cardservice.exception.BusinessException.INVALID_DAILY_EXPENSE_LIMIT;
import static com.andersenlab.etalon.cardservice.exception.BusinessException.INVALID_TRANSFER_LIMIT;
import static com.andersenlab.etalon.cardservice.exception.BusinessException.INVALID_WITHDRAW_LIMIT;
import static com.andersenlab.etalon.cardservice.repository.specification.CardSpecification.filterByAccountNumber;
import static com.andersenlab.etalon.cardservice.repository.specification.CardSpecification.filterByIssuer;
import static com.andersenlab.etalon.cardservice.repository.specification.CardSpecification.filterByProductType;
import static com.andersenlab.etalon.cardservice.repository.specification.CardSpecification.filterByStatus;
import static com.andersenlab.etalon.cardservice.repository.specification.CardSpecification.filterByUserId;
import static com.andersenlab.etalon.cardservice.util.Constants.CREATED_AT;

import com.andersenlab.etalon.cardservice.client.AccountServiceClient;
import com.andersenlab.etalon.cardservice.dto.account.response.AccountDetailedResponseDto;
import com.andersenlab.etalon.cardservice.dto.card.request.CardDetailsRequestDto;
import com.andersenlab.etalon.cardservice.dto.card.request.RequestFilterDto;
import com.andersenlab.etalon.cardservice.dto.card.response.CardDetailedResponseDto;
import com.andersenlab.etalon.cardservice.dto.card.response.CardResponseDto;
import com.andersenlab.etalon.cardservice.dto.card.response.ShortCardInfoDto;
import com.andersenlab.etalon.cardservice.entity.CardEntity;
import com.andersenlab.etalon.cardservice.entity.CardProductEntity;
import com.andersenlab.etalon.cardservice.entity.CurrencyEntity;
import com.andersenlab.etalon.cardservice.exception.BusinessException;
import com.andersenlab.etalon.cardservice.mapper.CardMapper;
import com.andersenlab.etalon.cardservice.repository.CardRepository;
import com.andersenlab.etalon.cardservice.service.CardService;
import com.andersenlab.etalon.cardservice.util.PatchUtils;
import com.andersenlab.etalon.cardservice.util.enums.CardStatus;
import com.andersenlab.etalon.cardservice.util.enums.Currency;
import com.andersenlab.etalon.cardservice.util.enums.CurrencyLimit;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

  private final CardRepository cardRepository;
  private final CardMapper cardMapper;
  private final AccountServiceClient accountServiceClient;

  @Override
  public CardResponseDto getActiveUserCardByAccountNumber(String accountNumber) {
    return cardMapper.toDto(
        cardRepository.findByAccountNumberAndStatus(accountNumber, CardStatus.ACTIVE));
  }

  @Override
  public List<CardResponseDto> findAllCardsByUserIdAndFilter(
      String userId, RequestFilterDto filters) {

    Specification<CardEntity> filterSpecification =
        Specification.where(filterByUserId(userId))
            .and(filterByIssuer(filters.issuer()))
            .and(filterByProductType(filters.productType()))
            .and(filterByStatus(filters.status()))
            .and(filterByAccountNumber(filters.accountNumber()));
    return updateBalanceFromAccountService(
        mapCardsListToDtoWithAccounts(
            cardRepository.findAll(filterSpecification, Sort.by(Sort.Direction.DESC, CREATED_AT))));
  }

  private List<CardResponseDto> updateBalanceFromAccountService(
      List<CardResponseDto> cardResponseDtoList) {
    return cardResponseDtoList.stream()
        .map(
            cardResponseDto ->
                cardResponseDto.toBuilder()
                    .balance(
                        accountServiceClient
                            .getAccountBalanceByAccountNumber(cardResponseDto.accountNumber())
                            .accountBalance())
                    .build())
        .sorted(
            Comparator.comparing((CardResponseDto card) -> card.status() != CardStatus.ACTIVE)
                .thenComparing(CardResponseDto::status))
        .toList();
  }

  @Override
  public CardDetailedResponseDto findCardById(long cardId, String userId) {

    CardEntity cardEntity =
        cardRepository
            .findByIdAndUserId(cardId, userId)
            .orElseThrow(
                () ->
                    new BusinessException(
                        HttpStatus.NOT_FOUND,
                        String.format(BusinessException.NOT_FOUND_CARD_BY_ID, cardId)));
    AccountDetailedResponseDto account =
        accountServiceClient.getDetailedAccountInfoInternalCall(cardEntity.getAccountNumber());
    return cardMapper.mapToDetailedDtoWithAccountData(cardEntity, account);
  }

  @Override
  public void updateCardDetails(
      Long id, CardDetailsRequestDto cardDetailsRequestDto, String userId) {

    CardEntity cardEntity =
        cardRepository
            .findByIdAndUserId(id, userId)
            .orElseThrow(
                () ->
                    new BusinessException(
                        HttpStatus.NOT_FOUND,
                        String.format(BusinessException.NOT_FOUND_CARD_BY_ID, id)));

    if (cardEntity.getIsBlocked()) {
      throw new BusinessException(HttpStatus.BAD_REQUEST, BusinessException.CARD_IS_BLOCKED);
    }

    CardProductEntity cardProduct = cardEntity.getProduct();
    List<CurrencyEntity> availableCurrencies = cardProduct.getAvailableCurrencies();
    Currency currencyCode = getCurrencyCode(availableCurrencies);

    processCurrencyLimits(currencyCode, cardDetailsRequestDto);

    PatchUtils.updateIfPresent(cardEntity::setWithdrawLimit, cardDetailsRequestDto.withdrawLimit());
    PatchUtils.updateIfPresent(cardEntity::setTransferLimit, cardDetailsRequestDto.transferLimit());
    PatchUtils.updateIfPresent(
        cardEntity::setDailyExpenseLimit, cardDetailsRequestDto.dailyExpenseLimit());
    cardRepository.save(cardEntity);
  }

  private Currency getCurrencyCode(List<CurrencyEntity> availableCurrencies) {
    if (CollectionUtils.isEmpty(availableCurrencies)) {
      throw new BusinessException(HttpStatus.NOT_FOUND, AVAILABLE_CURRENCIES_NOT_FOUND);
    }
    return availableCurrencies.get(0).getCurrencyCode();
  }

  private void processCurrencyLimits(
      Currency currency, CardDetailsRequestDto cardDetailsRequestDto) {
    BigDecimal withdrawLimit = CurrencyLimit.PLN_WITHDRAW_LIMIT.getLimitValue();
    BigDecimal transferLimit = CurrencyLimit.PLN_TRANSFER_LIMIT.getLimitValue();
    BigDecimal dailyExpenseLimit = CurrencyLimit.PLN_DAILY_EXPENSE_LIMIT.getLimitValue();

    if (Currency.EUR.equals(currency) || Currency.USD.equals(currency)) {
      withdrawLimit = CurrencyLimit.EUR_USD_WITHDRAW_LIMIT.getLimitValue();
      transferLimit = CurrencyLimit.EUR_USD_TRANSFER_LIMIT.getLimitValue();
      dailyExpenseLimit = CurrencyLimit.EUR_USD_DAILY_EXPENSE_LIMIT.getLimitValue();
    }

    validateLimit(
        withdrawLimit.compareTo(cardDetailsRequestDto.withdrawLimit()), INVALID_WITHDRAW_LIMIT);

    validateLimit(
        transferLimit.compareTo(cardDetailsRequestDto.transferLimit()), INVALID_TRANSFER_LIMIT);

    validateLimit(
        dailyExpenseLimit.compareTo(cardDetailsRequestDto.dailyExpenseLimit()),
        INVALID_DAILY_EXPENSE_LIMIT);
  }

  private void validateLimit(Integer result, String exceptionMessage) {
    if (result < 0) {
      throw new BusinessException(HttpStatus.BAD_REQUEST, exceptionMessage);
    }
  }

  @Override
  public CardDetailedResponseDto findCardByNumber(String cardNumber) {
    CardEntity cardEntity =
        cardRepository
            .findCardEntityByNumber(cardNumber)
            .orElseThrow(
                () ->
                    new BusinessException(
                        HttpStatus.NOT_FOUND,
                        String.format(BusinessException.NOT_FOUND_CARD_BY_NUMBER, cardNumber)));
    AccountDetailedResponseDto account =
        accountServiceClient.getDetailedAccountInfoInternalCall(cardEntity.getAccountNumber());
    return cardMapper.mapToDetailedDtoWithAccountData(cardEntity, account);
  }

  private List<CardResponseDto> mapCardsListToDtoWithAccounts(List<CardEntity> cardEntities) {
    return cardEntities.stream().map(this::mapCardToDtoWithAccountData).toList();
  }

  private CardResponseDto mapCardToDtoWithAccountData(CardEntity cardEntity) {
    AccountDetailedResponseDto account =
        accountServiceClient.getDetailedAccountInfoInternalCall(cardEntity.getAccountNumber());
    return cardMapper.mapToDtoWithAccountData(cardEntity, account);
  }

  public List<ShortCardInfoDto> findCardsInfoByCardIds(List<Long> cardIds) {
    List<CardEntity> cards = cardRepository.findAllByIdIn(cardIds);
    return cards.stream().map(cardMapper::toShortCardDto).collect(Collectors.toList());
  }
}

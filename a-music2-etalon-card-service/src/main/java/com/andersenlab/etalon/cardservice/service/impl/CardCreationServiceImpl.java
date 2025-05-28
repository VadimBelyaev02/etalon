package com.andersenlab.etalon.cardservice.service.impl;

import com.andersenlab.etalon.cardservice.client.AccountServiceClient;
import com.andersenlab.etalon.cardservice.client.InfoServiceClient;
import com.andersenlab.etalon.cardservice.client.UserServiceClient;
import com.andersenlab.etalon.cardservice.config.TimeProvider;
import com.andersenlab.etalon.cardservice.dto.account.request.AccountCreationRequestDto;
import com.andersenlab.etalon.cardservice.dto.account.response.AccountDetailedResponseDto;
import com.andersenlab.etalon.cardservice.dto.account.response.AccountResponseDto;
import com.andersenlab.etalon.cardservice.dto.card.request.CardCreationRequestDto;
import com.andersenlab.etalon.cardservice.dto.card.response.CardDetailedResponseDto;
import com.andersenlab.etalon.cardservice.dto.card.response.CardProductResponseDto;
import com.andersenlab.etalon.cardservice.dto.info.response.BankBranchesResponseDto;
import com.andersenlab.etalon.cardservice.dto.user.response.UserDataResponseDto;
import com.andersenlab.etalon.cardservice.entity.CardEntity;
import com.andersenlab.etalon.cardservice.entity.CurrencyLimitEntity;
import com.andersenlab.etalon.cardservice.exception.BusinessException;
import com.andersenlab.etalon.cardservice.mapper.CardMapper;
import com.andersenlab.etalon.cardservice.mapper.ProductMapper;
import com.andersenlab.etalon.cardservice.repository.CardRepository;
import com.andersenlab.etalon.cardservice.repository.CurrencyLimitRepository;
import com.andersenlab.etalon.cardservice.service.CardCreationService;
import com.andersenlab.etalon.cardservice.service.CardProductService;
import com.andersenlab.etalon.cardservice.service.GeneratorService;
import com.andersenlab.etalon.cardservice.service.ValidationService;
import com.andersenlab.etalon.cardservice.util.enums.AccountType;
import com.andersenlab.etalon.cardservice.util.enums.CardStatus;
import com.andersenlab.etalon.cardservice.util.enums.Currency;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardCreationServiceImpl implements CardCreationService {

  private final CardRepository cardRepository;
  private final CardProductService cardProductService;
  private final CardMapper cardMapper;
  private final ProductMapper productMapper;
  private final GeneratorService generator;
  private final TimeProvider timeProvider;
  private final UserServiceClient userServiceClient;
  private final AccountServiceClient accountServiceClient;
  private final InfoServiceClient infoFeignClient;
  private final CurrencyLimitRepository currencyLimitRepository;
  private final ValidationService validationService;

  @Override
  public CardDetailedResponseDto createUserCard(
      CardCreationRequestDto cardCreation, String userId) {
    final BankBranchesResponseDto branch =
        infoFeignClient.getBankBranch(cardCreation.bankBranchId());
    final UserDataResponseDto user = userServiceClient.getUserData(userId);
    CardProductResponseDto cardProduct =
        cardProductService.getCardProductById(cardCreation.cardProductId());
    Long cardId = cardRepository.findFirstByOrderByIdDesc().map(CardEntity::getId).orElse(null);
    Currency requestedCurrency = getCurrencyFromRequestIfExist(cardCreation);
    isCurrencyAvailableForProduct(cardProduct, requestedCurrency);
    CurrencyLimitEntity currencyLimit = getDefaultCurrencyLimits(requestedCurrency);

    AccountResponseDto account =
        accountServiceClient.createAccount(
            new AccountCreationRequestDto(
                userId, String.valueOf(AccountType.CARD), requestedCurrency));

    CardEntity newCard =
        buildCardEntity(user, cardProduct, account, cardId, branch.id(), currencyLimit);
    CardEntity newCardCreated = cardRepository.save(newCard);
    return cardMapper.mapToDetailedDtoWithAccountData(newCardCreated, account);
  }

  @Override
  public CardDetailedResponseDto reissueCard(CardEntity cardEntity, String userId) {
    Long cardId = cardRepository.findFirstByOrderByIdDesc().map(CardEntity::getId).orElse(null);
    AccountDetailedResponseDto accountDetailedResponseDto =
        accountServiceClient.getDetailedAccountInfoInternalCall(cardEntity.getAccountNumber());
    validateAccount(accountDetailedResponseDto, userId);
    CardEntity newCard =
        new CardEntity()
            .toBuilder()
                .userId(userId)
                .expirationDate(calculateExpirationDate(cardEntity.getProduct().getValidity()))
                .number(generator.generateCardNumber(cardEntity.getProduct().getIssuer(), cardId))
                .accountNumber(cardEntity.getAccountNumber())
                .isBlocked(false)
                .status(CardStatus.ACTIVE)
                .cardholderName(cardEntity.getCardholderName())
                .cvv(generator.generateCVV())
                .withdrawLimit(cardEntity.getWithdrawLimit())
                .transferLimit(cardEntity.getTransferLimit())
                .dailyExpenseLimit(cardEntity.getDailyExpenseLimit())
                .product(cardEntity.getProduct())
                .bankBranchId(cardEntity.getBankBranchId())
                .build();
    CardEntity newCardCreated = cardRepository.save(newCard);
    return cardMapper.mapToDetailedDtoWithAccountData(newCardCreated, accountDetailedResponseDto);
  }

  private void validateAccount(
      AccountDetailedResponseDto accountDetailedResponseDto, String userId) {
    validationService.validateAccountToCurrentUserLinked(accountDetailedResponseDto, userId);
    validationService.validateAccountToUnblockState(accountDetailedResponseDto, userId);
  }

  private CurrencyLimitEntity getDefaultCurrencyLimits(Currency requestedCurrency) {
    return currencyLimitRepository
        .findById(requestedCurrency)
        .orElseThrow(
            () ->
                new BusinessException(
                    HttpStatus.NOT_FOUND, BusinessException.CURRENCY_LIMITS_NOT_FOUND));
  }

  private ZonedDateTime calculateExpirationDate(Integer validity) {
    return timeProvider
        .getCurrentZonedDateTime()
        .plusYears(validity)
        .with(TemporalAdjusters.lastDayOfMonth());
  }

  private Currency getCurrencyFromRequestIfExist(CardCreationRequestDto cardCreation) {
    if (cardCreation.currencies().isEmpty()) {
      throw new BusinessException(
          HttpStatus.NOT_FOUND, BusinessException.CARD_CURRENCY_WAS_NOT_FOUND_IN_REQUEST);
    }
    return cardCreation.currencies().get(0);
  }

  private void isCurrencyAvailableForProduct(
      CardProductResponseDto cardProduct, Currency requestedCurrency) {
    List<Currency> availableCurrencies = cardProduct.availableCurrencies();
    if (!availableCurrencies.contains(requestedCurrency)) {
      throw new BusinessException(
          HttpStatus.NOT_ACCEPTABLE, String.format(BusinessException.CURRENCIES_DOESNT_MATCH));
    }
  }

  private CardEntity buildCardEntity(
      UserDataResponseDto user,
      CardProductResponseDto cardProduct,
      AccountResponseDto account,
      Long cardId,
      Long branchId,
      CurrencyLimitEntity currencyLimit) {
    return new CardEntity()
        .toBuilder()
            .userId(user.id())
            .expirationDate(calculateExpirationDate(cardProduct.validity()))
            .number(generator.generateCardNumber(cardProduct.issuer(), cardId))
            .accountNumber(account.iban())
            .isBlocked(false)
            .status(CardStatus.ACTIVE)
            .cardholderName(user.firstName().concat(" ").concat(user.lastName()).toUpperCase())
            .cvv(generator.generateCVV())
            .withdrawLimit(currencyLimit.getWithdrawLimit())
            .transferLimit(currencyLimit.getTransferLimit())
            .dailyExpenseLimit(currencyLimit.getDailyExpenseLimit())
            .product(productMapper.toEntity(cardProduct))
            .bankBranchId(branchId)
            .build();
  }
}

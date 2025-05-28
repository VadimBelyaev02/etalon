package com.andersenlab.etalon.infoservice.service.impl;

import static com.andersenlab.etalon.infoservice.util.BankIdentifiersUtils.getBankCodeFromIban;
import static com.andersenlab.etalon.infoservice.util.BankIdentifiersUtils.getBinFromCardNumber;
import static com.andersenlab.etalon.infoservice.util.Constants.ETALON_BANK_CODE;
import static com.andersenlab.etalon.infoservice.util.Constants.ETALON_BIN_MASTERCARD;
import static com.andersenlab.etalon.infoservice.util.Constants.ETALON_BIN_VISA;

import com.andersenlab.etalon.infoservice.dto.request.BankInfoRequestDto;
import com.andersenlab.etalon.infoservice.dto.response.BankInfoResponseDto;
import com.andersenlab.etalon.infoservice.entity.BankInfoEntity;
import com.andersenlab.etalon.infoservice.exception.BusinessException;
import com.andersenlab.etalon.infoservice.mapper.BankInfoAndDetailsMapper;
import com.andersenlab.etalon.infoservice.repository.BankInfoRepository;
import com.andersenlab.etalon.infoservice.service.BankInfoService;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BankInfoServiceImpl implements BankInfoService {
  private final BankInfoRepository bankInfoRepository;
  private final BankInfoAndDetailsMapper bankInfoAndDetailsMapper;

  @Override
  public BankInfoResponseDto getBankInfoBySelectedOption(BankInfoRequestDto bankInfoRequestDto) {
    if (Objects.nonNull(bankInfoRequestDto.iban())) {
      return getBankInfoByIban(bankInfoRequestDto.iban());
    }
    if (Objects.nonNull(bankInfoRequestDto.cardNumber())) {
      return getBankInfoByCardNumber(bankInfoRequestDto.cardNumber());
    }
    throw new BusinessException(HttpStatus.BAD_REQUEST, BusinessException.INFORMATION_NOT_PROVIDED);
  }

  @Override
  public BankInfoResponseDto getBankInfoByCardNumber(String cardNumber) {
    String bin = getBinFromCardNumber(cardNumber);
    if (bin.equals(ETALON_BIN_VISA) || bin.equals(ETALON_BIN_MASTERCARD)) {
      return BankInfoResponseDto.builder().isForeignBank(false).build();
    }
    BankInfoEntity bankInfoEntity =
        bankInfoRepository
            .getBankInfoEntityByBankDetails_Bin(bin)
            .orElseThrow(
                () ->
                    new BusinessException(
                        HttpStatus.NOT_FOUND,
                        BusinessException.BANK_NOT_FOUND_BY_BIN.formatted(bin)));
    return bankInfoAndDetailsMapper.toBankInfoResponseDto(bankInfoEntity);
  }

  @Override
  public BankInfoResponseDto getBankInfoByIban(String iban) {
    String bankCode = getBankCodeFromIban(iban);
    if (bankCode.equals(ETALON_BANK_CODE)) {
      return BankInfoResponseDto.builder().isForeignBank(false).build();
    }
    BankInfoEntity bankInfoEntity =
        bankInfoRepository
            .getBankInfoEntityByBankDetails_BankCode(bankCode)
            .orElseThrow(
                () ->
                    new BusinessException(
                        HttpStatus.NOT_FOUND,
                        BusinessException.BANK_NOT_FOUND_BY_BANK_CODE.formatted(bankCode)));
    return bankInfoAndDetailsMapper.toBankInfoResponseDto(bankInfoEntity);
  }
}

package com.andersenlab.etalon.infoservice.service;

import com.andersenlab.etalon.infoservice.dto.request.BankInfoRequestDto;
import com.andersenlab.etalon.infoservice.dto.response.BankInfoResponseDto;

public interface BankInfoService {
  BankInfoResponseDto getBankInfoBySelectedOption(BankInfoRequestDto bankInfoRequestDto);

  BankInfoResponseDto getBankInfoByCardNumber(String cardNumber);

  BankInfoResponseDto getBankInfoByIban(String iban);
}

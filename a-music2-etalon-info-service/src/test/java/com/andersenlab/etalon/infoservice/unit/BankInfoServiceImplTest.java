package com.andersenlab.etalon.infoservice.unit;

import static com.andersenlab.etalon.infoservice.MockData.getValidBankInfoEntity;
import static com.andersenlab.etalon.infoservice.MockData.getValidBankInfoResponseDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.andersenlab.etalon.infoservice.dto.response.BankInfoResponseDto;
import com.andersenlab.etalon.infoservice.entity.BankInfoEntity;
import com.andersenlab.etalon.infoservice.exception.BusinessException;
import com.andersenlab.etalon.infoservice.mapper.BankInfoAndDetailsMapper;
import com.andersenlab.etalon.infoservice.repository.BankInfoRepository;
import com.andersenlab.etalon.infoservice.service.impl.BankInfoServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BankInfoServiceImplTest {
  public static final String VALID_CARD_NUMBER = "4150461111111111";
  public static final String INVALID_CARD_NUMBER = "12345678";
  public static final String VALID_IBAN = "PL10315046000000000000000000";
  public static final String INVALID_IBAN = "PL1234";

  @Mock BankInfoRepository repository;
  BankInfoAndDetailsMapper mapper = Mappers.getMapper(BankInfoAndDetailsMapper.class);
  BankInfoServiceImpl target;

  @BeforeEach
  void setUp() {
    target = new BankInfoServiceImpl(repository, mapper);
  }

  @Test
  void whenGetBankInfoWithValidCardNumber_shouldSuccess() {
    // when
    BankInfoEntity entity = getValidBankInfoEntity();
    BankInfoResponseDto expected = getValidBankInfoResponseDto();
    String validBin = entity.getBankDetails().get(0).getBin();
    when(repository.getBankInfoEntityByBankDetails_Bin(validBin)).thenReturn(Optional.of(entity));

    // then
    BankInfoResponseDto actual = target.getBankInfoByCardNumber(VALID_CARD_NUMBER);
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void whenGetBankInfoWithValidIban_shouldSuccess() {
    // when
    BankInfoEntity entity = getValidBankInfoEntity();
    BankInfoResponseDto expected = getValidBankInfoResponseDto();
    String validBankCode = entity.getBankDetails().get(0).getBankCode();
    when(repository.getBankInfoEntityByBankDetails_BankCode(validBankCode))
        .thenReturn(Optional.of(entity));

    // then
    BankInfoResponseDto actual = target.getBankInfoByIban(VALID_IBAN);
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void whenGetBankInfoWithNotExistingIban_shouldThrowBusinessException() {

    assertThatThrownBy(() -> target.getBankInfoByIban(VALID_IBAN))
        .isInstanceOf(BusinessException.class);
  }

  @Test
  void whenGetBankInfoWithNotExistingCardNumber_shouldThrowBusinessException() {

    assertThatThrownBy(() -> target.getBankInfoByCardNumber(VALID_CARD_NUMBER))
        .isInstanceOf(BusinessException.class);
  }
}

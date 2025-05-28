package com.andersenlab.etalon.infoservice.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.andersenlab.etalon.infoservice.MockData;
import com.andersenlab.etalon.infoservice.dto.response.BankContactsResponseDto;
import com.andersenlab.etalon.infoservice.entity.BankContactsEntity;
import com.andersenlab.etalon.infoservice.mapper.BankContactsMapper;
import com.andersenlab.etalon.infoservice.repository.BankContactsRepository;
import com.andersenlab.etalon.infoservice.repository.LocaleRepository;
import com.andersenlab.etalon.infoservice.service.impl.BankContactsServiceImpl;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BankContactsServiceImplTest {

  @Mock private BankContactsRepository bankContactsRepository;

  @Mock private LocaleRepository localeRepository;

  @Mock private BankContactsMapper bankContactsMapper;

  @InjectMocks private BankContactsServiceImpl underTest;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(underTest, "defaultLocale", "pl");
  }

  @AfterEach
  void tearDown() {
    LocaleContextHolder.resetLocaleContext();
  }

  @Test
  void getAllContacts_plLocale() {
    // given
    BankContactsEntity bankContacts = MockData.getValidBankContactsEntity();
    when(bankContactsRepository.findFirstByOrderByIdAsc()).thenReturn(bankContacts);

    when(localeRepository.findAllCodes()).thenReturn(List.of("pl", "en"));

    BankContactsResponseDto plResponse =
        new BankContactsResponseDto(
            bankContacts.getId(),
            bankContacts.getBankName(),
            "ul. Jana Pawła, 10, 00-854 (PL)",
            bankContacts.getNip(),
            bankContacts.getRegon(),
            bankContacts.getSwiftCode(),
            bankContacts.getEmail(),
            bankContacts.getPhoneNumber(),
            null);
    when(bankContactsMapper.toDto(bankContacts, "pl")).thenReturn(plResponse);

    // when
    LocaleContextHolder.setLocale(Locale.forLanguageTag("pl"));
    BankContactsResponseDto result = underTest.getAllContacts();

    // then
    assertEquals("ul. Jana Pawła, 10, 00-854 (PL)", result.address());
    assertEquals(bankContacts.getBankName(), result.bankName());
    assertEquals(bankContacts.getNip(), result.nip());
    assertEquals(bankContacts.getRegon(), result.regon());
    assertEquals(bankContacts.getSwiftCode(), result.swiftCode());
    assertEquals(bankContacts.getEmail(), result.email());
    assertEquals(bankContacts.getPhoneNumber(), result.phoneNumber());

    verify(bankContactsRepository, times(1)).findFirstByOrderByIdAsc();
    verify(localeRepository, times(1)).findAllCodes();
    verify(bankContactsMapper, times(1)).toDto(bankContacts, "pl");
  }

  @Test
  void getAllContacts_enLocale() {
    // given
    BankContactsEntity bankContacts = MockData.getValidBankContactsEntity();
    when(bankContactsRepository.findFirstByOrderByIdAsc()).thenReturn(bankContacts);

    when(localeRepository.findAllCodes()).thenReturn(List.of("pl", "en"));

    BankContactsResponseDto enResponse =
        new BankContactsResponseDto(
            bankContacts.getId(),
            bankContacts.getBankName(),
            "Jana Pawła str., 10, 00-854 (EN)",
            bankContacts.getNip(),
            bankContacts.getRegon(),
            bankContacts.getSwiftCode(),
            bankContacts.getEmail(),
            bankContacts.getPhoneNumber(),
            null);
    when(bankContactsMapper.toDto(bankContacts, "en")).thenReturn(enResponse);

    // when
    LocaleContextHolder.setLocale(Locale.forLanguageTag("en"));
    BankContactsResponseDto result = underTest.getAllContacts();

    // then
    assertEquals("Jana Pawła str., 10, 00-854 (EN)", result.address());
    assertEquals(bankContacts.getBankName(), result.bankName());
    assertEquals(bankContacts.getNip(), result.nip());
    assertEquals(bankContacts.getRegon(), result.regon());
    assertEquals(bankContacts.getSwiftCode(), result.swiftCode());
    assertEquals(bankContacts.getEmail(), result.email());
    assertEquals(bankContacts.getPhoneNumber(), result.phoneNumber());

    verify(bankContactsRepository, times(1)).findFirstByOrderByIdAsc();
    verify(localeRepository, times(1)).findAllCodes();
    verify(bankContactsMapper, times(1)).toDto(bankContacts, "en");
  }

  @Test
  void getAllContacts_unknownLocale_fallbackToDefault() {
    // given
    BankContactsEntity bankContacts = MockData.getValidBankContactsEntity();
    when(bankContactsRepository.findFirstByOrderByIdAsc()).thenReturn(bankContacts);

    when(localeRepository.findAllCodes()).thenReturn(List.of("pl", "en"));

    BankContactsResponseDto fallbackResponse =
        new BankContactsResponseDto(
            bankContacts.getId(),
            bankContacts.getBankName(),
            "ul. Jana Pawła, 10, 00-854 (PL) - fallback",
            bankContacts.getNip(),
            bankContacts.getRegon(),
            bankContacts.getSwiftCode(),
            bankContacts.getEmail(),
            bankContacts.getPhoneNumber(),
            null);
    when(bankContactsMapper.toDto(bankContacts, "pl")).thenReturn(fallbackResponse);

    // when
    LocaleContextHolder.setLocale(Locale.forLanguageTag("de"));
    BankContactsResponseDto result = underTest.getAllContacts();

    // then
    assertEquals("ul. Jana Pawła, 10, 00-854 (PL) - fallback", result.address());
    assertEquals(bankContacts.getBankName(), result.bankName());
    assertEquals(bankContacts.getNip(), result.nip());
    assertEquals(bankContacts.getRegon(), result.regon());
    assertEquals(bankContacts.getSwiftCode(), result.swiftCode());
    assertEquals(bankContacts.getEmail(), result.email());
    assertEquals(bankContacts.getPhoneNumber(), result.phoneNumber());

    verify(bankContactsRepository, times(1)).findFirstByOrderByIdAsc();
    verify(localeRepository, times(1)).findAllCodes();
    verify(bankContactsMapper, times(1)).toDto(bankContacts, "pl");
  }
}

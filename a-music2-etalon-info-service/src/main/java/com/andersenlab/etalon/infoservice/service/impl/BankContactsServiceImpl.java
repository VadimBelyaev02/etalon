package com.andersenlab.etalon.infoservice.service.impl;

import com.andersenlab.etalon.infoservice.dto.response.BankContactsResponseDto;
import com.andersenlab.etalon.infoservice.entity.BankContactsEntity;
import com.andersenlab.etalon.infoservice.mapper.BankContactsMapper;
import com.andersenlab.etalon.infoservice.repository.BankContactsRepository;
import com.andersenlab.etalon.infoservice.repository.LocaleRepository;
import com.andersenlab.etalon.infoservice.service.BankContactsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BankContactsServiceImpl implements BankContactsService {

  private final BankContactsRepository bankContactsRepository;
  private final LocaleRepository localeRepository;
  private final BankContactsMapper bankContactsMapper;

  @Value("${spring.web.locale}")
  private String defaultLocale;

  @Override
  public BankContactsResponseDto getAllContacts() {
    BankContactsEntity entity = bankContactsRepository.findFirstByOrderByIdAsc();
    String currentLocale = LocaleContextHolder.getLocale().getLanguage();

    String locale =
        localeRepository.findAllCodes().contains(currentLocale) ? currentLocale : defaultLocale;

    return bankContactsMapper.toDto(entity, locale);
  }
}

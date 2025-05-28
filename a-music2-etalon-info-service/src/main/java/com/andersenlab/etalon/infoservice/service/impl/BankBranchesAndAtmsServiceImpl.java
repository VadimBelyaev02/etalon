package com.andersenlab.etalon.infoservice.service.impl;

import com.andersenlab.etalon.infoservice.dto.response.BankBranchesAndAtmsResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.BankBranchesResponseDto;
import com.andersenlab.etalon.infoservice.exception.BusinessException;
import com.andersenlab.etalon.infoservice.mapper.BankBranchesAndAtmsMapper;
import com.andersenlab.etalon.infoservice.repository.AtmRepository;
import com.andersenlab.etalon.infoservice.repository.BankBranchRepository;
import com.andersenlab.etalon.infoservice.service.BankBranchesAndAtmsService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BankBranchesAndAtmsServiceImpl implements BankBranchesAndAtmsService {

  private final AtmRepository atmRepository;
  private final BankBranchRepository bankBranchRepository;
  private final BankBranchesAndAtmsMapper bankAndAtmInfoMapper;

  @Override
  public List<BankBranchesAndAtmsResponseDto> getAllBranchesAndAtmsByCity(final String city) {
    final List<BankBranchesAndAtmsResponseDto> dtos = new ArrayList<>();
    if (Objects.nonNull(city)) {
      dtos.addAll(
          atmRepository.findAllByCity(city).stream().map(bankAndAtmInfoMapper::toDto).toList());
      dtos.addAll(
          bankBranchRepository.findAllByCity(city).stream()
              .map(bankAndAtmInfoMapper::toDto)
              .toList());
      return dtos.stream()
          .sorted(Comparator.comparing(BankBranchesAndAtmsResponseDto::address))
          .toList();
    }
    dtos.addAll(atmRepository.findAll().stream().map(bankAndAtmInfoMapper::toDto).toList());
    dtos.addAll(bankBranchRepository.findAll().stream().map(bankAndAtmInfoMapper::toDto).toList());
    return dtos.stream()
        .sorted(
            Comparator.comparing(BankBranchesAndAtmsResponseDto::city)
                .thenComparing(BankBranchesAndAtmsResponseDto::address))
        .toList();
  }

  @Override
  public List<String> getAllCities() {
    final List<BankBranchesAndAtmsResponseDto> dtos = new ArrayList<>();
    dtos.addAll(atmRepository.findAll().stream().map(bankAndAtmInfoMapper::toDto).toList());
    dtos.addAll(bankBranchRepository.findAll().stream().map(bankAndAtmInfoMapper::toDto).toList());
    return dtos.stream().map(BankBranchesAndAtmsResponseDto::city).distinct().sorted().toList();
  }

  @Override
  public BankBranchesResponseDto getBankBranchById(Long bankBranchId) {

    return bankAndAtmInfoMapper.toDtoBankBranchesResponseDto(
        bankBranchRepository
            .findById(bankBranchId)
            .orElseThrow(
                () ->
                    new BusinessException(
                        HttpStatus.NOT_FOUND,
                        BusinessException.BRANCH_NOT_FOUND_BY_ID.formatted(bankBranchId))));
  }
}

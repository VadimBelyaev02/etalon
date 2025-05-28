package com.andersenlab.etalon.infoservice.service;

import com.andersenlab.etalon.infoservice.dto.response.BankBranchesAndAtmsResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.BankBranchesResponseDto;
import java.util.List;

public interface BankBranchesAndAtmsService {

  List<BankBranchesAndAtmsResponseDto> getAllBranchesAndAtmsByCity(final String city);

  List<String> getAllCities();

  BankBranchesResponseDto getBankBranchById(Long bankBranchId);
}

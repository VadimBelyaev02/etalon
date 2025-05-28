package com.andersenlab.etalon.depositservice.service.business;

import com.andersenlab.etalon.depositservice.dto.deposit.response.DepositProductResponseDto;
import java.util.List;

public interface DepositProductService {
  List<DepositProductResponseDto> getAllDepositProducts();
}

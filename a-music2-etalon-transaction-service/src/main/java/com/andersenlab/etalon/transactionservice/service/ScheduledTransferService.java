package com.andersenlab.etalon.transactionservice.service;

import com.andersenlab.etalon.transactionservice.dto.transaction.response.ScheduledTransferDto;
import com.andersenlab.etalon.transactionservice.util.filter.ScheduledTransferFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ScheduledTransferService {

  Page<ScheduledTransferDto> getScheduledTransferByUserId(
      String userId, ScheduledTransferFilter scheduledTransferFilter, Pageable pageable);
}

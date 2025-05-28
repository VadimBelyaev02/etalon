package com.andersenlab.etalon.transactionservice.service.impl;

import com.andersenlab.etalon.transactionservice.dto.transaction.response.ScheduledTransferDto;
import com.andersenlab.etalon.transactionservice.mapper.ScheduledTransferMapper;
import com.andersenlab.etalon.transactionservice.repository.ScheduledTransferRepository;
import com.andersenlab.etalon.transactionservice.repository.specifications.ScheduledTransferSpecification;
import com.andersenlab.etalon.transactionservice.service.ScheduledTransferService;
import com.andersenlab.etalon.transactionservice.util.filter.ScheduledTransferFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledTransferServiceImpl implements ScheduledTransferService {

  private final ScheduledTransferRepository scheduledTransferRepository;
  private final ScheduledTransferMapper scheduledTransferMapper;

  @Override
  public Page<ScheduledTransferDto> getScheduledTransferByUserId(
      String userId, ScheduledTransferFilter scheduledTransferFilter, Pageable pageable) {
    return scheduledTransferRepository
        .findAll(
            ScheduledTransferSpecification.withFilters(userId, scheduledTransferFilter), pageable)
        .map(scheduledTransferMapper::scheduledTransferDto);
  }
}

package com.andersenlab.etalon.transactionservice.service;

import com.andersenlab.etalon.transactionservice.dto.transaction.request.TransactionCreateRequestDto;
import com.andersenlab.etalon.transactionservice.entity.EventEntity;
import com.andersenlab.etalon.transactionservice.entity.TransactionEntity;
import java.util.List;

public interface EventService {

  List<EventEntity> createEvents(
      TransactionEntity transactionEntity, TransactionCreateRequestDto transactionCreateRequestDto);

  Boolean checkEventsStatus(List<EventEntity> events);
}

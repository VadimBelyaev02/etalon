package com.andersenlab.etalon.transactionservice.service;

import com.andersenlab.etalon.transactionservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.request.CreateTransferRequestDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.CreateNewTransferResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.CreateTransferResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.TransferResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.TransferTypeResponseDto;
import com.andersenlab.etalon.transactionservice.entity.TransferEntity;
import com.andersenlab.etalon.transactionservice.util.enums.ConfirmationMethod;
import com.andersenlab.etalon.transactionservice.util.enums.TransferStatus;
import java.util.List;

public interface TransferService {

  CreateNewTransferResponseDto createTransfer(
      CreateTransferRequestDto transferRequestDto, String userId, boolean isTransient);

  List<TransferTypeResponseDto> getAllTransferTypes(String userId);

  TransferResponseDto getTransferByIdAndUserId(long transferId, String userId);

  CreateTransferResponseDto createConfirmation(long transferId, ConfirmationMethod method);

  MessageResponseDto processConfirmedTransfer(Long transferId);

  void deleteTransfer(long transferId);

  TransferEntity getTransferById(long transferId);

  void updateTransferStatus(Long transferId, TransferStatus transferStatus);
}

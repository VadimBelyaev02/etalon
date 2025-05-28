package com.andersenlab.etalon.userservice.service;

import com.andersenlab.etalon.userservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.userservice.dto.modification.request.UserEmailModificationRequestDto;
import com.andersenlab.etalon.userservice.dto.modification.responce.EmailModificationInfoResponseDto;
import com.andersenlab.etalon.userservice.dto.modification.responce.EmailModificationResponseDto;

public interface EmailModificationService {
  EmailModificationResponseDto requestEmailModification(
      UserEmailModificationRequestDto userEmailModificationRequestDto);

  EmailModificationInfoResponseDto getEmailModificationInfo(long modificationId);

  MessageResponseDto processEmailModification(long modificationId);
}

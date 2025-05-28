package com.andersenlab.etalon.userservice.dto.genericemail;

import com.andersenlab.etalon.userservice.util.EmailType;
import lombok.Builder;

@Builder(toBuilder = true)
public record EmailModificationSendRequestDto(
    String toEmail, String subject, EmailType type, String newEmail, String verificationCode) {}

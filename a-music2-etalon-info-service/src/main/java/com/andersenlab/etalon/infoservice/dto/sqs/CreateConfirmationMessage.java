package com.andersenlab.etalon.infoservice.dto.sqs;

import java.io.Serializable;
import lombok.Builder;

@Builder(toBuilder = true)
public record CreateConfirmationMessage(long confirmationId) implements Serializable {}

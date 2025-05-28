package com.andersenlab.etalon.infoservice.dto.email;

import com.andersenlab.etalon.infoservice.dto.FileDto;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SendRawEmailRequestDto {
  private String from;
  private String to;
  private String subject;
  private String text;
  private FileDto attachment;
}

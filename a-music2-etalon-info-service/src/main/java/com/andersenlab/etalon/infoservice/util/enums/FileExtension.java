package com.andersenlab.etalon.infoservice.util.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileExtension {
  PDF(".pdf"),
  XLSX(".xlsx");
  private final String extension;
}

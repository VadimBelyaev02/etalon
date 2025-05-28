package com.andersenlab.etalon.infoservice.controller.impl;

import static com.andersenlab.etalon.infoservice.util.Constants.PDF_TYPE;

import com.andersenlab.etalon.infoservice.controller.DocumentController;
import com.andersenlab.etalon.infoservice.dto.FileDto;
import com.andersenlab.etalon.infoservice.interceptor.AuthenticationHolder;
import com.andersenlab.etalon.infoservice.service.DocumentService;
import com.andersenlab.etalon.infoservice.util.filter.TransactionFilter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(DocumentController.BANK_INFO_DOCUMENT_URL)
@RequiredArgsConstructor
@Tag(name = "Document")
public class DocumentControllerImpl implements DocumentController {

  private final DocumentService documentService;
  private final AuthenticationHolder authenticationHolder;

  @GetMapping(value = CONFIRMATION_STATEMENT_URI)
  public ResponseEntity<Resource> downloadConfirmationPdf(
      @RequestParam Long transactionId, String locale) {
    return wrapFileDtoToResponse(
        documentService.createTransactionConfirmation(
            authenticationHolder.getUserId(), transactionId, locale));
  }

  @GetMapping(value = TRANSACTIONS_STATEMENT_URI)
  public ResponseEntity<Resource> downloadTransactionsStatement(
      @RequestParam(defaultValue = PDF_TYPE) String statementType,
      @ParameterObject TransactionFilter filter,
      String locale) {
    return wrapFileDtoToResponse(
        documentService.createTransactionsStatement(
            authenticationHolder.getUserId(), filter, statementType, locale));
  }

  @GetMapping(TRANSFERS_RECEIPTS_URI)
  public ResponseEntity<Resource> downloadReceiptPdf(@RequestParam Long transferId) {
    return wrapFileDtoToResponse(
        documentService.createTransferReceipt(transferId, authenticationHolder.getUserId()));
  }

  @Override
  public ResponseEntity<Resource> downloadTermsAndPolicy() {
    return wrapFileDtoToResponse(documentService.downloadTermsAndPolicy());
  }

  private ResponseEntity<Resource> wrapFileDtoToResponse(FileDto dto) {

    HttpHeaders headers = buildHeaders(dto);

    return ResponseEntity.ok()
        .headers(headers)
        .contentLength(dto.size())
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(dto.resource());
  }

  private HttpHeaders buildHeaders(FileDto dto) {
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + dto.fileName());
    headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
    headers.add(HttpHeaders.PRAGMA, "no-cache");
    headers.add(HttpHeaders.EXPIRES, "0");
    return headers;
  }
}

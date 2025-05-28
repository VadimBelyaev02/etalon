package com.andersenlab.etalon.infoservice.service.impl;

import static com.andersenlab.etalon.infoservice.controller.impl.DocumentControllerImpl.DEFAULT_FILE_NAME;
import static com.andersenlab.etalon.infoservice.exception.TechnicalException.FILE_PARSING_ERROR;
import static com.andersenlab.etalon.infoservice.util.Constants.DEFAULT_FILE_NAME_FORMAT;
import static com.andersenlab.etalon.infoservice.util.Constants.PDF_TYPE;
import static com.andersenlab.etalon.infoservice.util.Constants.TERMS_AND_POLICY_FILE_NAME;
import static com.andersenlab.etalon.infoservice.util.Constants.TRANSACTIONS_PERIOD_FORMAT;
import static java.util.Objects.isNull;

import com.andersenlab.etalon.infoservice.client.AccountServiceClient;
import com.andersenlab.etalon.infoservice.client.TransactionServiceClient;
import com.andersenlab.etalon.infoservice.client.UserServiceClient;
import com.andersenlab.etalon.infoservice.config.DocumentReceiptProperties;
import com.andersenlab.etalon.infoservice.config.DocumentStatementProperties;
import com.andersenlab.etalon.infoservice.dto.FileDto;
import com.andersenlab.etalon.infoservice.dto.common.TransferReceiptContext;
import com.andersenlab.etalon.infoservice.dto.request.TransactionStatementPdfRequestDto;
import com.andersenlab.etalon.infoservice.dto.response.AccountDetailedResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.EventResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.TransactionDetailedResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.TransferResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.UserDataResponseDto;
import com.andersenlab.etalon.infoservice.exception.TechnicalException;
import com.andersenlab.etalon.infoservice.mapper.TransactionStatementPdfMapper;
import com.andersenlab.etalon.infoservice.mapper.TransferReceiptContextMapper;
import com.andersenlab.etalon.infoservice.service.DocumentService;
import com.andersenlab.etalon.infoservice.service.ExcelService;
import com.andersenlab.etalon.infoservice.service.PdfService;
import com.andersenlab.etalon.infoservice.util.DateTimeUtils;
import com.andersenlab.etalon.infoservice.util.FileUtils;
import com.andersenlab.etalon.infoservice.util.enums.Currency;
import com.andersenlab.etalon.infoservice.util.enums.FileExtension;
import com.andersenlab.etalon.infoservice.util.enums.Type;
import com.andersenlab.etalon.infoservice.util.filter.TransactionFilter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {
  private static final String EXCEL_MEDIA_TYPE =
      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
  private static final String EXCEL_TYPE = "xlsx";
  private final DocumentStatementProperties statementProperties;
  private final DocumentReceiptProperties receiptProperties;
  private final PdfService pdfService;
  private final ExcelService excelService;
  private final UserServiceClient userServiceClient;
  private final TransactionServiceClient transactionServiceClient;
  private final TransactionStatementPdfMapper transactionStatementPdfMapper;
  private final TransferReceiptContextMapper transferReceiptContextMapper;
  private final List<String> availableLocales = List.of("en", "pl");
  private final AccountServiceClient accountServiceClient;
  private final S3ServiceImpl s3Service;

  @Override
  public FileDto createTransferReceipt(Long transferId, String userId) {

    TransferResponseDto transferResponseDto =
        transactionServiceClient.getTransferById(transferId, userId);

    TransferReceiptContext receiptRequestDto =
        transferReceiptContextMapper.toTransferReceiptContext(transferResponseDto);

    File pdf = pdfService.createTransactionReceipt(receiptRequestDto);
    String name =
        String.format(
            FileUtils.getFilename(receiptProperties.getTransfer().getName(), FileExtension.PDF),
            transferResponseDto.transaction().transactionName());
    MediaType pdfMediaType = MediaType.APPLICATION_PDF;
    return wrapToFileDto(pdf, pdfMediaType, name);
  }

  @Override
  public FileDto createTransactionConfirmation(String userId, Long transactionId, String locale) {

    UserDataResponseDto userData = userServiceClient.getUserData(userId);
    validateLocale(locale);
    TransactionDetailedResponseDto detailedTransaction =
        transactionServiceClient.getDetailedTransaction(transactionId, userId);

    Currency currency;
    if (Type.INCOME.equals(detailedTransaction.type())) {
      currency = getAccountDetails(detailedTransaction.incomeAccountNumber()).currency();
    } else {
      currency = getAccountDetails(detailedTransaction.outcomeAccountNumber()).currency();
    }

    TransactionStatementPdfRequestDto transactionStatementPdfDto =
        transactionStatementPdfMapper.toTransactionStatementPdfDto(
            userData, detailedTransaction, currency);

    File pdf =
        pdfService.createTransactionPdf(transactionStatementPdfDto, Locale.forLanguageTag(locale));

    String name =
        String.format(
            FileUtils.getFilename(
                statementProperties.getTransaction().getName(), FileExtension.PDF),
            detailedTransaction.transactionName());
    MediaType pdfMediaType = MediaType.APPLICATION_PDF;
    return wrapToFileDto(pdf, pdfMediaType, name);
  }

  private void validateLocale(String locale) {
    if (StringUtils.isNotBlank(locale) && !availableLocales.contains(locale)) {
      throw new TechnicalException(HttpStatus.BAD_REQUEST, "This locale is not supported");
    }
  }

  private AccountDetailedResponseDto getAccountDetails(String accountNumber) {
    return accountServiceClient.getDetailedAccountInfo(accountNumber);
  }

  @Override
  public FileDto createTransactionsStatement(
      String userId, TransactionFilter filter, String statementType, String locale) {
    TransactionFilter finalFilter =
        filter.toBuilder()
            .accountNumber(
                "all".equalsIgnoreCase(filter.getAccountNumber())
                    ? null
                    : filter.getAccountNumber())
            .type("all".equalsIgnoreCase(filter.getType()) ? null : filter.getType())
            .build();
    UserDataResponseDto userData = userServiceClient.getUserData(userId);
    validateLocale(locale);
    List<EventResponseDto> eventResponseDtoList =
        transactionServiceClient.getAllTransactions(finalFilter, userId);

    List<TransactionDetailedResponseDto> transactions =
        eventResponseDtoList.stream()
            .map(EventResponseDto::id)
            .distinct()
            .map(id -> transactionServiceClient.getDetailedTransaction(id, userId))
            .filter(
                td ->
                    isNull(finalFilter.getType())
                        || td.type().getName().equalsIgnoreCase(finalFilter.getType()))
            .toList();

    String period = getTransactionsPeriod(filter, transactions);

    if (EXCEL_TYPE.equals(statementType)) {
      return buildExcelTransactionsStatement(userData, transactions, period, locale);
    } else if (PDF_TYPE.equals(statementType)) {
      return buildPdfTransactionsStatement(userData, transactions, period, locale);
    }
    throw new TechnicalException(
        HttpStatus.CONFLICT,
        TechnicalException.ERROR_USING_STATEMENT_TYPE.formatted(statementType));
  }

  private FileDto buildPdfTransactionsStatement(
      UserDataResponseDto userData,
      List<TransactionDetailedResponseDto> transactions,
      String period,
      String locale) {
    File transactionsStatement =
        pdfService.createTransactionsStatement(userData, transactions, period, locale);

    String name =
        String.format(
            FileUtils.getFilename(
                statementProperties.getConfirmation().getName(), FileExtension.PDF),
            userData.lastName());
    MediaType pdfMediaType = MediaType.APPLICATION_PDF;
    return wrapToFileDto(transactionsStatement, pdfMediaType, name);
  }

  private FileDto buildExcelTransactionsStatement(
      UserDataResponseDto userData,
      List<TransactionDetailedResponseDto> transactions,
      String period,
      String locale) {

    File transactionsStatement =
        excelService.createTransactionsStatement(userData, transactions, period, locale);
    String name =
        String.format(
            FileUtils.getFilename(
                statementProperties.getTransactions().getName(), FileExtension.XLSX),
            userData.lastName());
    MediaType xlsxMediaType = MediaType.parseMediaType(EXCEL_MEDIA_TYPE);
    return wrapToFileDto(transactionsStatement, xlsxMediaType, name);
  }

  private FileDto wrapToFileDto(File file, MediaType mediaType, String filename) {

    FileDto dto;

    try {
      byte[] fileBytes = Files.readAllBytes(file.toPath());
      ByteArrayResource resource = new ByteArrayResource(fileBytes);

      dto =
          FileDto.builder()
              .fileName(buildFilename(file, mediaType))
              .size((long) fileBytes.length)
              .resource(resource)
              .build();

    } catch (IOException e) {
      throw new TechnicalException(
          HttpStatus.INTERNAL_SERVER_ERROR, TechnicalException.ERROR_READING_WRITING_FILE);
    }
    try {
      Files.delete(file.toPath());
    } catch (IOException e) {
      throw new TechnicalException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          TechnicalException.FAILED_DELETE_TEMPORARY_FILE.formatted(e));
    }

    return dto;
  }

  private String buildFilename(File file, MediaType mediaType) {
    return Objects.nonNull(file.getName())
        ? FileUtils.fixSpaces(file.getName())
        : DEFAULT_FILE_NAME_FORMAT.formatted(DEFAULT_FILE_NAME, mediaType.toString());
  }

  private String getTransactionsPeriod(
      TransactionFilter filter, List<TransactionDetailedResponseDto> transactions) {

    String startDate = filter.getStartDate();
    String endDate = filter.getEndDate();
    String orderBy = filter.getOrderBy();

    String start =
        isNull(startDate)
            ? transactions.isEmpty()
                ? StringUtils.EMPTY
                : Objects.equals("asc", orderBy)
                    ? transactions.get(0).transactionDate()
                    : transactions.get(transactions.size() - 1).transactionDate()
            : DateTimeUtils.getDateFromDateTimeString(startDate);

    String end =
        isNull(endDate)
            ? transactions.isEmpty()
                ? StringUtils.EMPTY
                : Objects.equals("asc", orderBy)
                    ? transactions.get(transactions.size() - 1).transactionDate()
                    : transactions.get(0).transactionDate()
            : DateTimeUtils.getDateFromDateTimeString(endDate);

    return TRANSACTIONS_PERIOD_FORMAT.formatted(start, end);
  }

  @Override
  public FileDto downloadTermsAndPolicy() {
    try {
      byte[] byteArray = s3Service.downloadFile(TERMS_AND_POLICY_FILE_NAME);
      return new FileDto(
          TERMS_AND_POLICY_FILE_NAME, new ByteArrayResource(byteArray), (long) byteArray.length);
    } catch (IOException e) {
      throw new TechnicalException(HttpStatus.BAD_REQUEST, FILE_PARSING_ERROR);
    }
  }
}

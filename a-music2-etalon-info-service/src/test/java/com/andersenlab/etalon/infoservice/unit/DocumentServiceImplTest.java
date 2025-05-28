package com.andersenlab.etalon.infoservice.unit;

import static com.andersenlab.etalon.infoservice.exception.TechnicalException.FILE_PARSING_ERROR;
import static com.andersenlab.etalon.infoservice.util.Constants.TERMS_AND_POLICY_FILE_NAME;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.andersenlab.etalon.infoservice.MockData;
import com.andersenlab.etalon.infoservice.client.AccountServiceClient;
import com.andersenlab.etalon.infoservice.client.TransactionServiceClient;
import com.andersenlab.etalon.infoservice.client.UserServiceClient;
import com.andersenlab.etalon.infoservice.config.DocumentStatementProperties;
import com.andersenlab.etalon.infoservice.dto.FileDto;
import com.andersenlab.etalon.infoservice.dto.request.TransactionStatementPdfRequestDto;
import com.andersenlab.etalon.infoservice.dto.response.AccountDetailedResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.EventResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.TransactionDetailedResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.UserDataResponseDto;
import com.andersenlab.etalon.infoservice.exception.TechnicalException;
import com.andersenlab.etalon.infoservice.mapper.TransactionStatementPdfMapper;
import com.andersenlab.etalon.infoservice.service.ExcelService;
import com.andersenlab.etalon.infoservice.service.PdfService;
import com.andersenlab.etalon.infoservice.service.impl.DocumentServiceImpl;
import com.andersenlab.etalon.infoservice.service.impl.S3ServiceImpl;
import com.andersenlab.etalon.infoservice.util.enums.Currency;
import com.andersenlab.etalon.infoservice.util.filter.TransactionFilter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocumentServiceImplTest {
  private static final String VALID_USER_ID = "userId";

  private static final Long VALID_TRANSACTION_ID = 123L;
  private static final String VALID_PDF_CONTENT_TYPE = "application/pdf";
  private static final String VALID_EXCEL_CONTENT_TYPE =
      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
  private static final String VALID_EXCEL_STATEMENT_TYPE = "xlsx";
  private static final String VALID_PDF_STATEMENT_TYPE = "pdf";
  private static final String INVALID_STATEMENT_TYPE = "unknown";

  private File pdfFile;
  private File excelFile;
  private UserDataResponseDto userDataResponseDto;
  private TransactionDetailedResponseDto transactionDetailedResponseDto;
  private TransactionStatementPdfRequestDto transactionStatementPdfRequestDto;
  private AccountDetailedResponseDto accountDetailedResponseDto;
  private List<EventResponseDto> eventResponseDtoList;

  @Mock private DocumentStatementProperties filenameProperties;
  @Mock private PdfService pdfService;
  @Mock private ExcelService excelService;

  @Mock private UserServiceClient userServiceClient;

  @Mock private TransactionServiceClient transactionServiceClient;

  @Mock private TransactionStatementPdfMapper transactionStatementPdfMapper;

  @Mock private AccountServiceClient accountServiceClient;

  @Mock private S3ServiceImpl s3Service;

  @InjectMocks private DocumentServiceImpl documentService;

  @BeforeEach
  public void setUp() throws NoSuchFieldException, IllegalAccessException {
    try (Workbook workbook = new XSSFWorkbook()) {
      excelFile = File.createTempFile("transactions-history", ".xlsx");
      try (FileOutputStream fileOut = new FileOutputStream(excelFile)) {
        workbook.write(fileOut);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    try (PDDocument document = new PDDocument()) {
      pdfFile = File.createTempFile("transaction-", ".pdf");
      document.save(pdfFile);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    userDataResponseDto = MockData.getValidUserDataResponseDto();
    transactionDetailedResponseDto = MockData.getValidIncomeTransactionDetailedResponseDto();
    transactionStatementPdfRequestDto = MockData.getValidTransactionStatementPdf();
    eventResponseDtoList = MockData.getValidEventResponseDtoList();
    accountDetailedResponseDto = MockData.getValidAccountDetailedResponseDto();
  }

  @AfterEach
  public void tearDown() {
    if (excelFile.exists()) {
      excelFile.delete();
    }
    if (pdfFile.exists()) {
      pdfFile.delete();
    }
  }

  @Test
  void whenDownloadTermsAndConditionPdf_Success() throws IOException {
    byte[] fileData = "test content".getBytes();
    when(s3Service.downloadFile(TERMS_AND_POLICY_FILE_NAME)).thenReturn(fileData);

    FileDto result = documentService.downloadTermsAndPolicy();

    assertNotNull(result);
    assertEquals(TERMS_AND_POLICY_FILE_NAME, result.fileName());
    assertEquals(fileData.length, result.size());
    assertArrayEquals(fileData, result.resource().getByteArray());
  }

  @Test
  void whenDownloadTermsAndConditionPdf_Fail() throws IOException {
    when(s3Service.downloadFile(TERMS_AND_POLICY_FILE_NAME)).thenThrow(new IOException("S3 error"));

    TechnicalException exception =
        assertThrows(TechnicalException.class, () -> documentService.downloadTermsAndPolicy());
    assertEquals(FILE_PARSING_ERROR, exception.getMessage());
  }

  @Test
  void whenDownloadConfirmationPdf_Success() {
    // given
    when(filenameProperties.getTransaction())
        .thenReturn(new DocumentStatementProperties.Transaction("transaction-%s"));

    when(userServiceClient.getUserData(anyString())).thenReturn(userDataResponseDto);
    when(transactionServiceClient.getDetailedTransaction(anyLong(), anyString()))
        .thenReturn(transactionDetailedResponseDto);
    when(accountServiceClient.getDetailedAccountInfo(anyString()))
        .thenReturn(accountDetailedResponseDto);
    when(transactionStatementPdfMapper.toTransactionStatementPdfDto(
            any(UserDataResponseDto.class),
            any(TransactionDetailedResponseDto.class),
            any(Currency.class)))
        .thenReturn(transactionStatementPdfRequestDto);
    when(pdfService.createTransactionPdf(any(), any())).thenReturn(pdfFile);

    // when()
    FileDto confirmation =
        documentService.createTransactionConfirmation(
            VALID_USER_ID, VALID_TRANSACTION_ID, anyString());

    // then
    verify(userServiceClient).getUserData(anyString());
    verify(transactionServiceClient).getDetailedTransaction(anyLong(), anyString());
    verify(transactionStatementPdfMapper).toTransactionStatementPdfDto(any(), any(), any());

    assertNotNull(confirmation);
  }

  @Test
  void whenDownloadTransactionsStatementExcel_Success() {
    // given
    TransactionFilter filter = MockData.getValidTransactionFilter();

    when(filenameProperties.getTransactions())
        .thenReturn(new DocumentStatementProperties.Transactions("transactions-%s"));

    when(userServiceClient.getUserData(anyString())).thenReturn(userDataResponseDto);
    when(transactionServiceClient.getAllTransactions(any(TransactionFilter.class), anyString()))
        .thenReturn(eventResponseDtoList);

    when(transactionServiceClient.getDetailedTransaction(anyLong(), anyString()))
        .thenReturn(transactionDetailedResponseDto);

    when(excelService.createTransactionsStatement(
            any(UserDataResponseDto.class), anyList(), anyString(), anyString()))
        .thenReturn(excelFile);

    // when()
    FileDto statement =
        documentService.createTransactionsStatement(
            VALID_USER_ID, filter, VALID_EXCEL_STATEMENT_TYPE, "pl");

    // then
    verify(userServiceClient).getUserData(anyString());
    verify(transactionServiceClient).getAllTransactions(any(TransactionFilter.class), anyString());
    verify(excelService)
        .createTransactionsStatement(
            any(UserDataResponseDto.class), anyList(), anyString(), anyString());

    assertNotNull(statement);
  }

  @Test
  void whenDownloadTransactionsStatementPdf_Success() {
    // given
    TransactionFilter filter = MockData.getValidTransactionFilter();

    when(filenameProperties.getConfirmation())
        .thenReturn(new DocumentStatementProperties.Confirmation("transactions-%s"));

    when(userServiceClient.getUserData(anyString())).thenReturn(userDataResponseDto);
    when(transactionServiceClient.getAllTransactions(any(TransactionFilter.class), anyString()))
        .thenReturn(eventResponseDtoList);
    when(pdfService.createTransactionsStatement(
            any(UserDataResponseDto.class), anyList(), anyString(), anyString()))
        .thenReturn(pdfFile);
    when(transactionServiceClient.getDetailedTransaction(anyLong(), anyString()))
        .thenReturn(transactionDetailedResponseDto);

    // when()
    FileDto statement =
        documentService.createTransactionsStatement(
            VALID_USER_ID, filter, VALID_PDF_STATEMENT_TYPE, "pl");

    // then
    verify(userServiceClient).getUserData(anyString());
    verify(transactionServiceClient).getAllTransactions(any(TransactionFilter.class), anyString());
    verify(pdfService)
        .createTransactionsStatement(
            any(UserDataResponseDto.class), anyList(), anyString(), anyString());

    assertNotNull(statement);
  }

  @Test
  void whenDownloadUnknownStatementType_shouldFail() {
    TransactionFilter filter = MockData.getValidTransactionFilter();
    // then
    assertThrows(
        TechnicalException.class,
        () ->
            documentService.createTransactionsStatement(
                VALID_USER_ID, filter, INVALID_STATEMENT_TYPE, "pl"));
  }
}

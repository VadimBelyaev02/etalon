package com.andersenlab.etalon.infoservice.service.impl;

import static com.andersenlab.etalon.infoservice.util.Constants.IMAGE_PATH;

import com.andersenlab.etalon.infoservice.dto.response.TransactionDetailedResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.UserDataResponseDto;
import com.andersenlab.etalon.infoservice.exception.TechnicalException;
import com.andersenlab.etalon.infoservice.service.ExcelService;
import com.andersenlab.etalon.infoservice.util.enums.TransactionStatus;
import com.andersenlab.etalon.infoservice.util.enums.Type;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExcelServiceImpl implements ExcelService {

  private static final String SHEET_NAME = "Transaction Data";
  private static final int START_ROW_NUMBER = 1;
  private static final int START_CELL_NUMBER = 1;
  private final MessageSource messageSource;

  @Override
  public File createTransactionsStatement(
      UserDataResponseDto userData,
      List<TransactionDetailedResponseDto> transactions,
      String period,
      String locale) {
    try (Workbook workbook = new XSSFWorkbook()) {
      Sheet sheet = workbook.createSheet(SHEET_NAME);
      Locale userLocale = Locale.forLanguageTag(locale);

      setSheetProperties(sheet);
      createImageRow(workbook, sheet);
      addInfoRows(sheet, userData, period, userLocale);

      if (!transactions.isEmpty()) {
        createHeaderRow(sheet, userLocale);
        addDataRows(sheet, transactions, userLocale);
        addTableBorders(sheet);
      } else {
        addNoTransactionsMessage(sheet);
      }

      return saveWorkbookToFile(workbook);
    } catch (IOException e) {
      throw new TechnicalException(
          HttpStatus.INTERNAL_SERVER_ERROR, TechnicalException.ERROR_CREATING_EXCEL);
    }
  }

  private void setSheetProperties(Sheet sheet) {
    int[] columnWidths = {3000, 8000, 8000, 8000, 6000, 6000, 6000, 6000};
    for (int i = 0; i < columnWidths.length; i++) {
      sheet.setColumnWidth(i, columnWidths[i]);
    }
  }

  private void createImageRow(Workbook workbook, Sheet sheet) {
    byte[] imageData = loadImageData();
    XSSFDrawing drawing = (XSSFDrawing) sheet.createDrawingPatriarch();
    int imageIndex = workbook.addPicture(imageData, Workbook.PICTURE_TYPE_PNG);

    ClientAnchor anchor = workbook.getCreationHelper().createClientAnchor();
    anchor.setCol1(START_CELL_NUMBER);
    anchor.setRow1(START_ROW_NUMBER);
    anchor.setCol2(START_CELL_NUMBER + 2);
    anchor.setRow2(START_ROW_NUMBER + 2);
    drawing.createPicture(anchor, imageIndex);
  }

  private byte[] loadImageData() {
    try (InputStream inputStream = new ClassPathResource(IMAGE_PATH).getInputStream()) {
      return IOUtils.toByteArray(inputStream);
    } catch (IOException e) {
      throw new TechnicalException(
          HttpStatus.INTERNAL_SERVER_ERROR, TechnicalException.ERROR_LOADING_IMAGE_FILE);
    }
  }

  private void addInfoRows(
      Sheet sheet, UserDataResponseDto userData, String period, Locale locale) {
    String accountHolder =
        messageSource.getMessage(
            "accountholder", new Object[] {userData.firstName(), userData.lastName()}, locale);
    String statementPeriod =
        messageSource.getMessage("statement-for-the-period", new Object[] {period}, locale);

    createInfoRow(sheet, accountHolder, START_ROW_NUMBER + 3);
    createInfoRow(sheet, statementPeriod, START_ROW_NUMBER + 5);
  }

  private void createInfoRow(Sheet sheet, String info, int rowNum) {
    Row row = sheet.createRow(rowNum);
    Cell cell = row.createCell(START_CELL_NUMBER);
    cell.setCellValue(info);
    applyBoldStyle(sheet, cell);
  }

  private void createHeaderRow(Sheet sheet, Locale locale) {
    Row headerRow = sheet.createRow(START_ROW_NUMBER + 7);
    String[] headers = {
      messageSource.getMessage("transaction-date-time", null, locale),
      messageSource.getMessage("sender-account", null, locale),
      messageSource.getMessage("receiver-account", null, locale),
      messageSource.getMessage("transaction-name", null, locale),
      messageSource.getMessage("transaction-status", null, locale),
      messageSource.getMessage("transaction-type", null, locale),
      messageSource.getMessage("transaction-amount", null, locale)
    };

    for (int i = 0; i < headers.length; i++) {
      Cell cell = headerRow.createCell(i + START_CELL_NUMBER);
      cell.setCellValue(headers[i]);
      applyHeaderStyle(sheet, cell);
    }
  }

  private void addDataRows(
      Sheet sheet, List<TransactionDetailedResponseDto> transactions, Locale locale) {
    int rowIndex = START_ROW_NUMBER + 8;

    for (TransactionDetailedResponseDto transaction : transactions) {
      Row row = sheet.createRow(rowIndex++);
      int cellIndex = START_CELL_NUMBER;

      row.createCell(cellIndex++)
          .setCellValue(transaction.transactionDate() + " " + transaction.transactionTime());
      row.createCell(cellIndex++).setCellValue(transaction.outcomeAccountNumber());
      row.createCell(cellIndex++).setCellValue(transaction.incomeAccountNumber());
      row.createCell(cellIndex++).setCellValue(transaction.transactionName());
      row.createCell(cellIndex++).setCellValue(formatStatus(transaction.status(), locale));
      row.createCell(cellIndex++)
          .setCellValue(messageSource.getMessage(transaction.type().name(), null, locale));
      row.createCell(cellIndex).setCellValue(formatAmount(transaction));
    }
  }

  private String formatStatus(TransactionStatus status, Locale locale) {
    return StringUtils.capitalize(messageSource.getMessage(status.name(), null, locale));
  }

  private String formatAmount(TransactionDetailedResponseDto transaction) {
    String sign = transaction.type() == Type.INCOME ? "+" : "-";
    return String.format(
        "%s%s%s",
        sign, transaction.transactionAmount(), transaction.currency().getCurrencySymbol());
  }

  private void addTableBorders(Sheet sheet) {
    int firstRow = START_ROW_NUMBER + 8;
    int lastRow = sheet.getLastRowNum();

    for (int i = firstRow; i <= lastRow; i++) {
      Row row = sheet.getRow(i);
      if (row != null) {
        for (Cell cell : row) {
          applyBorderStyle(sheet, cell);
        }
      }
    }
  }

  private void addNoTransactionsMessage(Sheet sheet) {
    Row row = sheet.createRow(START_ROW_NUMBER + 7);
    Cell cell = row.createCell(START_CELL_NUMBER);
    cell.setCellValue("You haven't made any transactions during this period");
    applyBoldStyle(sheet, cell);
  }

  private File saveWorkbookToFile(Workbook workbook) {
    File tempFile;
    try {
      File tempDir = new File(System.getProperty("java.io.tmpdir"));
      tempFile = File.createTempFile("transactions-history", ".xlsx", tempDir);
      try (FileOutputStream out = new FileOutputStream(tempFile)) {
        workbook.write(out);
      }
      return tempFile;
    } catch (IOException e) {
      throw new TechnicalException(
          HttpStatus.INTERNAL_SERVER_ERROR, TechnicalException.ERROR_CREATING_EXCEL);
    }
  }

  private void applyBoldStyle(Sheet sheet, Cell cell) {
    Workbook workbook = sheet.getWorkbook();
    CellStyle style = workbook.createCellStyle();
    Font font = workbook.createFont();
    font.setBold(true);
    font.setFontName("Arial");
    font.setFontHeightInPoints((short) 12);
    style.setFont(font);
    cell.setCellStyle(style);
  }

  private void applyHeaderStyle(Sheet sheet, Cell cell) {
    Workbook workbook = sheet.getWorkbook();
    CellStyle style = workbook.createCellStyle();
    style.setBorderBottom(BorderStyle.THIN);
    style.setBorderTop(BorderStyle.THIN);
    style.setBorderLeft(BorderStyle.THIN);
    style.setBorderRight(BorderStyle.THIN);

    Font font = workbook.createFont();
    font.setBold(true);
    font.setFontName("Arial");
    font.setFontHeightInPoints((short) 10);
    style.setFont(font);
    cell.setCellStyle(style);
  }

  private void applyBorderStyle(Sheet sheet, Cell cell) {
    Workbook workbook = sheet.getWorkbook();
    CellStyle style = workbook.createCellStyle();
    style.setBorderBottom(BorderStyle.THIN);
    style.setBorderTop(BorderStyle.THIN);
    style.setBorderLeft(BorderStyle.THIN);
    style.setBorderRight(BorderStyle.THIN);
    cell.setCellStyle(style);
  }
}

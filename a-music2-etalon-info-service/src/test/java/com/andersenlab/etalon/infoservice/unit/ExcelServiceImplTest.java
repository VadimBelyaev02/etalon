package com.andersenlab.etalon.infoservice.unit;

import static com.andersenlab.etalon.infoservice.MockData.getValidIncomeTransactionDetailedResponseDto;
import static com.andersenlab.etalon.infoservice.MockData.getValidUserDataResponseDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.andersenlab.etalon.infoservice.service.impl.ExcelServiceImpl;
import java.io.File;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

@ExtendWith(MockitoExtension.class)
public class ExcelServiceImplTest {
  @InjectMocks private ExcelServiceImpl target;
  @Mock private MessageSource messageSource;

  @Test
  void createTransactionStatementXlsx() {
    when(messageSource.getMessage(anyString(), any(), any())).thenReturn("Mocked message");
    File actualFile =
        target.createTransactionsStatement(
            getValidUserDataResponseDto(),
            List.of(getValidIncomeTransactionDetailedResponseDto()),
            null,
            "pl");
    File expectedFile =
        new File(
            Objects.requireNonNull(
                    Thread.currentThread()
                        .getContextClassLoader()
                        .getResource("static/transaction-statement-mock.xlsx"))
                .getFile());
    try (FileInputStream expectedXlsx = new FileInputStream(expectedFile);
        FileInputStream actualXlsx = new FileInputStream(actualFile)) {
      Workbook expectedWorkbook = WorkbookFactory.create(expectedXlsx);
      Workbook actualWorkbook = WorkbookFactory.create(actualXlsx);
      assertThat(expectedWorkbook.getNumberOfSheets())
          .isEqualTo(actualWorkbook.getNumberOfSheets());
    } catch (Exception ignored) {
      fail("Exception occurred");
    }
  }

  @Test
  void createEmptyTransactionStatementXlsx() {
    when(messageSource.getMessage(anyString(), any(), any())).thenReturn("Mocked message");
    File actualFile =
        target.createTransactionsStatement(
            getValidUserDataResponseDto(), Collections.emptyList(), null, "pl");
    File expectedFile =
        new File(
            Objects.requireNonNull(
                    Thread.currentThread()
                        .getContextClassLoader()
                        .getResource("static/transaction-statement-mock.xlsx"))
                .getFile());
    try (FileInputStream expectedXlsx = new FileInputStream(expectedFile);
        FileInputStream actualXlsx = new FileInputStream(actualFile)) {
      Workbook expectedWorkbook = WorkbookFactory.create(expectedXlsx);
      Workbook actualWorkbook = WorkbookFactory.create(actualXlsx);
      assertThat(expectedWorkbook.getNumberOfSheets())
          .isEqualTo(actualWorkbook.getNumberOfSheets());
    } catch (Exception ignored) {
      fail("Exception occurred");
    }
  }
}

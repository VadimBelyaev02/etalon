package com.andersenlab.etalon.infoservice.unit;

import static com.andersenlab.etalon.infoservice.MockData.getTransferReceiptContextDto;
import static com.andersenlab.etalon.infoservice.MockData.getValidFirstTransactionDetailedResponseDto;
import static com.andersenlab.etalon.infoservice.MockData.getValidIncomeTransactionDetailedResponseDto;
import static com.andersenlab.etalon.infoservice.MockData.getValidOutcomeTransactionDetailedResponseDto;
import static com.andersenlab.etalon.infoservice.MockData.getValidSecondTransactionDetailedResponseDto;
import static com.andersenlab.etalon.infoservice.MockData.getValidTransactionStatementPdf;
import static com.andersenlab.etalon.infoservice.MockData.getValidUserDataResponseDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.andersenlab.etalon.infoservice.dto.response.TransactionDetailedResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.UserDataResponseDto;
import com.andersenlab.etalon.infoservice.service.impl.PdfServiceImpl;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

@ExtendWith(MockitoExtension.class)
class PdfServiceImplTest {
  @InjectMocks private PdfServiceImpl target;
  @Mock private MessageSource messageSource;

  @Test
  void createTransactionPdf() {
    when(messageSource.getMessage(anyString(), any(), any())).thenReturn("Mocked message");
    File actualFile =
        target.createTransactionPdf(getValidTransactionStatementPdf(), Locale.forLanguageTag("pl"));
    File expectedFile =
        new File(
            Objects.requireNonNull(
                    Thread.currentThread()
                        .getContextClassLoader()
                        .getResource("static/confirmation-statement-mock.pdf"))
                .getFile());
    try (PDDocument expectedPdf = PDDocument.load(expectedFile);
        PDDocument actualPdf = PDDocument.load(actualFile)) {
      byte[] expectBytes = expectedPdf.getPages().get(0).getContents().readAllBytes();
      byte[] actualBytes = actualPdf.getPages().get(0).getContents().readAllBytes();
      assertThat(expectBytes).containsExactly(actualBytes);
    } catch (Exception ignored) {
      fail("Exception occurred");
    }
  }

  @Test
  void createTransactionReceipt() {
    File actualFile = target.createTransactionReceipt(getTransferReceiptContextDto());
    File expectedFile =
        new File(
            Objects.requireNonNull(
                    Thread.currentThread()
                        .getContextClassLoader()
                        .getResource("static/test-receipt.pdf"))
                .getFile());

    try (PDDocument expectedPdf = PDDocument.load(expectedFile);
        PDDocument actualPdf = PDDocument.load(actualFile)) {
      byte[] expectBytes = expectedPdf.getPages().get(0).getContents().readAllBytes();
      byte[] actualBytes = actualPdf.getPages().get(0).getContents().readAllBytes();
      assertThat(expectBytes).containsExactly(actualBytes);
    } catch (Exception ignored) {
      fail("Exception occurred");
    }
  }

  @Test
  void createTransactionsStatement_ShouldSucceed() {
    when(messageSource.getMessage(anyString(), any(), any())).thenReturn("Mocked message");
    UserDataResponseDto userDataResponseDto = getValidUserDataResponseDto();

    List<TransactionDetailedResponseDto> transactions =
        List.of(
            getValidIncomeTransactionDetailedResponseDto(),
            getValidOutcomeTransactionDetailedResponseDto());

    File actualFile =
        target.createTransactionsStatement(
            userDataResponseDto, transactions, "04.10.2023 - 04.10.2023", "pl");

    File expectedFile =
        new File(
            Objects.requireNonNull(
                    Thread.currentThread()
                        .getContextClassLoader()
                        .getResource("static/new_transactions_statement_test.pdf"))
                .getFile());

    try (PDDocument expectedPdf = PDDocument.load(expectedFile);
        PDDocument actualPdf = PDDocument.load(actualFile)) {
      byte[] expectBytes = expectedPdf.getPages().get(0).getContents().readAllBytes();
      byte[] actualBytes = actualPdf.getPages().get(0).getContents().readAllBytes();
      assertThat(expectBytes).containsExactly(actualBytes);
    } catch (Exception ignored) {
      fail("Exception occurred");
    }
  }

  @Test
  void createTransactionsStatement_NoTransactions_ShouldSucceed() {
    when(messageSource.getMessage(anyString(), any(), any())).thenReturn("Mocked message");
    UserDataResponseDto userDataResponseDto = getValidUserDataResponseDto();

    List<TransactionDetailedResponseDto> transactions = Collections.emptyList();

    File actualFile =
        target.createTransactionsStatement(
            userDataResponseDto, transactions, "14.04.2023 - 14.04.2023", "pl");

    File expectedFile =
        new File(
            Objects.requireNonNull(
                    Thread.currentThread()
                        .getContextClassLoader()
                        .getResource("static/empty-transactions-statement-test.pdf"))
                .getFile());

    try (PDDocument expectedPdf = PDDocument.load(expectedFile);
        PDDocument actualPdf = PDDocument.load(actualFile)) {
      byte[] expectedBytes = expectedPdf.getPages().get(0).getContents().readAllBytes();
      byte[] actualBytes = actualPdf.getPages().get(0).getContents().readAllBytes();
      assertThat(expectedBytes).containsExactly(actualBytes);
    } catch (Exception ignored) {
      fail("Exception occurred");
    }
  }

  @Test
  void createTransactionsStatement_WithDynamicRows_ShouldSucceed() {
    when(messageSource.getMessage(anyString(), any(), any())).thenReturn("Mocked message");
    UserDataResponseDto userDataResponseDto = getValidUserDataResponseDto();

    List<TransactionDetailedResponseDto> transactions =
        List.of(
            getValidFirstTransactionDetailedResponseDto(),
            getValidSecondTransactionDetailedResponseDto());

    File actualFile =
        target.createTransactionsStatement(
            userDataResponseDto, transactions, "07.08.2024 - 11.08.2024", "pl");

    File expectedFile =
        new File(
            Objects.requireNonNull(
                    Thread.currentThread()
                        .getContextClassLoader()
                        .getResource("static/transactions-with-dynamic-row-test.pdf"))
                .getFile());

    try (PDDocument expectedPdf = PDDocument.load(expectedFile);
        PDDocument actualPdf = PDDocument.load(actualFile)) {
      byte[] expectBytes = expectedPdf.getPages().get(0).getContents().readAllBytes();
      byte[] actualBytes = actualPdf.getPages().get(0).getContents().readAllBytes();
      assertThat(expectBytes).containsExactly(actualBytes);
    } catch (Exception ignored) {
      fail("Exception occurred");
    }
  }
}

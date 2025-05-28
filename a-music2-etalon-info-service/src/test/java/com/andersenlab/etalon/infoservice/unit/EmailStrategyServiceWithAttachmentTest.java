package com.andersenlab.etalon.infoservice.unit;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.andersenlab.etalon.infoservice.dto.FileDto;
import com.andersenlab.etalon.infoservice.dto.request.ConfirmationStatementDto;
import com.andersenlab.etalon.infoservice.dto.request.SharingTransferReceiptDto;
import com.andersenlab.etalon.infoservice.interceptor.AuthenticationHolder;
import com.andersenlab.etalon.infoservice.service.DocumentService;
import com.andersenlab.etalon.infoservice.service.EmailStrategyService;
import com.andersenlab.etalon.infoservice.util.enums.EmailType;
import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;
import software.amazon.awssdk.services.ses.model.RawMessage;
import software.amazon.awssdk.services.ses.model.SendRawEmailRequest;

@ExtendWith(MockitoExtension.class)
public class EmailStrategyServiceWithAttachmentTest {
  @Mock private DocumentService documentService;
  @Mock private AuthenticationHolder authenticationHolder;
  private final Session session = Session.getDefaultInstance(new Properties());
  private EmailStrategyService emailStrategyService;

  @BeforeEach
  void setUp() throws IOException {
    when(authenticationHolder.getUserId()).thenReturn("321");

    emailStrategyService = new EmailStrategyService(session, documentService, authenticationHolder);
    emailStrategyService.init();
  }

  @Test
  void testBuildEmailSharingTransferReceipt() throws MessagingException, IOException {
    when(documentService.createTransferReceipt(any(), any())).thenReturn(loadTestReceipt());
    SharingTransferReceiptDto request = new SharingTransferReceiptDto();

    request.setToEmail("testTest3@example.com");
    request.setType(EmailType.SHARING_TRANSFER_RECEIPT);
    request.setTransferId(1L);

    SendRawEmailRequest sendRawEmailRequest = emailStrategyService.buildMessage(request);
    Result result = getResult(sendRawEmailRequest);

    assertNotNull(result);
    assertEquals("testTest3@example.com", result.to);
    assertEquals("Transfer receipt", result.subject);
    assertEquals("Please find the transfer receipt attached to this mail", result.text);
    assertEquals("test-receipt.pdf", result.filename);
    assertArrayEquals(loadTestReceipt().resource().getByteArray(), result.bytes);
  }

  @Test
  void testBuildEmailConfirmationStatement() throws MessagingException, IOException {
    when(documentService.createTransactionConfirmation(any(), any(), any()))
        .thenReturn(loadTestReceipt());
    ConfirmationStatementDto request = new ConfirmationStatementDto();

    request.setToEmail("testTest4@example.com");
    request.setType(EmailType.CONFIRMATION_STATEMENT);
    request.setTransactionId(2L);
    request.setLocale("ru");

    SendRawEmailRequest sendRawEmailRequest = emailStrategyService.buildMessage(request);
    Result result = getResult(sendRawEmailRequest);

    assertNotNull(result);
    assertEquals("testTest4@example.com", result.to);
    assertEquals("Confirmation statement", result.subject);
    assertEquals("Please find the confirmation statement attached to this mail", result.text);
    assertEquals("test-receipt.pdf", result.filename);
    assertArrayEquals(loadTestReceipt().resource().getByteArray(), result.bytes);
  }

  private FileDto loadTestReceipt() throws IOException {
    Resource resource = new ClassPathResource("static/test-receipt.pdf");
    byte[] data = StreamUtils.copyToByteArray(resource.getInputStream());
    ByteArrayResource bar = new ByteArrayResource(data);
    String fileName = resource.getFilename();
    Long size = (long) data.length;
    return new FileDto(fileName, bar, size);
  }

  private Result getResult(SendRawEmailRequest sendRawEmailRequest)
      throws MessagingException, IOException {
    RawMessage raw = sendRawEmailRequest.rawMessage();
    byte[] rawBytes = raw.data().asByteArray();
    MimeMessage mime = new MimeMessage(session, new ByteArrayInputStream(rawBytes));
    String to =
        Arrays.stream(mime.getAllRecipients()).findFirst().map(Address::toString).orElse("");
    String subject = mime.getSubject();
    String text = "";
    String fileName = "";
    byte[] bytes = new byte[] {};
    Object content = mime.getContent();
    if (content instanceof MimeMultipart multipart) {
      Object part = multipart.getBodyPart(0).getContent();
      if (part instanceof MimeMultipart mimeMultipartNested) {
        text = (String) mimeMultipartNested.getBodyPart(0).getContent();
        fileName = multipart.getBodyPart(1).getFileName();
        bytes = multipart.getBodyPart(1).getInputStream().readAllBytes();
      }
    }
    return new Result(to, subject, text, fileName, bytes);
  }

  private record Result(String to, String subject, String text, String filename, byte[] bytes) {}
}

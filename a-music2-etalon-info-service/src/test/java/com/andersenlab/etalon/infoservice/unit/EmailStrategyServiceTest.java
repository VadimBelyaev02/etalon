package com.andersenlab.etalon.infoservice.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.andersenlab.etalon.infoservice.dto.request.ConfirmationEmailRequestDto;
import com.andersenlab.etalon.infoservice.dto.request.EmailModificationRequestDto;
import com.andersenlab.etalon.infoservice.dto.request.RegistrationEmailRequestDto;
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
import org.mockito.Mock;
import software.amazon.awssdk.services.ses.model.RawMessage;
import software.amazon.awssdk.services.ses.model.SendRawEmailRequest;

class EmailStrategyServiceTest {

  @Mock private DocumentService documentService;
  @Mock private AuthenticationHolder authenticationHolder;
  private final Session session = Session.getDefaultInstance(new Properties());
  private EmailStrategyService emailStrategyService;

  @BeforeEach
  void setUp() {
    emailStrategyService = new EmailStrategyService(session, documentService, authenticationHolder);
    emailStrategyService.init();
  }

  @Test
  void testBuildConfirmationMessage() throws MessagingException, IOException {
    ConfirmationEmailRequestDto request = new ConfirmationEmailRequestDto();

    request.setToEmail("test@example.com");
    request.setSubject("subject");
    request.setType(EmailType.CONFIRMATION);
    request.setVerificationCode("1234");

    SendRawEmailRequest sendRawEmailRequest = emailStrategyService.buildMessage(request);
    Result result = getResult(sendRawEmailRequest);

    assertNotNull(sendRawEmailRequest);
    assertEquals("test@example.com", result.to);
    assertEquals("Confirmation Code", result.subject);
    assertEquals("Your confirmation code is: 1234", result.text);
  }

  @Test
  void testBuildRegistrationMessage() throws MessagingException, IOException {

    RegistrationEmailRequestDto request = new RegistrationEmailRequestDto();
    request.setType(EmailType.REGISTRATION);
    request.setToEmail("userTest1@example.com");
    request.setVerificationCode("67890");

    SendRawEmailRequest sendRawEmailRequest = emailStrategyService.buildMessage(request);
    Result result = getResult(sendRawEmailRequest);

    assertNotNull(result);
    assertEquals("userTest1@example.com", result.to);
    assertEquals("Registration Complete", result.subject);
    assertEquals("Thank you for registering! Your confirmation code is: 67890", result.text);
  }

  @Test
  void testBuildEmailModificationMessage() throws MessagingException, IOException {

    EmailModificationRequestDto request = new EmailModificationRequestDto();

    request.setToEmail("testTest2@example.com");
    request.setNewEmail("new@email");
    request.setType(EmailType.EMAIL_MODIFY_CONFIRMATION);
    request.setSubject("Email Modification");
    request.setVerificationCode("1234");

    SendRawEmailRequest sendRawEmailRequest = emailStrategyService.buildMessage(request);
    Result result = getResult(sendRawEmailRequest);

    assertNotNull(result);
    assertEquals("testTest2@example.com", result.to);
    assertEquals("Email Modification", result.subject);
    assertEquals(
        String.format(
            "An email will be updated to: %s. Your confirmation code is: %s",
            request.getNewEmail(), request.getVerificationCode()),
        result.text);
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
    Object content = mime.getContent();
    if (content instanceof MimeMultipart multipart) {
      Object part = multipart.getBodyPart(0).getContent();
      if (part instanceof MimeMultipart mimeMultipartNested) {
        text = (String) mimeMultipartNested.getBodyPart(0).getContent();
      }
    }
    return new Result(to, subject, text);
  }

  private record Result(String to, String subject, String text) {}
}

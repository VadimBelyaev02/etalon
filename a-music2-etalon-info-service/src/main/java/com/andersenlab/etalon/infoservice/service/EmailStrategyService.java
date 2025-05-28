package com.andersenlab.etalon.infoservice.service;

import static com.andersenlab.etalon.infoservice.util.Constants.EMAIL_FROM;

import com.andersenlab.etalon.infoservice.dto.email.SendRawEmailRequestDto;
import com.andersenlab.etalon.infoservice.dto.request.BaseEmailRequestDto;
import com.andersenlab.etalon.infoservice.dto.request.ConfirmationEmailRequestDto;
import com.andersenlab.etalon.infoservice.dto.request.ConfirmationStatementDto;
import com.andersenlab.etalon.infoservice.dto.request.EmailModificationRequestDto;
import com.andersenlab.etalon.infoservice.dto.request.PasswordResetEmailRequestDto;
import com.andersenlab.etalon.infoservice.dto.request.RegistrationEmailRequestDto;
import com.andersenlab.etalon.infoservice.dto.request.SharingTransferReceiptDto;
import com.andersenlab.etalon.infoservice.exception.TechnicalException;
import com.andersenlab.etalon.infoservice.interceptor.AuthenticationHolder;
import com.andersenlab.etalon.infoservice.util.enums.EmailType;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.ses.model.SendRawEmailRequest;

@Service
@RequiredArgsConstructor
public class EmailStrategyService {
  private final Map<EmailType, MessageBuilderStrategy> messageBuilderStrategies = new HashMap<>();
  private final Session session;
  private final DocumentService documentService;
  private final AuthenticationHolder authenticationHolder;

  @Value("${app.urls.reset-password}")
  private String resetPasswordUrlPattern;

  @PostConstruct
  public void init() {
    messageBuilderStrategies.put(
        EmailType.CONFIRMATION,
        baseEmailRequestDto -> {
          ConfirmationEmailRequestDto confirmationEmailRequestDto =
              (ConfirmationEmailRequestDto) baseEmailRequestDto;

          return SendRawEmailRequestDto.builder()
              .from(EMAIL_FROM)
              .to(baseEmailRequestDto.getToEmail())
              .subject("Confirmation Code")
              .text(
                  "Your confirmation code is: " + confirmationEmailRequestDto.getVerificationCode())
              .build();
        });

    messageBuilderStrategies.put(
        EmailType.REGISTRATION,
        baseEmailRequestDto -> {
          RegistrationEmailRequestDto registrationEmailRequestDto =
              (RegistrationEmailRequestDto) baseEmailRequestDto;

          return SendRawEmailRequestDto.builder()
              .from(EMAIL_FROM)
              .to(baseEmailRequestDto.getToEmail())
              .subject("Registration Complete")
              .text(
                  "Thank you for registering! Your confirmation code is: "
                      + registrationEmailRequestDto.getVerificationCode())
              .build();
        });

    messageBuilderStrategies.put(
        EmailType.EMAIL_MODIFY_CONFIRMATION,
        baseEmailRequestDto -> {
          EmailModificationRequestDto emailModificationRequestDto =
              (EmailModificationRequestDto) baseEmailRequestDto;

          return SendRawEmailRequestDto.builder()
              .from(EMAIL_FROM)
              .to(baseEmailRequestDto.getToEmail())
              .subject("Email Modification")
              .text(
                  String.format(
                      "An email will be updated to: %s. Your confirmation code is: %s",
                      emailModificationRequestDto.getNewEmail(),
                      emailModificationRequestDto.getVerificationCode()))
              .build();
        });

    messageBuilderStrategies.put(
        EmailType.SHARING_TRANSFER_RECEIPT,
        baseEmailRequestDto -> {
          SharingTransferReceiptDto sharingTransferReceiptDto =
              (SharingTransferReceiptDto) baseEmailRequestDto;

          return SendRawEmailRequestDto.builder()
              .from(EMAIL_FROM)
              .to(baseEmailRequestDto.getToEmail())
              .subject("Transfer receipt")
              .text("Please find the transfer receipt attached to this mail")
              .attachment(
                  documentService.createTransferReceipt(
                      sharingTransferReceiptDto.getTransferId(), authenticationHolder.getUserId()))
              .build();
        });
    messageBuilderStrategies.put(
        EmailType.CONFIRMATION_STATEMENT,
        baseEmailRequestDto -> {
          ConfirmationStatementDto confirmationStatementDto =
              (ConfirmationStatementDto) baseEmailRequestDto;

          return SendRawEmailRequestDto.builder()
              .from(EMAIL_FROM)
              .to(baseEmailRequestDto.getToEmail())
              .subject("Confirmation statement")
              .text("Please find the confirmation statement attached to this mail")
              .attachment(
                  documentService.createTransactionConfirmation(
                      authenticationHolder.getUserId(),
                      confirmationStatementDto.getTransactionId(),
                      confirmationStatementDto.getLocale()))
              .build();
        });

    messageBuilderStrategies.put(
        EmailType.PASSWORD_RESET_CONFIRMATION,
        baseEmailRequestDto -> {
          PasswordResetEmailRequestDto passwordResetEmail =
              (PasswordResetEmailRequestDto) baseEmailRequestDto;

          String resetLink =
              String.format(resetPasswordUrlPattern, passwordResetEmail.getResetToken());

          String messageBody =
              """
                      Dear User,

                      We received a request to reset the password for your account associated with this email address. \
                      If you did not make this request, please ignore this email.

                      To reset your password, please click the link below:

                      %s

                      This link will expire in 1 hour. If you have any questions or need further assistance, \
                      please contact our support team.

                      Thank you,
                      Etalon Support Team
                  """
                  .formatted(resetLink);

          return SendRawEmailRequestDto.builder()
              .from(EMAIL_FROM)
              .to(baseEmailRequestDto.getToEmail())
              .subject("Password Reset Request")
              .text(messageBody)
              .build();
        });
  }

  public SendRawEmailRequest buildMessage(BaseEmailRequestDto baseEmailRequestDto) {
    MessageBuilderStrategy builder = messageBuilderStrategies.get(baseEmailRequestDto.getType());
    if (Objects.isNull(builder)) {
      throw new IllegalArgumentException(
          "No strategy found for email type: " + baseEmailRequestDto.getType());
    }
    SendRawEmailRequestDto sendRawEmailRequestDto = builder.build(baseEmailRequestDto);
    return buildSendRawEmailRequest(sendRawEmailRequestDto);
  }

  private SendRawEmailRequest buildSendRawEmailRequest(SendRawEmailRequestDto builder) {
    try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
      MimeMessageHelper mimeMessageHelper = getMimeMessageHelper(builder);
      mimeMessageHelper.getMimeMessage().writeTo(bos);
      SdkBytes bytes = SdkBytes.fromByteArray(bos.toByteArray());
      return SendRawEmailRequest.builder().rawMessage(raw -> raw.data(bytes)).build();
    } catch (Exception e) {
      throw new TechnicalException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  private MimeMessageHelper getMimeMessageHelper(SendRawEmailRequestDto builder)
      throws MessagingException {
    MimeMessage mimeMessage = new MimeMessage(session);
    MimeMessageHelper mimeMessageHelper =
        new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());
    mimeMessageHelper.setFrom(builder.getFrom());
    mimeMessageHelper.setTo(builder.getTo());
    mimeMessageHelper.setSubject(builder.getSubject());
    mimeMessageHelper.setText(builder.getText());
    if (Objects.nonNull(builder.getAttachment())) {
      mimeMessageHelper.addAttachment(
          builder.getAttachment().fileName(),
          builder.getAttachment().resource(),
          MediaType.APPLICATION_PDF_VALUE);
    }
    return mimeMessageHelper;
  }

  @FunctionalInterface
  public interface MessageBuilderStrategy {
    SendRawEmailRequestDto build(BaseEmailRequestDto baseEmailRequestDto);
  }
}

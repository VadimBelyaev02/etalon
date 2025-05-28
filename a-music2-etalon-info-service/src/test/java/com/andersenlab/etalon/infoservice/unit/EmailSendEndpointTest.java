package com.andersenlab.etalon.infoservice.unit;

import static com.andersenlab.etalon.infoservice.controller.EmailController.SEND_EMAIL;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.infoservice.annotation.WithUserId;
import com.andersenlab.etalon.infoservice.component.AbstractComponentTest;
import com.andersenlab.etalon.infoservice.controller.EmailController;
import com.andersenlab.etalon.infoservice.dto.request.ConfirmationEmailRequestDto;
import com.andersenlab.etalon.infoservice.util.enums.EmailType;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;

class EmailSendEndpointTest extends AbstractComponentTest {
  @Test
  @WithUserId
  void whenReceiveRequestToSendEmail_shouldAccept() throws Exception {

    ConfirmationEmailRequestDto confirmationEmailRequestDto = new ConfirmationEmailRequestDto();

    confirmationEmailRequestDto.setToEmail("test@example.com");
    confirmationEmailRequestDto.setSubject("subject");
    confirmationEmailRequestDto.setType(EmailType.CONFIRMATION);
    confirmationEmailRequestDto.setVerificationCode("1234");

    mockMvc
        .perform(
            post(UriComponentsBuilder.fromPath(EmailController.API_V1_URI + SEND_EMAIL)
                    .toUriString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(confirmationEmailRequestDto)))
        .andExpect(status().isAccepted());
  }
}

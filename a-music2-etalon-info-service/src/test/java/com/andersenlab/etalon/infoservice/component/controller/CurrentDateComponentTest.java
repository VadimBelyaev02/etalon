package com.andersenlab.etalon.infoservice.component.controller;

import static com.andersenlab.etalon.infoservice.controller.BankInfoController.SERVER_DATE_TIME_URL;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.andersenlab.etalon.infoservice.MockData;
import com.andersenlab.etalon.infoservice.component.AbstractComponentTest;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;
import org.springframework.web.util.UriComponentsBuilder;

class CurrentDateComponentTest extends AbstractComponentTest {

  @Test
  void whenRequestCurrentDate_shouldReturnCurrentDate() throws Exception {
    // given
    final String expected =
        MockData.getCurrentDate()
            .currentDate()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));

    // when/then
    mockMvc
        .perform(get(UriComponentsBuilder.fromPath(SERVER_DATE_TIME_URL).toUriString()))
        .andExpect(jsonPath("$.currentDate", is(expected)));
  }
}

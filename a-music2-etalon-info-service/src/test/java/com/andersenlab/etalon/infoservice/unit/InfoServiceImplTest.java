package com.andersenlab.etalon.infoservice.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.andersenlab.etalon.infoservice.MockData;
import com.andersenlab.etalon.infoservice.config.TimeProvider;
import com.andersenlab.etalon.infoservice.dto.response.DateTimeResponseDto;
import com.andersenlab.etalon.infoservice.service.impl.InfoServiceImpl;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InfoServiceImplTest {

  @Mock private TimeProvider timeProvider;
  @InjectMocks private InfoServiceImpl underTest;

  @Test
  void whenRequestCurrentDate_shouldReturnCurrentDate() {
    // given
    final DateTimeResponseDto expected = MockData.getCurrentDate();
    when(timeProvider.getCurrentZonedDateTime())
        .thenReturn(ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC")));

    // when
    DateTimeResponseDto currentDate = underTest.getCurrentDate();

    // then
    assertEquals(expected.currentDate().getDayOfWeek(), currentDate.currentDate().getDayOfWeek());
    assertEquals(expected.currentDate().getDayOfMonth(), currentDate.currentDate().getDayOfMonth());
    assertEquals(expected.currentDate().getDayOfYear(), currentDate.currentDate().getDayOfYear());
    assertEquals(expected.currentDate().getHour(), currentDate.currentDate().getHour());
  }
}

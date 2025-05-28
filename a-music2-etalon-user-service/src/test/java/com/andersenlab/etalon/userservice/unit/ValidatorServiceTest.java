package com.andersenlab.etalon.userservice.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.andersenlab.etalon.userservice.dto.info.response.StatusMessageResponseDto;
import com.andersenlab.etalon.userservice.exception.BusinessException;
import com.andersenlab.etalon.userservice.repository.UserRepository;
import com.andersenlab.etalon.userservice.service.impl.ValidatorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ValidatorServiceTest {

  private String email;
  private String pesel;

  @Mock private UserRepository userRepository;
  @InjectMocks private ValidatorServiceImpl underTest;

  @BeforeEach
  public void setUp() {
    email = "testexample@example.com";
    pesel = "12345678911";
  }

  @Test
  void whenEmailAvailableToRegister_shouldReturnTrueStatus() {
    // given
    final String response = "Email is available to register";
    given(userRepository.existsByEmail(eq(email))).willReturn(false);

    // when
    StatusMessageResponseDto result = underTest.isEmailAvailableToRegister(email);

    // then
    then(userRepository).should(times(1)).existsByEmail(email);
    assertTrue(result.status());
    assertEquals(response, result.message());
  }

  @Test
  void whenEmailNotAvailableToRegister_shouldThrowBusinessException() {
    // given
    given(userRepository.existsByEmail(email)).willThrow(BusinessException.class);

    // then
    assertThrows(BusinessException.class, () -> underTest.isEmailAvailableToRegister(email));
    verify(userRepository, times(1)).existsByEmail(email);
  }

  @Test
  void whenPeselAvailableToRegister_shouldReturnTrueStatus() {
    // given
    final String response = "Pesel is available to register";
    given(userRepository.existsByPesel(anyString())).willReturn(false);

    // when
    StatusMessageResponseDto result = underTest.isPeselAvailableToRegister(pesel);

    // then
    then(userRepository).should(times(1)).existsByPesel(pesel);
    assertTrue(result.status());
    assertEquals(response, result.message());
  }

  @Test
  void whenPeselNotAvailableToRegister_shouldThrowBusinessException() {
    // given
    given(userRepository.existsByPesel(pesel)).willThrow(BusinessException.class);

    // when
    assertThrows(BusinessException.class, () -> underTest.isPeselAvailableToRegister(pesel));
    verify(userRepository, times(1)).existsByPesel(pesel);
  }
}

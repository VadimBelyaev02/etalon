package com.andersenlab.etalon.userservice.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.andersenlab.etalon.userservice.MockData;
import com.andersenlab.etalon.userservice.dto.user.request.UpdateRegistrationOrderStatusRequestDto;
import com.andersenlab.etalon.userservice.dto.user.response.RegistrationOrderResponseDto;
import com.andersenlab.etalon.userservice.entity.RegistrationOrder;
import com.andersenlab.etalon.userservice.exception.BusinessException;
import com.andersenlab.etalon.userservice.mapper.RegistrationOrderMapper;
import com.andersenlab.etalon.userservice.repository.RegistrationOrderRepository;
import com.andersenlab.etalon.userservice.service.impl.RegistrationOrderServiceImpl;
import com.andersenlab.etalon.userservice.util.OrderStatus;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class RegistrationOrderServiceImplTest {

  private static final String REGISTRATION_ID = "36c50b72-ada5-4407-b896-20649ba633a2";

  private RegistrationOrder existingOrder;
  private RegistrationOrder updatedOrder;
  private RegistrationOrder savedOrder;
  private UpdateRegistrationOrderStatusRequestDto updateRegistrationOrderStatus;

  @Mock private RegistrationOrderRepository registrationOrderRepository;

  @Mock private RegistrationOrderMapper registrationOrderMapper;

  @InjectMocks private RegistrationOrderServiceImpl registrationOrderService;

  @BeforeEach
  void setUp() {
    existingOrder = MockData.getValidRegistrationOrder();
    updatedOrder = MockData.getValidUpdatedRegistrationOrder();
    savedOrder = MockData.getValidSavedRegistrationOrder();
    updateRegistrationOrderStatus = MockData.getValidUpdateRegistrationOrderStatus();
  }

  @Test
  void updateRegistrationOrderStatus_shouldUpdateStatusAndReturnDto() {
    // Arrange
    RegistrationOrderResponseDto responseDto =
        new RegistrationOrderResponseDto("96070642313", OrderStatus.COMPLETED, REGISTRATION_ID);

    when(registrationOrderRepository.findByRegistrationId(REGISTRATION_ID))
        .thenReturn(Optional.of(existingOrder));
    when(registrationOrderRepository.save(any(RegistrationOrder.class))).thenReturn(updatedOrder);
    when(registrationOrderMapper.toDto(updatedOrder)).thenReturn(responseDto);

    // Act
    RegistrationOrderResponseDto result =
        registrationOrderService.updateRegistrationOrderStatus(updateRegistrationOrderStatus);

    // Assert
    assertNotNull(result);
    assertEquals(responseDto, result);
    assertEquals(OrderStatus.COMPLETED, result.orderStatus());

    verify(registrationOrderRepository, times(2)).findByRegistrationId(REGISTRATION_ID);
    verify(registrationOrderRepository, times(1)).save(any(RegistrationOrder.class));
    verify(registrationOrderMapper, times(1)).toDto(updatedOrder);
  }

  @Test
  void updateRegistrationOrder_shouldUpdateAndReturnRegistrationOrder() {
    when(registrationOrderRepository.findByRegistrationId(REGISTRATION_ID))
        .thenReturn(Optional.of(existingOrder));
    when(registrationOrderRepository.save(any(RegistrationOrder.class))).thenReturn(savedOrder);

    RegistrationOrder result = registrationOrderService.updateRegistrationOrder(updatedOrder);

    assertNotNull(result);
    assertEquals(existingOrder.getId(), result.getId());
    assertEquals(updatedOrder.getPesel(), result.getPesel());
    assertEquals(updatedOrder.getOrderStatus(), result.getOrderStatus());
    assertEquals(updatedOrder.getRegistrationId(), result.getRegistrationId());

    verify(registrationOrderRepository).findByRegistrationId(REGISTRATION_ID);
    verify(registrationOrderRepository).save(any(RegistrationOrder.class));
  }

  @Test
  void testGetRegistrationOrderByRegistrationId_whenFound_shouldReturnOrder() {
    RegistrationOrder mockOrder = new RegistrationOrder();
    mockOrder.setRegistrationId(REGISTRATION_ID);
    when(registrationOrderRepository.findByRegistrationId(REGISTRATION_ID))
        .thenReturn(Optional.of(mockOrder));

    RegistrationOrder result =
        registrationOrderService.getRegistrationOrderByRegistrationId(REGISTRATION_ID);

    assertNotNull(result);
    assertEquals(REGISTRATION_ID, result.getRegistrationId());
    verify(registrationOrderRepository, times(1)).findByRegistrationId(REGISTRATION_ID);
  }

  @Test
  void testGetRegistrationOrderByRegistrationId_whenNotFound_shouldThrowBusinessException() {
    when(registrationOrderRepository.findByRegistrationId(REGISTRATION_ID))
        .thenReturn(Optional.empty());

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> registrationOrderService.getRegistrationOrderByRegistrationId(REGISTRATION_ID));

    assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    assertEquals(
        "Registration order with id-36c50b72-ada5-4407-b896-20649ba633a2 not found",
        exception.getMessage());
    verify(registrationOrderRepository, times(1)).findByRegistrationId(REGISTRATION_ID);
  }

  @Test
  void testGetRegistrationOrderById_whenFound_shouldReturnOrder() {
    Long id = 1L;
    RegistrationOrder mockOrder = MockData.getValidRegistrationOrder();
    when(registrationOrderRepository.findById(id)).thenReturn(Optional.of(mockOrder));

    RegistrationOrder result = registrationOrderService.getRegistrationOrderById(id);

    assertNotNull(result);
    assertEquals(id, result.getId());
    assertEquals("96070642313", result.getPesel());
    verify(registrationOrderRepository, times(1)).findById(id);
  }

  @Test
  void testGetRegistrationOrderById_whenNotFound_shouldThrowBusinessException() {
    long id = 0L;
    when(registrationOrderRepository.findById(id)).thenReturn(Optional.empty());

    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> registrationOrderService.getRegistrationOrderById(id));

    assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    assertEquals("No registration order found by id-0", exception.getMessage());
    verify(registrationOrderRepository, times(1)).findById(id);
  }
}

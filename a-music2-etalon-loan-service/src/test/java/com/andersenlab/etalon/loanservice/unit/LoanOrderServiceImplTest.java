package com.andersenlab.etalon.loanservice.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.andersenlab.etalon.loanservice.MockData;
import com.andersenlab.etalon.loanservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.loanservice.dto.loan.request.LoanOrderRequestDto;
import com.andersenlab.etalon.loanservice.dto.loan.response.LoanOrderDetailedResponseDto;
import com.andersenlab.etalon.loanservice.dto.loan.response.LoanOrderResponseDto;
import com.andersenlab.etalon.loanservice.entity.GuarantorEntity;
import com.andersenlab.etalon.loanservice.entity.LoanOrderEntity;
import com.andersenlab.etalon.loanservice.entity.LoanProductEntity;
import com.andersenlab.etalon.loanservice.exception.BusinessException;
import com.andersenlab.etalon.loanservice.mapper.GuarantorMapper;
import com.andersenlab.etalon.loanservice.mapper.LoanOrderMapper;
import com.andersenlab.etalon.loanservice.repository.GuarantorRepository;
import com.andersenlab.etalon.loanservice.repository.LoanOrderRepository;
import com.andersenlab.etalon.loanservice.repository.LoanProductRepository;
import com.andersenlab.etalon.loanservice.service.ValidationService;
import com.andersenlab.etalon.loanservice.service.impl.LoanOrderServiceImpl;
import com.andersenlab.etalon.loanservice.util.LoanOrderUtils;
import com.andersenlab.etalon.loanservice.util.enums.OrderStatus;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class LoanOrderServiceImplTest {

  private static final String USER_ID = "user";
  private static final Long ORDER_ID = 1L;
  private static final Long PRODUCT_ID = 2L;

  private LoanOrderEntity loanOrderEntity;
  private LoanProductEntity loanProductEntity;
  private LoanOrderRequestDto loanOrderRequestDto;

  @Mock private LoanOrderRepository loanOrderRepository;
  @Mock private LoanProductRepository loanProductRepository;
  @Mock private GuarantorRepository guarantorRepository;
  @Mock private ValidationService validationService;
  @Spy private LoanOrderMapper loanOrderMapper = Mappers.getMapper(LoanOrderMapper.class);
  @Spy private GuarantorMapper guarantorMapper = Mappers.getMapper(GuarantorMapper.class);
  @InjectMocks private LoanOrderServiceImpl underTest;

  @BeforeEach
  void setUp() {
    loanOrderEntity = MockData.getValidLoanOrderEntity();
    loanOrderRequestDto = MockData.getValidLoanOrderRequestDto();
    loanProductEntity = MockData.getValidLoanProductEntity();
  }

  @Test
  void whenGetAllLoanOrdersByUserId_shouldSuccess() {
    // given
    when(loanOrderRepository.findAllByUserId(USER_ID)).thenReturn(List.of(loanOrderEntity));

    // when
    final List<LoanOrderResponseDto> result = underTest.getAllLoanOrdersByUserId(USER_ID);

    // then
    verify(loanOrderRepository, times(1)).findAllByUserId(USER_ID);
    assertEquals(loanOrderEntity.getId(), result.get(0).id());
    assertEquals(loanOrderEntity.getProduct().getName(), result.get(0).productName());
    assertEquals(loanOrderEntity.getProduct().getDuration(), result.get(0).duration());
    assertEquals(loanOrderEntity.getAmount(), result.get(0).amount());
    assertEquals(loanOrderEntity.getStatus(), result.get(0).status());
  }

  @Test
  void whenGetNoLoanOrders_thenReturnEmptyList() {
    // given
    when(loanOrderRepository.findAllByUserId(USER_ID)).thenReturn(List.of());

    // when
    final List<LoanOrderResponseDto> result = underTest.getAllLoanOrdersByUserId(USER_ID);

    // then
    verify(loanOrderRepository, times(1)).findAllByUserId(USER_ID);
    assertEquals(List.of(), result);
  }

  @Test
  void whenGetLoanOrderDetailed_thenSuccess() {
    // given
    when(loanOrderRepository.findByIdAndUserId(ORDER_ID, USER_ID))
        .thenReturn(Optional.ofNullable(loanOrderEntity));

    // when
    final LoanOrderDetailedResponseDto result = underTest.getLoanOrderDetailed(ORDER_ID, USER_ID);

    // then
    verify(loanOrderRepository, times(1)).findByIdAndUserId(ORDER_ID, USER_ID);
    assertEquals(loanOrderEntity.getId(), result.id());
    assertEquals(loanOrderEntity.getAmount(), result.amount());
    assertEquals(loanOrderEntity.getStatus(), result.status());
    assertEquals(loanOrderEntity.getProduct().getName(), result.productName());
    assertEquals(loanOrderEntity.getGuarantors().size(), result.guarantors().size());
  }

  @Test
  void whenGetLoanOrderDetailed_thenThrowBusinessException() {
    // given
    when(loanOrderRepository.findByIdAndUserId(ORDER_ID, USER_ID))
        .thenThrow(
            new BusinessException(
                HttpStatus.NOT_FOUND,
                String.format(BusinessException.LOAN_ORDER_NOT_FOUND_BY_ID, ORDER_ID)));

    // then
    assertThrows(BusinessException.class, () -> underTest.getLoanOrderDetailed(ORDER_ID, USER_ID));
    verify(loanOrderRepository, times(1)).findByIdAndUserId(ORDER_ID, USER_ID);
  }

  @Test
  void whenCreateNewLoanOrder_ThenSuccess() {
    // given
    final Set<GuarantorEntity> guarantorEntities =
        guarantorMapper.setOfDtosToEntities(loanOrderRequestDto.guarantors());
    when(loanProductRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(loanProductEntity));
    when(guarantorRepository.findAllByPeselIn(anySet())).thenReturn(guarantorEntities);

    // when
    final MessageResponseDto response = underTest.createNewLoanOrder(USER_ID, loanOrderRequestDto);

    // then
    verify(validationService, times(1)).validateLoanOrderRequestDto(loanOrderRequestDto);
    verify(loanOrderRepository, times(1)).save(any(LoanOrderEntity.class));
    assertEquals(MessageResponseDto.SEND_LOAN_ORDER_TO_REVIEW, response.message());
  }

  @Test
  void whenCreateLoanOrderEntity_ThenSuccess() {
    // given
    final Set<GuarantorEntity> guarantorEntities =
        guarantorMapper.setOfDtosToEntities(loanOrderRequestDto.guarantors());
    when(loanProductRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(loanProductEntity));
    when(guarantorRepository.findAllByPeselIn(anySet())).thenReturn(guarantorEntities);

    // when
    final LoanOrderEntity loanOrderEntity =
        underTest.createLoanOrderEntity(USER_ID, loanOrderRequestDto);

    // then
    assertEquals(USER_ID, loanOrderEntity.getUserId());
    assertEquals(OrderStatus.IN_REVIEW, loanOrderEntity.getStatus());
  }

  @Test
  void whenGetExistingAndNewGuarantors_ThenSuccess() {
    // given
    final Set<GuarantorEntity> guarantorEntities =
        guarantorMapper.setOfDtosToEntities(loanOrderRequestDto.guarantors());
    when(guarantorRepository.findAllByPeselIn(
            LoanOrderUtils.collectPeselsFromGuarantors(loanOrderRequestDto.guarantors())))
        .thenReturn(guarantorEntities);

    // when
    underTest.getExistingAndNewGuarantors(loanOrderRequestDto.guarantors());

    // then
    verify(guarantorRepository, times(1)).findAllByPeselIn(anySet());
    verify(guarantorMapper, times(2)).setOfDtosToEntities(loanOrderRequestDto.guarantors());
  }
}

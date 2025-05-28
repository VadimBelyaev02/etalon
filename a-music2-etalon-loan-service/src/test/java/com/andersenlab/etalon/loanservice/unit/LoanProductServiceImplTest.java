package com.andersenlab.etalon.loanservice.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.andersenlab.etalon.loanservice.MockData;
import com.andersenlab.etalon.loanservice.dto.loan.response.LoanProductResponseDto;
import com.andersenlab.etalon.loanservice.entity.LoanProductEntity;
import com.andersenlab.etalon.loanservice.mapper.LoanProductMapper;
import com.andersenlab.etalon.loanservice.repository.LoanProductRepository;
import com.andersenlab.etalon.loanservice.service.impl.LoanProductServiceImpl;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoanProductServiceImplTest {

  @Mock private LoanProductRepository loanProductRepository;
  @Spy private LoanProductMapper mapper = Mappers.getMapper(LoanProductMapper.class);
  @InjectMocks private LoanProductServiceImpl underTest;
  private LoanProductEntity loanProductEntity;

  @BeforeEach
  void setUp() {
    loanProductEntity = MockData.getValidLoanProductEntity();
  }

  @Test
  void whenGetAllLoanProducts_shouldSuccess() {
    // given
    when(loanProductRepository.findAll()).thenReturn(List.of(loanProductEntity));

    // when
    final List<LoanProductResponseDto> result = underTest.getAllLoanProducts();

    // then
    verify(loanProductRepository, times(1)).findAll();
    assertEquals(loanProductEntity.getId(), result.get(0).id());
    assertEquals(loanProductEntity.getName(), result.get(0).name());
    assertEquals(loanProductEntity.getDuration(), result.get(0).duration());
    assertEquals(loanProductEntity.getApr(), result.get(0).apr());
    assertEquals(loanProductEntity.getRequiredGuarantors(), result.get(0).requiredGuarantors());
    assertEquals(loanProductEntity.getMinAmount(), result.get(0).minAmount());
    assertEquals(loanProductEntity.getMaxAmount(), result.get(0).maxAmount());
    assertEquals(loanProductEntity.getMonthlyCommission(), result.get(0).monthlyCommission());
  }

  @Test
  void whenGetNoLoanProducts_thenReturnEmptyList() {
    // given
    when(loanProductRepository.findAll()).thenReturn(List.of());

    // when
    final List<LoanProductResponseDto> result = underTest.getAllLoanProducts();

    // then
    verify(loanProductRepository, times(1)).findAll();
    assertEquals(List.of(), result);
  }
}

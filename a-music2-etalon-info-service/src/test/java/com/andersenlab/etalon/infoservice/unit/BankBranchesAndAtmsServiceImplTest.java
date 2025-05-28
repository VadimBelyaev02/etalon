package com.andersenlab.etalon.infoservice.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.andersenlab.etalon.infoservice.MockData;
import com.andersenlab.etalon.infoservice.dto.response.BankBranchesAndAtmsResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.BankBranchesResponseDto;
import com.andersenlab.etalon.infoservice.entity.AtmEntity;
import com.andersenlab.etalon.infoservice.entity.BankBranchEntity;
import com.andersenlab.etalon.infoservice.exception.BusinessException;
import com.andersenlab.etalon.infoservice.mapper.BankBranchesAndAtmsMapper;
import com.andersenlab.etalon.infoservice.repository.AtmRepository;
import com.andersenlab.etalon.infoservice.repository.BankBranchRepository;
import com.andersenlab.etalon.infoservice.service.impl.BankBranchesAndAtmsServiceImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BankBranchesAndAtmsServiceImplTest {
  private final Long INVALID_BRANCH_ID = 700L;
  private AtmEntity atm;
  private BankBranchEntity bankBranch;
  @Mock private AtmRepository atmRepository;
  @Mock private BankBranchRepository bankBranchRepository;

  @Spy
  private BankBranchesAndAtmsMapper mapper = Mappers.getMapper(BankBranchesAndAtmsMapper.class);

  @InjectMocks private BankBranchesAndAtmsServiceImpl underTest;

  @BeforeEach
  void setUp() {
    atm = MockData.getValidAtmEntity();
    bankBranch = MockData.getValidBankBranchEntity();
  }

  @Test
  void whenGetAllBranchesAndAtms_shouldSuccess() {
    // given
    when(atmRepository.findAll()).thenReturn(List.of(atm));
    when(bankBranchRepository.findAll()).thenReturn(List.of(bankBranch));

    // when
    final List<BankBranchesAndAtmsResponseDto> result = underTest.getAllBranchesAndAtmsByCity(null);

    // then
    verify(atmRepository, times(1)).findAll();
    verify(bankBranchRepository, times(1)).findAll();
    assertEquals(atm.getAtmName(), result.get(1).name());
    assertEquals(atm.getCity(), result.get(1).city());
    assertEquals(atm.getAddress(), result.get(1).address());
    assertEquals(bankBranch.getBankBranchName(), result.get(0).name());
    assertEquals(bankBranch.getCity(), result.get(0).city());
    assertEquals(bankBranch.getAddress(), result.get(0).address());
  }

  @Test
  void whenGetAllBranchesAndAtmsByCityByCity_shouldSuccess() {
    // given
    String CITY = "Gdansk";
    when(atmRepository.findAllByCity(CITY)).thenReturn(List.of(atm));
    when(bankBranchRepository.findAllByCity(CITY)).thenReturn(List.of(bankBranch));

    // when
    final List<BankBranchesAndAtmsResponseDto> result = underTest.getAllBranchesAndAtmsByCity(CITY);

    // then
    verify(atmRepository, times(1)).findAllByCity(CITY);
    verify(bankBranchRepository, times(1)).findAllByCity(CITY);
    assertEquals(atm.getAtmName(), result.get(1).name());
    assertEquals(atm.getCity(), result.get(1).city());
    assertEquals(atm.getAddress(), result.get(1).address());
    assertEquals(bankBranch.getBankBranchName(), result.get(0).name());
    assertEquals(bankBranch.getCity(), result.get(0).city());
    assertEquals(bankBranch.getAddress(), result.get(0).address());
  }

  @Test
  void whenGetAllCities_shouldSuccess() {
    // given
    when(atmRepository.findAll()).thenReturn(List.of(atm));
    when(bankBranchRepository.findAll()).thenReturn(List.of(bankBranch));

    // when
    final List<String> result = underTest.getAllCities();

    // then
    verify(atmRepository, times(1)).findAll();
    verify(bankBranchRepository, times(1)).findAll();
    assertEquals(atm.getCity(), result.get(0));
    assertEquals(bankBranch.getCity(), result.get(0));
  }

  @Test
  void whenGetBankBranchByValidId_thenSuccessesAndReturnBankBranch() {
    // given
    Long VALID_BRANCH_ID = 7L;
    when(bankBranchRepository.findById(VALID_BRANCH_ID))
        .thenReturn(Optional.ofNullable(bankBranch));

    // when
    final BankBranchesResponseDto result = underTest.getBankBranchById(VALID_BRANCH_ID);

    // then
    verify(bankBranchRepository, times(1)).findById(VALID_BRANCH_ID);
    assertEquals(bankBranch.getId(), result.id());
  }

  @Test
  void whenGetBankBranchByInvalidId_thenReturnBusinessException() {
    // given
    when(bankBranchRepository.findById(INVALID_BRANCH_ID)).thenThrow(BusinessException.class);

    // when/then
    assertThrows(BusinessException.class, () -> underTest.getBankBranchById(INVALID_BRANCH_ID));
  }
}

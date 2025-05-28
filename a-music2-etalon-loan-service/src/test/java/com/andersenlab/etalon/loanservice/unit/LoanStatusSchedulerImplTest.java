package com.andersenlab.etalon.loanservice.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.andersenlab.etalon.loanservice.config.TimeProvider;
import com.andersenlab.etalon.loanservice.entity.LoanEntity;
import com.andersenlab.etalon.loanservice.repository.LoanRepository;
import com.andersenlab.etalon.loanservice.service.impl.LoanStatusSchedulerImpl;
import com.andersenlab.etalon.loanservice.util.enums.LoanStatus;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoanStatusSchedulerImplTest {

  @Mock private LoanRepository loanRepository;

  @Mock private TimeProvider timeProvider;

  @InjectMocks private LoanStatusSchedulerImpl loanStatusScheduler;

  @Test
  void updateDelinquentLoans_shouldUpdateLoanStatus() {
    // given
    ZonedDateTime currentDateTime = ZonedDateTime.now();
    when(timeProvider.getCurrentZonedDateTime()).thenReturn(currentDateTime);

    LoanEntity loan1 =
        LoanEntity.builder()
            .id(1L)
            .status(LoanStatus.ACTIVE)
            .nextPaymentDate(currentDateTime.minusDays(1))
            .build();

    LoanEntity loan2 =
        LoanEntity.builder()
            .id(2L)
            .status(LoanStatus.ACTIVE)
            .nextPaymentDate(currentDateTime.minusDays(2))
            .build();

    List<LoanEntity> loansToUpdate = Arrays.asList(loan1, loan2);

    when(loanRepository.findAllByStatusAndNextPaymentDateBefore(LoanStatus.ACTIVE, currentDateTime))
        .thenReturn(loansToUpdate);

    // when
    loanStatusScheduler.updateDelinquentLoans();

    // then
    ArgumentCaptor<LoanEntity> loanCaptor = ArgumentCaptor.forClass(LoanEntity.class);
    verify(loanRepository, times(2)).save(loanCaptor.capture());

    List<LoanEntity> savedLoans = loanCaptor.getAllValues();

    for (LoanEntity savedLoan : savedLoans) {
      assertEquals(LoanStatus.DELINQUENT, savedLoan.getStatus());
      assertEquals(currentDateTime, savedLoan.getUpdateAt());
    }

    verifyNoMoreInteractions(loanRepository);
  }
}

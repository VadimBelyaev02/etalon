package com.andersenlab.etalon.loanservice.service.impl;

import com.andersenlab.etalon.loanservice.config.TimeProvider;
import com.andersenlab.etalon.loanservice.entity.LoanEntity;
import com.andersenlab.etalon.loanservice.repository.LoanRepository;
import com.andersenlab.etalon.loanservice.util.enums.LoanStatus;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanStatusSchedulerImpl {

  private final LoanRepository loanRepository;
  private final TimeProvider timeProvider;

  @Scheduled(cron = "0 0 8 * * ?", zone = "${app.default.timezone}")
  public void updateDelinquentLoans() {
    ZonedDateTime currentDateTime = timeProvider.getCurrentZonedDateTime();

    List<LoanEntity> loansToUpdate =
        loanRepository.findAllByStatusAndNextPaymentDateBefore(LoanStatus.ACTIVE, currentDateTime);

    for (LoanEntity loan : loansToUpdate) {
      log.info(
          "{updateDelinquentLoans()} -> loan {} status will be changed from ACTIVE to DELINQUENT",
          loan.getId());
      loan.setStatus(LoanStatus.DELINQUENT);
      loan.setUpdateAt(currentDateTime);
      loanRepository.save(loan);
    }
  }
}

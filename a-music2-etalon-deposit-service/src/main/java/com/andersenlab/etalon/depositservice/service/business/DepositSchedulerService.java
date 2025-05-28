package com.andersenlab.etalon.depositservice.service.business;

public interface DepositSchedulerService {

  void calculateAndTransferMonthlyInterest();

  void withdrawDepositsAfterExpiration();

  void calculateAndSaveDepositInterest();
}

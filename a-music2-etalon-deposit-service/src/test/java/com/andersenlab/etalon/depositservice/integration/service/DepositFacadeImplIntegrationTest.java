package com.andersenlab.etalon.depositservice.integration.service;

import static com.andersenlab.etalon.depositservice.client.TransactionServiceClient.TRANSACTIONS_URL;
import static com.github.tomakehurst.wiremock.client.WireMock.moreThanOrExactly;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.andersenlab.etalon.depositservice.entity.DepositEntity;
import com.andersenlab.etalon.depositservice.entity.DepositInterestEntity;
import com.andersenlab.etalon.depositservice.integration.AbstractIntegrationTest;
import com.andersenlab.etalon.depositservice.repository.DepositInterestRepository;
import com.andersenlab.etalon.depositservice.repository.DepositRepository;
import com.andersenlab.etalon.depositservice.service.business.DepositSchedulerService;
import com.andersenlab.etalon.depositservice.util.enums.DepositStatus;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Sql(
    scripts = {
      "classpath:data/deposit-products-initial-data.sql",
      "classpath:data/deposit-initial-data.sql",
      "classpath:data/deposit-interest-initial-data.sql"
    },
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class DepositFacadeImplIntegrationTest extends AbstractIntegrationTest {
  private static final String PATH = "/transaction";

  @Autowired private DepositInterestRepository depositInterestRepository;
  @Autowired private DepositRepository depositRepository;
  @Autowired private DepositSchedulerService depositSchedulerService;

  @Test
  void whenCalculateAndSaveDepositInterest_shouldSave() {
    // when
    depositSchedulerService.calculateAndSaveDepositInterest();
    List<DepositInterestEntity> result = depositInterestRepository.findAll();

    // then
    assertFalse(result.isEmpty());
    assertNotNull(
        result.stream()
            .map(DepositInterestEntity::getInterestAmount)
            .filter(y -> y.equals(BigDecimal.valueOf(0.94)))
            .toList()
            .get(0));
  }

  @Test
  @Transactional(propagation = Propagation.NOT_SUPPORTED)
  void whenCalculateAndTransferMonthlyInterest_shouldSave() {
    // when
    List<DepositEntity> depositEntities = depositRepository.findAll();
    depositRepository.deleteAll();
    depositRepository.saveAll(depositEntities);
    depositSchedulerService.calculateAndTransferMonthlyInterest();
    await()
        .atMost(Duration.ofSeconds(10))
        .with()
        .pollDelay(Duration.ofSeconds(1))
        .untilAsserted(
            () ->
                mockTransactionService.verify(
                    moreThanOrExactly(1), postRequestedFor(urlEqualTo(PATH + TRANSACTIONS_URL))));
  }

  @Test
  void whenWithdrawDepositsAfterExpiration_shouldSave() {
    // when
    depositSchedulerService.withdrawDepositsAfterExpiration();
    DepositEntity actualResult =
        depositRepository.findById(3L).isPresent() ? depositRepository.findById(3L).get() : null;

    assertNotNull(actualResult);
    assertEquals(DepositStatus.EXPIRED.name(), actualResult.getStatus().name());
  }
}

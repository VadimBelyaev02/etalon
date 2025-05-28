package com.andersenlab.etalon.transactionservice.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.andersenlab.etalon.transactionservice.MockData;
import com.andersenlab.etalon.transactionservice.dto.common.FeeDto;
import com.andersenlab.etalon.transactionservice.entity.TransferEntity;
import com.andersenlab.etalon.transactionservice.service.impl.FeeServiceImpl;
import java.math.BigDecimal;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class FeeServiceImplTest {

  private TransferEntity transferEntity;
  private final FeeServiceImpl feeService = new FeeServiceImpl();

  @BeforeEach
  void setUp() {
    transferEntity = MockData.getValidTransferEntity();
  }

  @ParameterizedTest
  @MethodSource("feeArgsLocalBank")
  void whenCreateTransaction_thenFeeAmountSuccess(BigDecimal amount, BigDecimal expectedFee) {
    TransferEntity transfer = transferEntity.toBuilder().amount(amount).isFeeProvided(true).build();
    FeeDto resultFee = feeService.calculateFeeLocalBank(transfer.getAmount());
    assertEquals(expectedFee.setScale(2), resultFee.amount().setScale(2));
  }

  @ParameterizedTest
  @MethodSource("feeArgsForeignBank")
  void whenCreateTransactionForeignBank_thenFeeAmountSuccess(
      BigDecimal amount, BigDecimal expectedFee) {
    TransferEntity transfer = transferEntity.toBuilder().amount(amount).isFeeProvided(true).build();
    FeeDto resultFee = feeService.calculateFeeForeignBank(transfer.getAmount());
    assertEquals(expectedFee.setScale(2), resultFee.amount().setScale(2));
  }

  private static Stream<Arguments> feeArgsLocalBank() {
    return Stream.of(
        Arguments.of(BigDecimal.valueOf(1000.00), BigDecimal.valueOf(10.00)),
        Arguments.of(BigDecimal.valueOf(100.00), BigDecimal.valueOf(1.00)));
  }

  private static Stream<Arguments> feeArgsForeignBank() {
    return Stream.of(
        Arguments.of(BigDecimal.valueOf(1000.00), BigDecimal.valueOf(20.00)),
        Arguments.of(BigDecimal.valueOf(100.00), BigDecimal.valueOf(2.00)));
  }
}

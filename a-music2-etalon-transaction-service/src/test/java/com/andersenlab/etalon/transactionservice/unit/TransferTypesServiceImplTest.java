package com.andersenlab.etalon.transactionservice.unit;

import static org.mockito.Mockito.when;

import com.andersenlab.etalon.transactionservice.MockData;
import com.andersenlab.etalon.transactionservice.dto.account.response.AccountNumberResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.TransferTypeResponseDto;
import com.andersenlab.etalon.transactionservice.entity.TransferTypeEntity;
import com.andersenlab.etalon.transactionservice.mapper.TransferTypeMapper;
import com.andersenlab.etalon.transactionservice.repository.TransferTypeRepository;
import com.andersenlab.etalon.transactionservice.service.AccountService;
import com.andersenlab.etalon.transactionservice.service.impl.TransferServiceImpl;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransferTypesServiceImplTest {
  private static final String USER_ID = "1L";
  private TransferTypeResponseDto transferTypeResponseDtoToAnotherAccount;
  private TransferTypeResponseDto transferTypeResponseDtoToCard;
  private TransferTypeResponseDto transferTypeResponseDtoToMyAccount;
  private TransferTypeEntity transferTypeToAnotherAccount;
  private TransferTypeEntity transferTypeToCard;
  private TransferTypeEntity transferTypeToMyAccount;

  @Spy
  private com.andersenlab.etalon.transactionservice.mapper.TransferTypeMapper transferTypeMapper =
      Mappers.getMapper(TransferTypeMapper.class);

  @Mock private TransferTypeRepository transferTypeRepository;
  @Mock private AccountService accountService;
  @InjectMocks private TransferServiceImpl transferService;

  @BeforeEach
  void setUp() {
    transferTypeToAnotherAccount = MockData.getValidTransferToAnotherAccountTypeEntity();
    transferTypeToCard = MockData.getValidTransferToCardTypeEntity();
    transferTypeToMyAccount = MockData.getValidTransferToMyAccountTypeEntity();
    transferTypeResponseDtoToAnotherAccount =
        MockData.getValidTransferTypeToAnotherAccountResponseDto();
    transferTypeResponseDtoToCard = MockData.getValidTransferTypeToCardResponseDto();
    transferTypeResponseDtoToMyAccount = MockData.getValidTransferTypeToMyAccountResponseDto();
  }

  @Test
  void getAllPaymentsTypeTestForUserWithTwoAccounts() {
    when(transferTypeRepository.findAll())
        .thenReturn(
            List.of(transferTypeToAnotherAccount, transferTypeToCard, transferTypeToMyAccount));
    when(accountService.getAllAccountNumbers(USER_ID))
        .thenReturn(List.of(new AccountNumberResponseDto("1"), new AccountNumberResponseDto("2")));

    Assertions.assertEquals(
        transferTypeResponseDtoToAnotherAccount,
        transferService.getAllTransferTypes(USER_ID).get(0));
    Assertions.assertEquals(
        transferTypeResponseDtoToCard, transferService.getAllTransferTypes(USER_ID).get(1));
    Assertions.assertEquals(
        transferTypeResponseDtoToMyAccount, transferService.getAllTransferTypes(USER_ID).get(2));
    Assertions.assertEquals(3, transferService.getAllTransferTypes(USER_ID).size());
  }

  @Test
  void getAllPaymentsTypeTestForUserWithOneAccount() {
    when(transferTypeRepository.findAll())
        .thenReturn(
            List.of(transferTypeToAnotherAccount, transferTypeToCard, transferTypeToMyAccount));
    when(accountService.getAllAccountNumbers(USER_ID))
        .thenReturn(List.of(new AccountNumberResponseDto("1")));

    Assertions.assertEquals(
        transferTypeResponseDtoToAnotherAccount,
        transferService.getAllTransferTypes(USER_ID).get(0));
    Assertions.assertEquals(
        transferTypeResponseDtoToCard, transferService.getAllTransferTypes(USER_ID).get(1));
    Assertions.assertEquals(2, transferService.getAllTransferTypes(USER_ID).size());
  }
}

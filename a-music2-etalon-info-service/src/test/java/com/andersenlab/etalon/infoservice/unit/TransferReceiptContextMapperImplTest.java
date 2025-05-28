package com.andersenlab.etalon.infoservice.unit;

import static com.andersenlab.etalon.infoservice.MockData.getTransferReceiptContextDto;
import static org.assertj.core.api.Assertions.assertThat;

import com.andersenlab.etalon.infoservice.MockData;
import com.andersenlab.etalon.infoservice.dto.common.TransferReceiptContext;
import com.andersenlab.etalon.infoservice.dto.response.TransferResponseDto;
import com.andersenlab.etalon.infoservice.mapper.TransferReceiptContextMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class TransferReceiptContextMapperImplTest {
  TransferReceiptContextMapper target = Mappers.getMapper(TransferReceiptContextMapper.class);

  @Test
  void
      whenToTransferReceiptContextWithValidTransferResponseDto_thenReturnValidTransferReceiptContext() {
    final TransferReceiptContext expected = getTransferReceiptContextDto();
    // when
    final TransferResponseDto transferResponseDto = MockData.getValidTransferResponseDto();
    final TransferReceiptContext actual = target.toTransferReceiptContext(transferResponseDto);
    // then
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void whenToTransferReceiptContextWithNullValue_thenReturnNull() {
    // when/then
    final TransferReceiptContext actual = target.toTransferReceiptContext(null);
    assertThat(actual).isNull();
  }
}

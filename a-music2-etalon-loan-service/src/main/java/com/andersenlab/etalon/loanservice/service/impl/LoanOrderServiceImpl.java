package com.andersenlab.etalon.loanservice.service.impl;

import com.andersenlab.etalon.loanservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.loanservice.dto.loan.request.GuarantorRequestDto;
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
import com.andersenlab.etalon.loanservice.service.LoanOrderService;
import com.andersenlab.etalon.loanservice.service.ValidationService;
import com.andersenlab.etalon.loanservice.util.LoanOrderUtils;
import com.andersenlab.etalon.loanservice.util.enums.OrderStatus;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@EnableScheduling
public class LoanOrderServiceImpl implements LoanOrderService {

  private final LoanOrderRepository loanOrderRepository;
  private final LoanProductRepository loanProductRepository;
  private final GuarantorRepository guarantorRepository;
  private final LoanOrderMapper loanOrderMapper;
  private final GuarantorMapper guarantorMapper;
  private final ValidationService validationService;

  @Override
  public List<LoanOrderResponseDto> getAllLoanOrdersByUserId(String userId) {
    return loanOrderMapper.listOfLoanOrdersToListOfDtos(
        loanOrderRepository.findAllByUserId(userId));
  }

  @Override
  public LoanOrderDetailedResponseDto getLoanOrderDetailed(Long orderId, String userId) {

    LoanOrderEntity loanOrder =
        loanOrderRepository
            .findByIdAndUserId(orderId, userId)
            .orElseThrow(
                () ->
                    new BusinessException(
                        HttpStatus.NOT_FOUND,
                        String.format(BusinessException.LOAN_ORDER_NOT_FOUND_BY_ID, orderId)));

    return loanOrderMapper.toDetailsDto(loanOrder);
  }

  @Override
  @Transactional
  public MessageResponseDto createNewLoanOrder(String userId, LoanOrderRequestDto dto) {
    validationService.validateLoanOrderRequestDto(dto);

    LoanOrderEntity loanOrderEntity = createLoanOrderEntity(userId, dto);
    loanOrderRepository.save(loanOrderEntity);

    return new MessageResponseDto(MessageResponseDto.SEND_LOAN_ORDER_TO_REVIEW);
  }

  public LoanOrderEntity createLoanOrderEntity(String userId, LoanOrderRequestDto dto) {
    LoanProductEntity loanProduct =
        loanProductRepository
            .findById(dto.productId())
            .orElseThrow(
                () ->
                    new BusinessException(
                        HttpStatus.NOT_FOUND,
                        String.format(
                            BusinessException.LOAN_PRODUCT_NOT_FOUND_BY_ID, dto.productId())));

    Set<GuarantorEntity> guarantors =
        loanProduct.getRequiredGuarantors() == null
            ? Collections.emptySet()
            : getExistingAndNewGuarantors(dto.guarantors());

    return LoanOrderEntity.builder()
        .userId(userId)
        .borrower(dto.borrower())
        .amount(dto.amount())
        .averageMonthlySalary(dto.averageMonthlySalary())
        .averageMonthlyExpenses(dto.averageMonthlyExpenses())
        .status(OrderStatus.IN_REVIEW)
        .product(loanProduct)
        .guarantors(guarantors)
        .build();
  }

  public Set<GuarantorEntity> getExistingAndNewGuarantors(Set<GuarantorRequestDto> guarantorDtos) {
    Set<GuarantorEntity> existingGuarantors =
        guarantorRepository.findAllByPeselIn(
            LoanOrderUtils.collectPeselsFromGuarantors(guarantorDtos));

    Set<GuarantorEntity> newGuarantors = guarantorMapper.setOfDtosToEntities(guarantorDtos);
    newGuarantors.removeIf(
        guarantor ->
            existingGuarantors.stream()
                .anyMatch(
                    existingGuarantor ->
                        existingGuarantor.getPesel().equals(guarantor.getPesel())));

    existingGuarantors.addAll(newGuarantors);
    return existingGuarantors;
  }
}

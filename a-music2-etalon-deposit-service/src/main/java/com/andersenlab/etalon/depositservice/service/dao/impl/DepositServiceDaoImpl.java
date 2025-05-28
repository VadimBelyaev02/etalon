package com.andersenlab.etalon.depositservice.service.dao.impl;

import com.andersenlab.etalon.depositservice.config.properties.PaginationProperties;
import com.andersenlab.etalon.depositservice.entity.DepositEntity;
import com.andersenlab.etalon.depositservice.exception.BusinessException;
import com.andersenlab.etalon.depositservice.repository.DepositRepository;
import com.andersenlab.etalon.depositservice.repository.specifications.DepositSpecification;
import com.andersenlab.etalon.depositservice.service.dao.DepositServiceDao;
import com.andersenlab.etalon.depositservice.util.DepositUtils;
import com.andersenlab.etalon.depositservice.util.enums.DepositStatus;
import com.andersenlab.etalon.depositservice.util.filter.CustomPageRequest;
import com.andersenlab.etalon.depositservice.util.filter.DepositFilterRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DepositServiceDaoImpl implements DepositServiceDao {
  private final DepositRepository depositRepository;
  private final PaginationProperties paginationProperties;

  @Override
  public DepositEntity save(DepositEntity depositEntity) {
    return depositRepository.save(depositEntity);
  }

  @Override
  public List<DepositEntity> saveAll(List<DepositEntity> deposits) {
    return depositRepository.saveAll(deposits);
  }

  @Override
  public List<DepositEntity> findAllByExample(Example<DepositEntity> depositExample) {
    return depositRepository.findAll();
  }

  @Override
  public List<DepositEntity> findAllByStatusNot(DepositStatus status) {
    return depositRepository.findAllByStatusNot(status);
  }

  @Override
  public Page<DepositEntity> findAllByUserId(Pageable pageable, String userId) {
    return depositRepository.findAllByUserId(pageable, userId);
  }

  @Override
  public DepositEntity findByIdAndUserId(Long id, String userId) {
    return depositRepository
        .findByIdAndUserId(id, userId)
        .orElseThrow(
            () ->
                new BusinessException(
                    HttpStatus.NOT_FOUND,
                    String.format(BusinessException.DEPOSIT_NOT_FOUND_BY_ID, id)));
  }

  @Override
  public Page<DepositEntity> findFilteredDeposits(
      String userId, CustomPageRequest pageRequest, DepositFilterRequest filter) {
    Specification<DepositEntity> spec =
        Specification.where(DepositSpecification.hasUserId(userId))
            .and(DepositSpecification.hasAccountNumber(filter.accountNumber()))
            .and(DepositSpecification.hasStatus(filter.statusList()));
    Pageable pageable = DepositUtils.getPageRequestFromRequest(pageRequest, paginationProperties);

    return depositRepository.findAll(spec, pageable);
  }
}

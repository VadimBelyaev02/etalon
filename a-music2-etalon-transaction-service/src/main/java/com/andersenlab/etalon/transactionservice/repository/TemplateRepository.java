package com.andersenlab.etalon.transactionservice.repository;

import com.andersenlab.etalon.transactionservice.entity.TemplateEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateRepository extends JpaRepository<TemplateEntity, Long> {

  TemplateEntity findByTemplateNameAndUserId(String templateName, String userId);

  List<TemplateEntity> getTemplateEntitiesByUserIdOrderByCreateAtDesc(String userId);
}

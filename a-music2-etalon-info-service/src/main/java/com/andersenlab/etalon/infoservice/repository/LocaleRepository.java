package com.andersenlab.etalon.infoservice.repository;

import com.andersenlab.etalon.infoservice.entity.LocaleEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LocaleRepository extends JpaRepository<LocaleEntity, Long> {

  @Query("""
        SELECT l.code
        FROM LocaleEntity l
        """)
  List<String> findAllCodes();
}

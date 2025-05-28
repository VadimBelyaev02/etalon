package com.andersenlab.etalon.infoservice.repository;

import com.andersenlab.etalon.infoservice.entity.AtmEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AtmRepository extends JpaRepository<AtmEntity, Long> {

  List<AtmEntity> findAllByCity(final String city);
}

package com.andersenlab.etalon.infoservice.repository;

import com.andersenlab.etalon.infoservice.entity.CountryCodesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryCodesRepository extends JpaRepository<CountryCodesEntity, Long> {}

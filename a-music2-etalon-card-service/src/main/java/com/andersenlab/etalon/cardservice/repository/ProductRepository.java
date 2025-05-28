package com.andersenlab.etalon.cardservice.repository;

import com.andersenlab.etalon.cardservice.entity.CardProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<CardProductEntity, Long> {}

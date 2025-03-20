package com.nichi.nikkie.repository;


import com.nichi.nikkie.entity.NikkeiStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NikkeiStockRepository extends JpaRepository<NikkeiStock, Long> {
    List<NikkeiStock> findAll();
}

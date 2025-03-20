package com.nichi.nikkie.repository;

import com.nichi.nikkie.entity.Nikkei225PAFPrice	;
import com.nichi.nikkie.entity.Nikkei225PAFPriceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Nikkei225PAFPriceRepository extends JpaRepository<Nikkei225PAFPrice, Nikkei225PAFPriceId> {

}

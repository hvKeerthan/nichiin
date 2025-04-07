package com.nichi.nikkie.repository;

import com.nichi.nikkie.entity.Nikkei225PAFPrice	;
import com.nichi.nikkie.entity.Nikkei225PAFPriceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface Nikkei225PAFPriceRepository extends JpaRepository<Nikkei225PAFPrice, Nikkei225PAFPriceId> {
    @Query("SELECT n FROM Nikkei225PAFPrice n WHERE n.id.code = :code")
    Nikkei225PAFPrice findByCode(@Param("code") String code);

    @Modifying
    @Transactional
    @Query("DELETE FROM Nikkei225PAFPrice n WHERE n.id.code = :code")
    void deleteByCode(@Param("code") String code);

    @Query("SELECT n FROM Nikkei225PAFPrice n ORDER BY n.id.code ASC")
    List<Nikkei225PAFPrice> findAllSortedByCode();
}

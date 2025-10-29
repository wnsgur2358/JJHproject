package com.multi.matchon.stadium.repository;

import com.multi.matchon.stadium.domain.Stadium;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StadiumRepository extends JpaRepository<Stadium, Long> {

    // 이름 키워드 검색
    Page<Stadium> findByStadiumNameContainingIgnoreCase(String keyword, Pageable pageable);

    // 지역 필터링
    Page<Stadium> findByStadiumRegionContaining(String region, Pageable pageable);

    // 전체 조회
    Page<Stadium> findAll(Pageable pageable);

    // 주소 키워드 검색
    List<Stadium> findByStadiumAddressContainingIgnoreCase(String keyword);

}

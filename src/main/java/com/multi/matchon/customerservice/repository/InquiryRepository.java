package com.multi.matchon.customerservice.repository;

import com.multi.matchon.customerservice.domain.CustomerServiceType;
import com.multi.matchon.customerservice.domain.Inquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    @Query("SELECT i FROM Inquiry i WHERE i.isDeleted = false AND i.member.id = :memberId " +
            "AND (:keyword IS NULL OR i.inquiryTitle LIKE %:keyword%) " +
            "AND (:category IS NULL OR i.inquiryCategory = :category)")
    Page<Inquiry> searchByMemberAndFilter(@Param("memberId") Long memberId,
                                          @Param("keyword") String keyword,
                                          @Param("category") CustomerServiceType category,
                                          Pageable pageable);

    // 관리자 전체 목록 (삭제된 건 제외)
    List<Inquiry> findAllByIsDeletedFalse(Sort sort);

    // 관리자 단일 조회 (삭제되지 않은 것만)
    Optional<Inquiry> findByIdAndIsDeletedFalse(Long id);

    Page<Inquiry> findAllByIsDeletedFalse(Pageable pageable);
}


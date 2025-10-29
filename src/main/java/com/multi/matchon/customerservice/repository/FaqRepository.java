package com.multi.matchon.customerservice.repository;

import com.multi.matchon.customerservice.domain.CustomerServiceType;
import com.multi.matchon.customerservice.domain.Faq;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FaqRepository extends JpaRepository<Faq, Long> {

    // 기본 전체 조회 (삭제되지 않은 것만)
    Page<Faq> findByIsDeletedFalse(Pageable pageable);

    // 카테고리 필터
    Page<Faq> findByFaqCategoryAndIsDeletedFalse(CustomerServiceType faqCategory, Pageable pageable);

    // 제목 키워드 검색
    Page<Faq> findByFaqTitleContainingIgnoreCaseAndIsDeletedFalse(String keyword, Pageable pageable);

    // 카테고리 + 제목 키워드 검색
    Page<Faq> findByFaqCategoryAndFaqTitleContainingIgnoreCaseAndIsDeletedFalse(
            CustomerServiceType faqCategory,
            String keyword,
            Pageable pageable
    );

    // FAQ 중복 체크 (제목 + 내용 + 카테고리 + 삭제되지 않은 조건)
    boolean existsByFaqCategoryAndFaqTitleAndFaqContentAndIsDeletedFalse(
            CustomerServiceType faqCategory,
            String faqTitle,
            String faqContent
    );
}

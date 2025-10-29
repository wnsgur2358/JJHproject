package com.multi.matchon.customerservice.service;

import com.multi.matchon.customerservice.domain.CustomerServiceType;
import com.multi.matchon.customerservice.domain.Faq;
import com.multi.matchon.customerservice.dto.res.FaqDto;
import com.multi.matchon.customerservice.repository.FaqRepository;
import com.multi.matchon.member.domain.Member;
import com.multi.matchon.member.domain.MemberRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FaqService {

    private final FaqRepository faqRepository;


    public Long savePost(FaqDto faqDto) {
        Member member = faqDto.getMember();

        // 보안 체크: 관리자만 등록 가능
        if (member == null || member.getMemberRole() != MemberRole.ADMIN) {
            throw new AccessDeniedException("FAQ 등록은 관리자만 가능합니다.");
        }


        boolean isDuplicate = faqRepository.existsByFaqCategoryAndFaqTitleAndFaqContentAndIsDeletedFalse(
                faqDto.getFaqCategory(), faqDto.getFaqTitle(), faqDto.getFaqContent());

        if (isDuplicate) {
            throw new IllegalArgumentException("같은 FAQ가 이미 존재합니다.");
        }

        Faq faq = faqDto.toEntity();
        return faqRepository.save(faq).getId();
    }


    public FaqDto getFaqById(Long id) {
        Faq faq = faqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FAQ를 찾을 수 없습니다."));
        return convertEntityToDto(faq);
    }


    public Page<FaqDto> searchByCategoryAndTitlePaged(CustomerServiceType category, String keyword, Pageable pageable) {
        return faqRepository.findByFaqCategoryAndFaqTitleContainingIgnoreCaseAndIsDeletedFalse(category, keyword, pageable)
                .map(this::convertEntityToDto);
    }


    public Page<FaqDto> searchByTitlePaged(String keyword, Pageable pageable) {
        return faqRepository.findByFaqTitleContainingIgnoreCaseAndIsDeletedFalse(keyword, pageable)
                .map(this::convertEntityToDto);
    }


    public Page<FaqDto> getFaqListPaged(CustomerServiceType category, Pageable pageable) {
        Page<Faq> page = (category != null)
                ? faqRepository.findByFaqCategoryAndIsDeletedFalse(category, pageable)
                : faqRepository.findByIsDeletedFalse(pageable);


        List<FaqDto> distinctList = page.getContent().stream()
                .map(this::convertEntityToDto)
                .distinct() // equals/hashCode 기반
                .collect(Collectors.toList());

        return new PageImpl<>(distinctList, pageable, page.getTotalElements());
    }


    private FaqDto convertEntityToDto(Faq faq) {
        return FaqDto.builder()
                .faqId(faq.getId())
                .member(faq.getMember())
                .faqCategory(faq.getFaqCategory())
                .faqTitle(faq.getFaqTitle())
                .faqContent(faq.getFaqContent())
                .isDeleted(faq.getIsDeleted())
                .createdDate(faq.getCreatedDate())
                .modifiedDate(faq.getModifiedDate())
                .build();
    }

    @Transactional
    public void updateFaq(Long id, String title, String content, CustomerServiceType category) {
        Faq faq = faqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FAQ를 찾을 수 없습니다."));
        faq.update(title, content, category);
    }

    @Transactional
    public void deleteFaqById(Long id) {
        Faq faq = faqRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("FAQ를 찾을 수 없습니다."));
        faq.softDelete(); // 소프트 삭제
    }

}

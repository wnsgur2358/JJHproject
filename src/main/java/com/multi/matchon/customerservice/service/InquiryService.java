package com.multi.matchon.customerservice.service;

import com.multi.matchon.common.domain.Status;
import com.multi.matchon.customerservice.domain.CustomerServiceType;
import com.multi.matchon.customerservice.domain.Inquiry;
import com.multi.matchon.customerservice.dto.req.InquiryReqDto;
import com.multi.matchon.customerservice.dto.res.InquiryResDto;
import com.multi.matchon.customerservice.repository.InquiryRepository;
import com.multi.matchon.member.domain.Member;
import com.multi.matchon.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final MemberRepository memberRepository;

    public void saveInquiry(Long memberId, InquiryReqDto dto) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        Inquiry inquiry = dto.toEntity(member);
        inquiryRepository.save(inquiry);
    }

    @Transactional
    public void updateInquiry(Long id, InquiryReqDto dto) {
        Inquiry inquiry = inquiryRepository.findById(id).orElseThrow();
        if (inquiry.getInquiryStatus() != Status.PENDING) {
            throw new IllegalStateException("수정 불가");
        }
        inquiry.update(dto.getInquiryTitle(), dto.getInquiryContent(), dto.getInquiryCategory());
    }

    @Transactional
    public void deleteCompletedInquiry(Long id) {
        Inquiry inquiry = inquiryRepository.findById(id).orElseThrow();
        if (inquiry.getInquiryStatus() == Status.COMPLETED) {
            inquiry.markDeleted();
        } else {
            throw new IllegalStateException("답변 완료된 문의만 삭제할 수 있습니다.");
        }
    }

    public Page<InquiryResDto> findMyInquiries(Long memberId, Optional<String> keyword, Optional<CustomerServiceType> category, Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "createdDate"));
        return inquiryRepository.searchByMemberAndFilter(memberId, keyword.orElse(null), category.orElse(null), sortedPageable)
                .map(InquiryResDto::new);
    }

    public InquiryReqDto findEditableInquiry(Long id) {
        Inquiry inquiry = inquiryRepository.findById(id).orElseThrow();
        if (inquiry.getInquiryStatus() != Status.PENDING) {
            throw new IllegalStateException("수정 불가");
        }
        InquiryReqDto dto = new InquiryReqDto(inquiry.getId(), inquiry.getInquiryTitle(), inquiry.getInquiryContent(), inquiry.getInquiryCategory());
        return dto;
    }

    public InquiryResDto findByIdAndOwner(Long id, Long memberId) {
        Inquiry inquiry = inquiryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NoSuchElementException("문의 내역이 없습니다."));
        if (!inquiry.getMember().getId().equals(memberId)) {
            throw new RuntimeException("해당 문의에 접근할 수 없습니다.");
        }
        return new InquiryResDto(inquiry);
    }
}

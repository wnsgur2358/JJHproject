package com.multi.matchon.customerservice.dto.res;

import com.multi.matchon.customerservice.domain.CustomerServiceType;
import com.multi.matchon.customerservice.domain.Faq;
import com.multi.matchon.member.domain.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
public class FaqDto {

    private Long faqId; // ← id → faqId로 변경
    private Member member;
    private CustomerServiceType faqCategory;
    private String faqTitle;
    private String faqContent;
    private Boolean isDeleted;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    @Builder
    public FaqDto(Long faqId,
                  Member member,
                  CustomerServiceType faqCategory,
                  String faqTitle,
                  String faqContent,
                  Boolean isDeleted,
                  LocalDateTime createdDate,
                  LocalDateTime modifiedDate) {
        this.faqId = faqId;
        this.member = member;
        this.faqCategory = faqCategory;
        this.faqTitle = faqTitle;
        this.faqContent = faqContent;
        this.isDeleted = isDeleted;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public FaqDto withMember(Member member) {
        return FaqDto.builder()
                .faqId(this.faqId)
                .member(member)
                .faqCategory(this.faqCategory)
                .faqTitle(this.faqTitle)
                .faqContent(this.faqContent)
                .isDeleted(this.isDeleted)
                .createdDate(this.createdDate)
                .modifiedDate(this.modifiedDate)
                .build();
    }

    public Faq toEntity() {
        return Faq.builder()
                .id(faqId)
                .member(member)
                .faqCategory(faqCategory)
                .faqTitle(faqTitle)
                .faqContent(faqContent)
                .isDeleted(isDeleted != null ? isDeleted : false)
                .build();
    }

    // 중복된 데이터 제거(FaqService.java)할려면 이거 필요함...
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FaqDto)) return false;
        FaqDto that = (FaqDto) o;
        return faqCategory == that.faqCategory &&
                ((faqTitle == null && that.faqTitle == null) || (faqTitle != null && faqTitle.equals(that.faqTitle))) &&
                ((faqContent == null && that.faqContent == null) || (faqContent != null && faqContent.equals(that.faqContent)));
    }

    @Override
    public int hashCode() {
        int result = faqCategory != null ? faqCategory.hashCode() : 0;
        result = 31 * result + (faqTitle != null ? faqTitle.hashCode() : 0);
        result = 31 * result + (faqContent != null ? faqContent.hashCode() : 0);
        return result;
    }


}

package com.multi.matchon.customerservice.dto.req;

import com.multi.matchon.common.domain.Status;
import com.multi.matchon.customerservice.domain.CustomerServiceType;
import com.multi.matchon.customerservice.domain.Inquiry;
import com.multi.matchon.member.domain.Member;

public class InquiryReqDto {

    private Long id;
    private String inquiryTitle;
    private String inquiryContent;
    private CustomerServiceType inquiryCategory;

    public InquiryReqDto() {}

    public InquiryReqDto(Long id, String inquiryTitle, String inquiryContent, CustomerServiceType inquiryCategory) {
        this.id = id;
        this.inquiryTitle = inquiryTitle;
        this.inquiryContent = inquiryContent;
        this.inquiryCategory = inquiryCategory;
    }

    // Getter
    public Long getId() { return id; }
    public String getInquiryTitle() { return inquiryTitle; }
    public String getInquiryContent() { return inquiryContent; }
    public CustomerServiceType getInquiryCategory() { return inquiryCategory; }

    // Setter
    public void setId(Long id) { this.id = id; }
    public void setInquiryTitle(String inquiryTitle) { this.inquiryTitle = inquiryTitle; }
    public void setInquiryContent(String inquiryContent) { this.inquiryContent = inquiryContent; }
    public void setInquiryCategory(CustomerServiceType inquiryCategory) { this.inquiryCategory = inquiryCategory; }

    public Inquiry toEntity(Member member) {
        return Inquiry.builder()
                .member(member)
                .inquiryTitle(inquiryTitle)
                .inquiryContent(inquiryContent)
                .inquiryCategory(inquiryCategory)
                .inquiryStatus(Status.PENDING)
                .build();
    }
}

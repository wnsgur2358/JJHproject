package com.multi.matchon.customerservice.dto.res;

import com.multi.matchon.common.domain.Status;
import com.multi.matchon.customerservice.domain.Inquiry;

import java.time.LocalDateTime;

public class InquiryResDto {

    private Long id;
    private String inquiryTitle;
    private String inquiryContent;
    private String categoryLabel;
    private String statusLabel;
    private String memberName;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private String answerContent;
    private Status inquiryStatus;

    public InquiryResDto(Inquiry inquiry) {
        this.id = inquiry.getId();
        this.inquiryTitle = inquiry.getInquiryTitle();
        this.inquiryContent = inquiry.getInquiryContent();
        this.categoryLabel = inquiry.getInquiryCategory().getLabel();
        this.inquiryStatus = inquiry.getInquiryStatus();
        this.createdDate = inquiry.getCreatedDate();
        this.modifiedDate = inquiry.getModifiedDate();
        this.answerContent = (inquiry.getAnswer() != null) ? inquiry.getAnswer().getAnswerContent() : null;
        this.memberName = inquiry.getMember().getMemberName();
    }

    public Long getId() { return id; }
    public String getInquiryTitle() {return inquiryTitle; }
    public String getInquiryContent() { return inquiryContent; }
    public String getCategoryLabel() { return categoryLabel; }
    public LocalDateTime getCreatedDate() { return createdDate; }
    public LocalDateTime getModifiedDate() { return modifiedDate; }
    public String getAnswerContent() { return answerContent; }
    public Status getInquiryStatus() { return inquiryStatus; }
    public String getMemberName() { return memberName; }

    public String getStatusLabel() {
        return switch (inquiryStatus) {
            case PENDING -> "대기중";
            case COMPLETED -> "답변완료";
            default -> "알 수 없음";
        };
    }
}

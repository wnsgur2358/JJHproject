package com.multi.matchon.customerservice.domain;

import com.multi.matchon.common.domain.BaseEntity;
import com.multi.matchon.common.domain.Status;
import com.multi.matchon.member.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name="inquiry")

public class Inquiry extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "inquiry_category", nullable = false)
    private CustomerServiceType inquiryCategory;

    @Column(name = "inquiry_title", nullable = false, length = 100)
    private String inquiryTitle;

    @Column(name = "inquiry_content", nullable = false, columnDefinition = "TEXT")
    private String inquiryContent;

    @Enumerated(EnumType.STRING)
    @Column(name = "inquiry_status", nullable = false)
    @Builder.Default
    private Status inquiryStatus = Status.PENDING;

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    @OneToOne(mappedBy = "inquiry", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private InquiryAnswer answer;

    public void update(String title, String content, CustomerServiceType category) {
        this.inquiryTitle = title;
        this.inquiryContent = content;
        this.inquiryCategory = category;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public void setStatus(Status status) {
        this.inquiryStatus = status;
    }

    public void markDeleted() {
        this.isDeleted = true;
    }

    public void setAnswer(InquiryAnswer answer) {
        this.answer = answer;
    }

    public void complete() {
        this.inquiryStatus = Status.COMPLETED;
    }

    public String getCategoryLabel() {
        return this.inquiryCategory.getLabel();
    }

    public String getStatusLabel() {
        return switch (this.inquiryStatus) {
            case PENDING -> "대기중";
            case COMPLETED -> "답변완료";
            default -> "알 수 없음";
        };
    }
}



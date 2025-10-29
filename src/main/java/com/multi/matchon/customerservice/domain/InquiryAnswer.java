package com.multi.matchon.customerservice.domain;

import com.multi.matchon.common.domain.BaseEntity;
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
@Table(name="inquiry_answer")

public class InquiryAnswer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="answer_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "inquiry_id")
    private Inquiry inquiry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="admin_id",nullable = false)
    private Member member;

    @Column(name="answer_content", nullable = false, columnDefinition = "TEXT")
    private String answerContent;

    @Column(name="is_deleted")
    @Builder.Default
    private Boolean isDeleted=false;

    public void markDeleted() {
        this.isDeleted = true;
    }
}

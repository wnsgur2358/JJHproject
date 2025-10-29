package com.multi.matchon.customerservice.domain;

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
@Table(name="faq")
//@Setter: JPA entity에서 setter사용은 자제, test용
public class Faq extends TimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="faq_id")
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="admin_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name="faq_category", nullable = false)
    private CustomerServiceType faqCategory;

    @Column(name="faq_title", nullable = false, length = 100)
    private String faqTitle;

    @Column(name="faq_content",nullable = false, columnDefinition = "TEXT")
    private String faqContent;

    @Column(name="is_deleted")
    @Builder.Default
    private Boolean isDeleted=false;

    public void update(String title, String content, CustomerServiceType category) {
        this.faqTitle = title;
        this.faqContent = content;
        this.faqCategory = category;
    }

    public void softDelete() {
        this.isDeleted = true;
    }
}

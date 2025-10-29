package com.multi.matchon.team.domain;

import com.multi.matchon.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name="response")
//@Setter: JPA entity에서 setter사용은 자제, test용
public class Response extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="resonse_id")
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="review_id", nullable = false)
    private Review review;

    @Column(name="review_response",nullable = false, columnDefinition = "TEXT")
    private String reviewResponse;

    @Column(name="is_deleted")
    @Builder.Default
    private Boolean isDeleted=false;


    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    public void updateContent(String newContent) {
        this.reviewResponse = newContent;
        this.respondedAt = LocalDateTime.now();
    }
}

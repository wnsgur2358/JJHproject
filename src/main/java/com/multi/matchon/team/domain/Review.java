package com.multi.matchon.team.domain;

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
@Table(name="review")
//@Setter: JPA entity에서 setter사용은 자제, test용
public class Review extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="team_review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="review_writer", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(name="review_rating", nullable = false)
    private Integer reviewRating;

    @Column(name="content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name="is_deleted")
    @Builder.Default
    private Boolean isDeleted=false;

    public void updateReview(int rating, String content) {
        this.reviewRating = rating;
        this.content = content;
    }

    public void softDelete() {
        this.isDeleted = true;
    }
}

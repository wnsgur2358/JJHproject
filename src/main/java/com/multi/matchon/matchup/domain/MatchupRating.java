package com.multi.matchon.matchup.domain;

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
@Table(name="matchup_rating")
//@Setter: JPA entity에서 setter사용은 자제, test용
public class MatchupRating extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="matchup_rating_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="matchup_board_id",nullable = false)
    private MatchupBoard matchupBoard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="evaluator_id",nullable = false)
    private Member memberEval;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="target_member_id",nullable = false)
    private Member memberTarget;

    @Column(name="manner_score",columnDefinition = "INT CHECK (1<=manner_score AND manner_score<=5)")
    private Integer mannerScore;

    @Column(name="skill_score", columnDefinition = "INT CHECK (1<=skill_score AND skill_score<=5)")
    private Integer skillScore;

    @Column(name="review",columnDefinition = "TEXT")
    private String review;

    @Column(name="is_completed")
    @Builder.Default
    private Boolean isCompleted = false;

    public void rating(Integer mannerScore, Integer skillScore, String review, Boolean isCompleted){
        this.mannerScore = mannerScore;
        this.skillScore = skillScore;
        this.review = review;
        this.isCompleted = isCompleted;
    }

}

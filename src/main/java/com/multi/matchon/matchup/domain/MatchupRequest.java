package com.multi.matchon.matchup.domain;

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
@Table(name="matchup_request")
//@Setter: JPA entity에서 setter사용은 자제, test용
public class MatchupRequest extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="matchup_request_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="matchup_board_id", nullable = false)
    private MatchupBoard matchupBoard;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="applicant_id",nullable = false)
    private Member member;

    @Column(name="self_intro",nullable = false, columnDefinition = "TEXT")
    private String selfIntro;

    @Column(name="participant_count",nullable = false)
    private Integer participantCount;

    @Column(name="status",nullable = false)
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Status matchupStatus = Status.PENDING;

    @Column(name="request_submitted_count")
    @Builder.Default
    private Integer matchupRequestSubmittedCount = 1;

    @Column(name="cancel_submitted_count")
    @Builder.Default
    private Integer matchupCancelSubmittedCount = 0;

    @Column(name="is_deleted")
    @Builder.Default
    private Boolean isDeleted=false;

    public void update(String selfIntro, Integer participantCount) {
        this.selfIntro = selfIntro;
        this.participantCount = participantCount;
    }

    public void updateRequestMangementInfo(Status matchupStatus, Integer matchupRequestSubmittedCount, Integer matchupCancelSubmittedCount, Boolean isDeleted) {
        this.matchupStatus = matchupStatus;
        this.matchupRequestSubmittedCount = matchupRequestSubmittedCount;
        this.matchupCancelSubmittedCount = matchupCancelSubmittedCount;
        this.isDeleted = isDeleted;
    }
}

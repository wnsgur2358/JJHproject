package com.multi.matchon.team.domain;


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
@Table(name="team_join_request")
//@Setter: JPA entity에서 setter사용은 자제, test용
public class TeamJoinRequest extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="join_request_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="applicant_id",nullable = false)
    private Member member;



    @Column(name="join_request_status",nullable = false)
    @Enumerated(EnumType.STRING)
    private Status joinRequestStatus;

    @Column(name="is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted=false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="team_id", nullable = false)
    private Team team;

    @Column(name="introduction", columnDefinition = "TEXT")
    private String introduction;

    public void approved() {
        if (this.joinRequestStatus != Status.PENDING) {
            throw new IllegalStateException("이미 처리된 요청입니다.");
        }
        this.joinRequestStatus = Status.APPROVED;
    }

    public void denied() {
        if (this.joinRequestStatus != Status.PENDING) {
            throw new IllegalStateException("이미 처리된 요청입니다.");
        }
        this.joinRequestStatus = Status.DENIED;
    }
}


package com.multi.matchon.team.domain;

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
@Table(name="team_member")
//@Setter: JPA entity에서 setter사용은 자제, test용
public class TeamMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="team_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id",nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="team_id", nullable = false)
    private Team team;

    @Column(name="introduction",nullable = false,columnDefinition = "TEXT")
    private String introduction;

    @Column(name="team_leader_status",nullable = false)
    @Builder.Default
    private Boolean teamLeaderStatus=false;

}

package com.multi.matchon.team.repository;

import com.multi.matchon.member.domain.Member;
import com.multi.matchon.team.domain.Team;
import com.multi.matchon.team.domain.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    Optional<TeamMember> findByMember_IdAndTeam_Id(Long memberId, Long teamId);
    boolean existsByTeamAndMemberAndTeamLeaderStatusTrue(Team team, Member member);
    Optional<TeamMember> findByMemberId(Long memberId);
    Optional<TeamMember> findByTeamAndTeamLeaderStatusTrue(Team team);
    Optional<TeamMember> findLeaderByTeam(Team team);
}

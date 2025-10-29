package com.multi.matchon.member.repository;

import com.multi.matchon.member.domain.Member;
import com.multi.matchon.member.domain.MemberRole;
import com.multi.matchon.member.dto.res.ResTeamInfoDto;
import com.multi.matchon.team.domain.Team;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    // 로그인 전용
    Optional<Member> findSimpleByMemberEmail(String email);

    // 마이페이지 전용 (positions, team fetch)
    @EntityGraph(attributePaths = {"positions", "team"})
    Optional<Member> findByMemberEmail(String email);


    // 팀 전용
    @Query("""
             select
                new com.multi.matchon.member.dto.res.ResTeamInfoDto(
                    t2.teamName,
                    t2.teamIntroduction
                )
             from Member t1
             join t1.team t2
             where t1=:loginMember and t1.isDeleted=false
            """)
    Optional<ResTeamInfoDto> findResTeamInfoByMember(@Param("loginMember") Member loginMember);

    @Query("""
            select t1.myTemperature
            from Member t1
            where t1=:loginMember and t1.isDeleted=false
            """)
    Optional<Double> findMyTemperatureByMember(@Param("loginMember") Member loginMember);


    Optional<Member> findByIdAndIsDeletedFalse(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select t1
            from Member t1
            where t1.id=:id and t1.isDeleted=false
            """)
    Optional<Member> findByIdAndIsDeletedFalseWithLock(@Param("id") Long id);

    Optional<Member> findByMemberEmailAndIsDeletedFalse(String senderEmail);

    List<Member> findAllByTeam(Team team);

    List<Member> findByMemberRoleAndIsDeletedFalse(MemberRole role);

    List<Member> findAllByTeamIdAndIsDeletedFalse(Long teamId);
}

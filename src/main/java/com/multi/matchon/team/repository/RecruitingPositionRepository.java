package com.multi.matchon.team.repository;

import com.multi.matchon.team.domain.RecruitingPosition;
import com.multi.matchon.team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecruitingPositionRepository extends JpaRepository<RecruitingPosition, Long> {
    void deleteByTeam(Team team);
}

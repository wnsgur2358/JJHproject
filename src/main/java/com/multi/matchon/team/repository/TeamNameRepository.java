package com.multi.matchon.team.repository;

import com.multi.matchon.common.domain.PositionName;
import com.multi.matchon.team.domain.RegionType;
import com.multi.matchon.team.domain.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;


import java.util.List;
import java.util.Optional;

public interface TeamNameRepository extends JpaRepository <Team, Long> {
    List<Team> findAll();

    @Query("""
    SELECT DISTINCT t FROM Team t
    LEFT JOIN t.recruitingPositions rp
    LEFT JOIN rp.positions p
    WHERE t.isDeleted = false
    AND (:positionName IS NULL OR p.positionName = :positionName)
    AND (:region IS NULL OR t.teamRegion = :region)
    AND (:rating IS NULL OR t.teamRatingAverage >= :rating)
    AND (:recruitmentStatus IS NULL OR t.recruitmentStatus = :recruitmentStatus)
    
""")
    Page<Team> findWithRatingFilter(
            @Param("positionName") PositionName positionName,
            @Param("region") RegionType region,
            @Param("rating") Double rating,
            @Param("recruitmentStatus") Boolean recruitmentStatus,
            Pageable pageable);


    @Query("""
    SELECT DISTINCT t FROM Team t
    LEFT JOIN t.recruitingPositions rp
    LEFT JOIN rp.positions p
    WHERE t.isDeleted = false AND (
        (:positionName IS NULL OR p.positionName = :positionName)
        AND (:region IS NULL OR t.teamRegion = :region)
        AND (:recruitmentStatus IS NULL OR t.recruitmentStatus = :recruitmentStatus)
    )
""")
    Page<Team> findWithoutRatingFilter(
            @Param("positionName") PositionName positionName,
            @Param("region") RegionType region,
            @Param("recruitmentStatus") Boolean recruitmentStatus,
            Pageable pageable
    );

    boolean existsByCreatedPersonAndIsDeletedFalse(String createdPerson);

    @Query("SELECT t FROM Team t WHERE t.id = :id AND t.isDeleted = false")
    Optional<Team> findByIdNotDeleted(@Param("id") Long id);

    @Query("SELECT t FROM Team t WHERE t.isDeleted = false")
    List<Team> findAllNotDeleted();

    boolean existsByTeamNameAndIsDeletedFalse(String teamName);
}


//    research needed!!
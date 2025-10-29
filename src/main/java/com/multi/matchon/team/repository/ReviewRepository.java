package com.multi.matchon.team.repository;

import com.multi.matchon.team.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("""
            SELECT r FROM Review r 
            JOIN FETCH r.member m 
            WHERE r.team.id = :teamId
            AND r.isDeleted = false 
            ORDER BY r.createdDate DESC
            """)
    List<Review> findReviewsByTeamId(@Param("teamId") Long teamId);

    @Query("""
        SELECT r FROM Review r
        JOIN FETCH r.member m
        WHERE r.team.id = :teamId
        AND r.isDeleted = false
        ORDER BY r.createdDate DESC
        """)
    org.springframework.data.domain.Page<Review> findByTeamId(@Param("teamId") Long teamId, org.springframework.data.domain.Pageable pageable);
}
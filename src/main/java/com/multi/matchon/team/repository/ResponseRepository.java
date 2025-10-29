package com.multi.matchon.team.repository;

import com.multi.matchon.team.domain.Response;
import com.multi.matchon.team.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResponseRepository extends JpaRepository<Response, Long> {
    Optional<Response> findByReviewAndIsDeletedFalse(Review review);
    boolean existsByReviewAndIsDeletedFalse(Review review);
}
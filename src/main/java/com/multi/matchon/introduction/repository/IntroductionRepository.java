package com.multi.matchon.introduction.repository;

import com.multi.matchon.introduction.domain.Introduction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntroductionRepository extends JpaRepository<Introduction, Long> {
}

package com.multi.matchon.common.repository;

import com.multi.matchon.common.domain.SportsType;
import com.multi.matchon.common.domain.SportsTypeName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SportsTypeRepository extends JpaRepository<SportsType, Long> {
    Optional<SportsType> findBySportsTypeName(SportsTypeName sportsTypeName);
}

package com.multi.matchon.common.jwt.repository;

import com.multi.matchon.common.jwt.domain.RefreshToken;
import com.multi.matchon.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByMember(Member member);
    void deleteByMember(Member member);
}

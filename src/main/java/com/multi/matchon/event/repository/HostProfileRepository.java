package com.multi.matchon.event.repository;

import com.multi.matchon.event.domain.HostProfile;
import com.multi.matchon.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HostProfileRepository extends JpaRepository<HostProfile, Long> {
    Optional<HostProfile> findByMember(Member member);

    Optional<HostProfile> findByHostName(String hostName);
}

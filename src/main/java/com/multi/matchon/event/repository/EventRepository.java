package com.multi.matchon.event.repository;


import com.multi.matchon.common.domain.Status;
import com.multi.matchon.event.domain.EventRegionType;
import com.multi.matchon.event.domain.EventRequest;
import com.multi.matchon.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<EventRequest, Long> {
    // 기본 캘린더 조회
    List<EventRequest> findByEventDateBetweenAndIsDeletedFalse(LocalDate start, LocalDate end);

    // 지역 필터 포함 캘린더 조회
    List<EventRequest> findByEventDateBetweenAndEventRegionTypeAndIsDeletedFalse(LocalDate start, LocalDate end, EventRegionType region);

    // 마이페이지, 삭제 필터 포함
    List<EventRequest> findByMemberAndIsDeletedFalse(Member member);

    Page<EventRequest> findByMemberAndIsDeletedFalse(Member member, Pageable pageable);

    // 상태 변경
    @Modifying
    @Transactional
    @Query("UPDATE EventRequest e SET e.eventStatus = :status WHERE e.id = :eventId")
    void updateEventStatus(@Param("eventId") Long eventId, @Param("status") Status status);


}

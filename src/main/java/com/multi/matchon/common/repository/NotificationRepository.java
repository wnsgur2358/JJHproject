package com.multi.matchon.common.repository;


import com.multi.matchon.common.auth.dto.CustomUser;
import com.multi.matchon.common.domain.Notification;
import com.multi.matchon.common.dto.res.ResNotificationDto;
import com.multi.matchon.common.dto.res.ResReadNotificationDto;
import com.multi.matchon.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {


    @Query("""
            select
            new com.multi.matchon.common.dto.res.ResNotificationDto(
                t1.id,
                t1.notificationMessage,
                t1.createdDate,
                null
            )
            from Notification t1
            where t1.receivedMember=:loginMember and t1.isRead=false
            order by t1.createdDate asc
            """)
    List<ResNotificationDto> findAllByMemberAndUnreadFalse(@Param("loginMember") Member loginMember);


    @Query("""
            select
                t1
            from Notification t1
            where t1.id=:notificationId and t1.receivedMember=:loginMember and t1.isRead=false
            
            """)
    Optional<Notification> findTargetUrlByNotificationIdAndMember(@Param("notificationId") Long notificationId, @Param("loginMember") Member loginMember);

    @Query("""
            select
            new com.multi.matchon.common.dto.res.ResReadNotificationDto(
                t1.id,
                t1.notificationMessage,
                t1.createdDate,
                t1.targetUrl
            )
            from Notification t1
            where t1.receivedMember=:loginMember and t1.isRead=true
            order by t1.createdDate asc
            """)
    List<ResReadNotificationDto> findAllByMemberAndUnreadTrue(@Param("loginMember") Member loginMember);
}

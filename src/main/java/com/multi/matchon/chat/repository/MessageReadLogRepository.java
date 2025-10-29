package com.multi.matchon.chat.repository;


import com.multi.matchon.chat.domain.ChatRoom;
import com.multi.matchon.chat.domain.MessageReadLog;
import com.multi.matchon.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageReadLogRepository extends JpaRepository<MessageReadLog, Long> {
    Long countByChatRoomAndMemberAndIsReadFalse(ChatRoom chatRoom, Member member);

    @Modifying
    @Query("""
            update MessageReadLog t1
            set t1.isRead = true
            where t1.chatRoom =:chatRoom and t1.member=:sender and t1.isRead = false
            """)
    int updateMessagesRead(@Param("chatRoom") ChatRoom chatRoom,@Param("sender") Member sender);

    @Query("""
    SELECT COUNT(m)
    FROM MessageReadLog m
    WHERE m.chatRoom.id = :roomId
      AND m.member.id = :userId
      AND m.isRead = false
""")
    Long countUnreadMessages(@Param("roomId") Long roomId, @Param("userId") Long userId);
}

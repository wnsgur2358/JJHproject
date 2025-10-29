package com.multi.matchon.chat.repository;


import com.multi.matchon.chat.domain.ChatMessage;
import com.multi.matchon.chat.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {


    @Query("""
            select t1
            from ChatMessage t1
            join fetch t1.member
            where t1.chatRoom = :chatRoom and t1.createdDate>=:joinedDate and t1.chatRoom.isDeleted=false
            order by t1.createdDate asc
            """)
    List<ChatMessage> findByChatRoomAndJoinedDateOrderByCreatedTimeAscWithMember(@Param("chatRoom") ChatRoom chatRoom, @Param("joinedDate") LocalDateTime joinedDate);
}

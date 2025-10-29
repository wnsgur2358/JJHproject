package com.multi.matchon.chat.repository;


import com.multi.matchon.chat.domain.ChatRoom;
import com.multi.matchon.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByIdAndIsDeletedFalse(Long roomId);

    @Query("""
            select t1.isGroupChat
            from ChatRoom t1
            where t1.id=:roomId and t1.isDeleted=false
            """)
    Boolean isGroupChat(@Param("roomId")Long roomId);

    @Query("""
    SELECT t.chatRoom FROM Team t
    WHERE t.id = :teamId AND t.chatRoom.isGroupChat = true
""")
    Optional<ChatRoom> findTeamGroupChatRoom(@Param("teamId") Long teamId);



    @Query("SELECT DISTINCT p.chatRoom FROM ChatParticipant p " +
            "WHERE p.chatRoom.isGroupChat = false AND p.member.id = :memberId")
    List<ChatRoom> findPrivateChatsByMemberId(@Param("memberId") Long memberId);
}

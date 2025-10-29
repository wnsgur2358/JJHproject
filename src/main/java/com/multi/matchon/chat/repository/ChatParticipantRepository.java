package com.multi.matchon.chat.repository;


import com.multi.matchon.chat.domain.ChatParticipant;
import com.multi.matchon.chat.domain.ChatRoom;
import com.multi.matchon.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {

    @Query("""
            select
                t1.chatRoom
            from ChatParticipant t1
            join ChatParticipant t2
            on t1.chatRoom.id = t2.chatRoom.id
            where t1.isDeleted=false and t2.isDeleted=false and t1.member.id =:receiverId and t2.member.id=:senderId and t1.chatRoom.isGroupChat = false and t1.member.isDeleted = false and t2.member.isDeleted = false and t1.chatRoom.isDeleted =false and t2.chatRoom.isDeleted=false
            """)
    Optional<ChatRoom> findPrivateChatRoomByReceiverIdAndSenderId(Long receiverId, Long senderId);

    @Query("""
            select t1
            from ChatParticipant t1
            join fetch t1.chatRoom t2
            where t1.isDeleted=false and t1.member.id =:memberId and t2.isDeleted=false
            
            """)
    List<ChatParticipant> findAllByMemberIdAndIsDeletedFalse(@Param("memberId") Long memberId);



    @Query("""
            select t1
            from ChatParticipant t1
            join fetch t1.member
            where t1.isDeleted=false and t1.chatRoom =:chatRoom
            
            """)
    List<ChatParticipant> findByChatRoomWithMember(@Param("chatRoom") ChatRoom chatRoom);

    @Query("""
            select t1
            from ChatParticipant t1
            join fetch t1.member
            where t1.isDeleted=false and t1.chatRoom.id =:chatRoomId
            """)
    List<ChatParticipant> findByChatRoomIdWithMember(@Param("chatRoomId") Long chatRoomId);



    @Query("""
            select
                case
                    when count(t1) >0 then true
                    else false
                end
            from ChatParticipant t1
            where t1.isDeleted=false and t1.chatRoom=:chatRoom and t1.member=:sender and t1.member.isDeleted=false
            """)
    Boolean isRoomParticipantByChatRoomAndMember(@Param("chatRoom") ChatRoom chatRoom,@Param("sender") Member sender);

        @Query("""
            select
             t1
             from ChatParticipant t1
             join fetch t1.member t2
             where t1.isDeleted=false and t1.chatRoom.id =:roomId and t1.chatRoom.isGroupChat=false and t2 !=:blocker
            """)

    Optional<ChatParticipant> findByRoomIdAndMemberAndRoleMember(@Param("roomId") Long roomId,@Param("blocker") Member blocker);



    @Query("""
            select t1
            from ChatParticipant t1
            where t1.isDeleted=false and t1.chatRoom=:groupChatRoom and t1.member =:applicant
            
            """)
    Optional<ChatParticipant> findByChatRoomAndMember(@Param("groupChatRoom") ChatRoom groupChatRoom,@Param("applicant") Member applicant);

    // 1. 모든 1:1 채팅방 (비공개, 그룹 아님)
    @Query("""
    SELECT cp.chatRoom
    FROM ChatParticipant cp
    WHERE cp.member.id = :leaderId
      AND cp.chatRoom.isDeleted = false
      AND cp.chatRoom.isGroupChat = false
""")
    List<ChatRoom> findAllPrivateChatsForLeader(@Param("leaderId") Long leaderId);


    @Query("""
            select 
            t1
            from ChatParticipant t1
            join fetch t1.member t2
            where t1.chatRoom.id=:roomId and t1.isDeleted=false and t2.isDeleted=false
            """)
    List<ChatParticipant> findGroupChatAllParticipantByRoomId(@Param("roomId") Long roomId);


    @Query("""
            select t1
            from ChatParticipant t1
            join fetch t1.chatRoom t2
            join fetch t1.member t3
            where t2 in (select t4.chatRoom from ChatParticipant t4 where t4.member=:loginMember and t4.chatRoom.isGroupChat=false) and t1.member!=:loginMember and t1.isDeleted=false and t2.isDeleted=false and t3.isDeleted=false
            
            """)
    List<ChatParticipant> findAllPrivateChatParticipantByMember(@Param("loginMember") Member loginMember);


    @Query("""
            select distinct t1
            from ChatParticipant t1
            join fetch t1.chatRoom t2
            join t2.matchupBoard t3
            where t1.isDeleted =false and t2.isDeleted=false and t3.matchEndtime<=:thresholdTime and t2.isGroupChat=true
            """)
    List<ChatParticipant> findAfterThreeDaysOfMatchWithChatParticipantAndChatRoom(@Param("thresholdTime") LocalDateTime thresholdTime);

}

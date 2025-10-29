package com.multi.matchon.chat.domain;

import com.multi.matchon.common.domain.BaseEntity;
import com.multi.matchon.member.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name="chat_participant")
//@Setter: JPA entity에서 setter사용은 자제, test용
public class ChatParticipant extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="chat_participant_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="chat_room_id")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    @Column(name="is_deleted")
    @Builder.Default
    private Boolean isDeleted=false;

    public void deleteParticipant(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void changeChatRoom(ChatRoom chatRoom){
        if(this.chatRoom!=null)
            this.chatRoom.getChatParticipants().remove(this);

        this.chatRoom = chatRoom;

        if(!chatRoom.getChatParticipants().contains(this))
            chatRoom.getChatParticipants().add(this);

    }
}

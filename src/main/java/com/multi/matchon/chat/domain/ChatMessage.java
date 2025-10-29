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
@Table(name="chat_message")
//@Setter: JPA entity에서 setter사용은 자제, test용
public class ChatMessage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="chat_message_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="chat_room_id",nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="sender_id",nullable = false)
    private Member member;

    @Column(name="content",columnDefinition = "TEXT")
    private String content;

    @Column(name="is_deleted")
    @Builder.Default
    private Boolean isDeleted=false;


}

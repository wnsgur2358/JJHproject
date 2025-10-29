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
@Table(name="message_read_log")
//@Setter: JPA entity에서 setter사용은 자제, test용
public class MessageReadLog extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="message_read_log_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="chat_room_id",nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="receiver_id",nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="chat_message_id",nullable = false)
    private ChatMessage chatMessage;

    @Column(name="is_read",columnDefinition = "BOOLEAN DEFAULT FALSE")
    @Builder.Default
    private Boolean isRead=false;

    public void updateIsRead(Boolean isRead) {
        this.isRead = isRead;
    }
}

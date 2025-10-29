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
@Table(name="chat_user_block",uniqueConstraints = {@UniqueConstraint(name="UK_blocker_id_2_blocked_id", columnNames = {"block_id", "blocked_id"})})
public class ChatUserBlock extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_user_block_id")
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="blocker_id", nullable = false)
    private Member blocker;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="blocked_id", nullable = false)
    private Member blocked;



}

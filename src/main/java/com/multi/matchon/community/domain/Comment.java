package com.multi.matchon.community.domain;

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
@Table(name="comment")
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="board_id", nullable = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id", nullable = false)
    private Member member;

    @Column(name="content",nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name="is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    public void setIsDeleted(boolean b) {
        this.isDeleted = b;
    }
}


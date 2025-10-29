package com.multi.matchon.community.domain;

import com.multi.matchon.common.domain.BaseEntity;
import com.multi.matchon.member.domain.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "board")
public class Board extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "board_attachment_enabled", nullable = false)
    @Builder.Default
    private Boolean boardAttachmentEnabled = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 30)
    @Builder.Default
    private Category category = Category.FREEBOARD;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer", nullable = false)
    private Member member;

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(name = "pinned", nullable = false)
    @Builder.Default
    private boolean pinned = false;

    // === 비즈니스 로직 ===

    public void setIsDeleted(boolean deleted) {
        this.isDeleted = deleted;
    }

    public void update(
            @NotBlank @Size(max = 50) String title,
            @NotBlank String content,
            @NotNull Category category) {
        this.title = title;
        this.content = content;
        this.category = category;
    }

    public void setBoardAttachmentEnabled(boolean enabled) {
        this.boardAttachmentEnabled = enabled;
    }

    public void pin() {
        this.pinned = true;
    }

    public void unpin() {
        this.pinned = false;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

}

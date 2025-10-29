package com.multi.matchon.community.dto.req;

import com.multi.matchon.community.domain.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardRequest {

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 100, message = "제목은 100자 이하로 입력해주세요.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    @NotNull(message = "카테고리를 선택해주세요.")
    private Category category;

    private boolean pinned = false; // 관리자만 설정 가능

    public BoardRequest(String title, String content, Category category, boolean pinned) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.pinned = pinned;
    }

    public BoardRequest(String title, String content, Category category) {
        this(title, content, category, false);
    }

    public BoardRequest() {
    }
}

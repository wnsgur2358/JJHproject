package com.multi.matchon.community.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor            // 기본 생성자
@AllArgsConstructor           // 전체 필드 생성자
public class CommentRequest {
    @NotBlank(message = "댓글 내용을 입력해주세요.")
    @Size(max = 500, message = "댓글은 최대 500자까지 입력할 수 있습니다.")
    private String content;
}


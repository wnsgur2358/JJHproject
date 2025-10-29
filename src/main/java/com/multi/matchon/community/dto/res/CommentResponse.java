package com.multi.matchon.community.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentResponse {
    private String memberName;
    private String createdDate;
    private String content;
    private Long commentId;
    private Long memberId;
}


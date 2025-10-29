package com.multi.matchon.community.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class BoardListResponse {
    private Long id;
    private String title;
    private String categoryName;
    private String authorName;
    private LocalDateTime createdDate;
    private int commentCount;
    private boolean pinned;
}


package com.multi.matchon.community.dto.res;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReportResponse {
    private Long id;
    private String reportType;
    private Long targetId;
    private String targetWriterName;
    private String reporterName;
    private String reasonType;
    private String reason;
    private LocalDateTime createdDate;
    private Long boardId;
    private Long targetMemberId;
    private boolean targetExists;
    private boolean suspended;
    private boolean targetIsAdmin;

}


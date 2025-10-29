package com.multi.matchon.matchup.dto.res;


import com.multi.matchon.common.domain.Status;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ResMatchupRequestOverviewListDto {

    private Long boardId;

    private LocalDateTime matchDatetime;

    private Integer currentParticipantCount;

    private Integer maxParticipants;

    private Long requestId;

    private String applicantName;

    private String selfIntro;

    private Integer participantCount;

    private Status matchupStatus;

    private Integer matchupRequestSubmittedCount;

    private Integer matchupCancelSubmittedCount;

    private Boolean isDeleted;




    public ResMatchupRequestOverviewListDto(
            Long boardId,
            LocalDateTime matchDatetime,
            Integer currentParticipantCount,
            Integer maxParticipants,
            Long requestId,
            String applicantName,
            String selfIntro,
            Integer participantCount,
            Status matchupStatus,
            Integer matchupRequestSubmittedCount,
            Integer matchupCancelSubmittedCount,
            Boolean isDeleted
            ) {
        this.boardId = boardId;
        this.matchDatetime = matchDatetime;
        this.currentParticipantCount = currentParticipantCount;
        this.maxParticipants = maxParticipants;
        this.requestId = requestId;
        this.applicantName = applicantName;
        this.selfIntro = selfIntro;
        this.participantCount = participantCount;
        this.matchupStatus = matchupStatus;
        this.matchupRequestSubmittedCount = matchupRequestSubmittedCount;
        this.matchupCancelSubmittedCount = matchupCancelSubmittedCount;
        this.isDeleted = isDeleted;

    }
}

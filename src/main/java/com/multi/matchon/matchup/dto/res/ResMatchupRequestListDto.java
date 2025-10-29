package com.multi.matchon.matchup.dto.res;

import com.multi.matchon.common.domain.SportsTypeName;
import com.multi.matchon.common.domain.Status;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
public class ResMatchupRequestListDto {

    private Long boardId;

    private Long requestId;

    private SportsTypeName sportsTypeName;

    private String sportsFacilityName;

    private String sportsFacilityAddress;

    private LocalDateTime matchDatetime;

    private LocalDateTime matchEndtime;

    private Integer currentParticipantCount;

    private Integer maxParticipants;

    private Integer participantCount;

    private Status matchupStatus;

    private Integer matchupRequestSubmittedCount;

    private Integer matchupCancelSubmittedCount;

    private Long roomId;

    private Boolean isDeleted;

    public ResMatchupRequestListDto(Long boardId, Long requestId, SportsTypeName sportsTypeName, String sportsFacilityName, String sportsFacilityAddress,  LocalDateTime matchDatetime, LocalDateTime matchEndtime, Integer currentParticipantCount, Integer maxParticipants ,Integer participantCount, Status matchupStatus, Integer matchupRequestSubmittedCount, Integer matchupCancelSubmittedCount, Long roomId, Boolean isDeleted) {
        this.boardId = boardId;
        this.matchDatetime = matchDatetime;
        this.matchEndtime = matchEndtime;
        this.matchupStatus = matchupStatus;
        this.participantCount = participantCount;
        this.requestId = requestId;
        this.sportsFacilityAddress = sportsFacilityAddress;
        this.sportsFacilityName = sportsFacilityName;
        this.sportsTypeName = sportsTypeName;
        this.currentParticipantCount = currentParticipantCount;
        this.maxParticipants = maxParticipants;
        this.matchupRequestSubmittedCount = matchupRequestSubmittedCount;
        this.matchupCancelSubmittedCount = matchupCancelSubmittedCount;
        this.roomId = roomId;
        this.isDeleted = isDeleted;
    }
}

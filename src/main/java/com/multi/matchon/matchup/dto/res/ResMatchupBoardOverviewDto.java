package com.multi.matchon.matchup.dto.res;

import com.multi.matchon.common.domain.SportsTypeName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
public class ResMatchupBoardOverviewDto {

    private Long boardId;

    private String writerName;

    private SportsTypeName sportsTypeName;

    private String sportsFacilityName;

    private String sportsFacilityAddress;

    private LocalDateTime matchDatetime;

    private LocalDateTime matchEndtime;

    private Integer currentParticipantCount;

    private Integer maxParticipants;

    public ResMatchupBoardOverviewDto(Long boardId, String writerName, SportsTypeName sportsTypeName, String sportsFacilityName, String sportsFacilityAddress, LocalDateTime matchDatetime, LocalDateTime matchEndtime, Integer currentParticipantCount,  Integer maxParticipants) {
        this.boardId = boardId;
        this.writerName = writerName;
        this.currentParticipantCount = currentParticipantCount;
        this.matchDatetime = matchDatetime;
        this.matchEndtime = matchEndtime;
        this.maxParticipants = maxParticipants;
        this.sportsFacilityAddress = sportsFacilityAddress;
        this.sportsFacilityName = sportsFacilityName;
        this.sportsTypeName = sportsTypeName;
    }
}

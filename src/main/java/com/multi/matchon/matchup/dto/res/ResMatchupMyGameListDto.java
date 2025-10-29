package com.multi.matchon.matchup.dto.res;

import com.multi.matchon.common.domain.SportsTypeName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
public class ResMatchupMyGameListDto {

    private Long boardId;

    private LocalDateTime matchDatetime;

    private LocalDateTime matchEndtime;

    private SportsTypeName sportsTypeName;

    private String sportsFacilityName;

    private String sportsFacilityAddress;

    private Boolean isRatingInitialized;

    private Integer currentParticipantCount;

    private Integer maxParticipants;



    public ResMatchupMyGameListDto(Long boardId, LocalDateTime matchDatetime, LocalDateTime matchEndtime, SportsTypeName sportsTypeName, String sportsFacilityName, String sportsFacilityAddress, Boolean isRatingInitialized, Integer currentParticipantCount, Integer maxParticipants) {
        this.boardId = boardId;
        this.matchDatetime = matchDatetime;
        this.matchEndtime = matchEndtime;
        this.sportsFacilityAddress = sportsFacilityAddress;
        this.sportsFacilityName = sportsFacilityName;
        this.sportsTypeName = sportsTypeName;
        this.isRatingInitialized = isRatingInitialized;
        this.currentParticipantCount = currentParticipantCount;
        this.maxParticipants = maxParticipants;
    }
}

package com.multi.matchon.matchup.dto.req;

import com.multi.matchon.common.domain.SportsTypeName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
public class ReqMatchupRequestDto {

    @NotNull(message = "Matchup Board ")
    private Long boardId;

    private String writerName;

    private SportsTypeName sportsTypeName;


    private String sportsFacilityName;


    private String sportsFacilityAddress;


    private LocalDateTime matchDatetime;


    private LocalDateTime matchEndtime;


    private Integer currentParticipantCount;


    private Integer maxParticipants;

    @NotBlank
    private String selfIntro;

    @NotNull
    private Integer participantCount;

    public ReqMatchupRequestDto(Long boardId, String writerName, SportsTypeName sportsTypeName, String sportsFacilityName,  String sportsFacilityAddress, LocalDateTime matchDatetime, LocalDateTime matchEndtime, Integer currentParticipantCount,  Integer maxParticipants){
        this.boardId = boardId;
        this.currentParticipantCount = currentParticipantCount;
        this.matchDatetime = matchDatetime;
        this.matchEndtime = matchEndtime;
        this.maxParticipants = maxParticipants;
        this.sportsFacilityAddress = sportsFacilityAddress;
        this.sportsFacilityName = sportsFacilityName;
        this.sportsTypeName = sportsTypeName;
        this.writerName = writerName;
    }
}

package com.multi.matchon.matchup.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResMatchupRatingDto {

    private Long boardId;

    private Long evalId;

    private String evalName;

    private Long targetId;

    private String targetName;

    private Integer mannerScore;

    private Integer skillScore;

    private String review;

    private Boolean isCompleted;

    //등록용
    public ResMatchupRatingDto(Long boardId, Long evalId, String evalName, Long targetId, String targetName) {
        this.boardId = boardId;
        this.evalId = evalId;
        this.evalName = evalName;
        this.targetId = targetId;
        this.targetName = targetName;
    }

    //조회용
    public ResMatchupRatingDto(Long boardId, String evalName, String targetName, Integer mannerScore, Integer skillScore, String review){
        this.boardId = boardId;
        this.evalName = evalName;
        this.targetName = targetName;
        this.mannerScore = mannerScore;
        this.skillScore = skillScore;
        this.review = review;
    }
}

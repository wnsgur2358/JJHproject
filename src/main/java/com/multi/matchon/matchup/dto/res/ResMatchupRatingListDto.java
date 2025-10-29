package com.multi.matchon.matchup.dto.res;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResMatchupRatingListDto {

    private Long boardId;

    private String evalName;

    private String targetName;

    // 받은 경우

    private Long receivedEvalId;

    private Long receivedTargetId;

    private Integer receivedMannerScore;

    private Integer recievedSkillScore;

    private String receivedReview;

    private Boolean isCompletedReceive;


    // 보낸 경우

    private Long sendedEvalId;

    private Long sendedTargetId;

    private Integer sendedMannerScore;

    private Integer sendedSkillScore;

    private String sendedReview;

    private Boolean isCompletedSend;



    public ResMatchupRatingListDto(Long boardId, String evalName, String targetName,
                                   Long sendedEvalId, Long sendedTargetId,Integer sendedMannerScore, Integer sendedSkillScore, String sendedReview, Boolean isCompletedSend,
                                   Long receivedEvalId, Long receivedTargetId, Integer receivedMannerScore, Integer recievedSkillScore, String receivedReview, Boolean isCompletedReceive) {
        this.boardId = boardId;
        this.evalName = evalName;
        this.isCompletedReceive = isCompletedReceive;
        this.isCompletedSend = isCompletedSend;
        this.receivedEvalId = receivedEvalId;
        this.receivedTargetId = receivedTargetId;
        this.sendedEvalId = sendedEvalId;
        this.sendedTargetId = sendedTargetId;
        this.targetName = targetName;
        this.sendedReview = sendedReview;
        this.receivedReview = receivedReview;
        this.sendedMannerScore = sendedMannerScore;
        this.sendedSkillScore = sendedSkillScore;
        this.receivedMannerScore = receivedMannerScore;
        this.recievedSkillScore = recievedSkillScore;



    }
}

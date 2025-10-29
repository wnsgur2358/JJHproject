package com.multi.matchon.matchup.dto.req;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.N;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReqMatchupRatingDto {


    @NotNull(message = "Matchup 게시판 번호가 없습니다.")
    private Long boardId;

    @NotNull(message = "Matchup 평가자 번호가 없습니다.")
    private Long evalId;


    @NotNull(message = "Matchup 평가 대상자 번호가 없습니다.")
    private Long targetId;

    @NotNull(message = "Matchup 입력된 매너 점수가 없습니다.")
    @Min(value=1, message = "Matchup 매너 점수는 최소 1입니다.")
    @Max(value=5, message = "Matchup 매너 점수는 최고 5입니다.")
    private Integer mannerScore;

    @NotNull(message = "Matchup 입력된 실력 점수가 없습니다.")
    @Min(value=1, message = "Matchup 실력 점수는 최소 1입니다.")
    @Max(value=5, message = "Matchup 실력 점수는 최고 5입니다.")
    private Integer skillScore;

    @NotBlank(message = "리뷰를 남겨주세요")
    @Size(max=300, message = "Matchup 리뷰는 1~300자 내에 입력되어야합니다.")
    private String review;
}

package com.multi.matchon.team.dto.req;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqReviewDto {
    @NotBlank(message = "리뷰 내용을 입력해주세요")
    private String content;
    
    @Min(value = 1, message = "평점은 1점 이상이어야 합니다")
    @Max(value = 5, message = "평점은 5점 이하여야 합니다")
    private int rating;
}
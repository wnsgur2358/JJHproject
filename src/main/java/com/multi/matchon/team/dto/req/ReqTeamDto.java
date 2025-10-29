package com.multi.matchon.team.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReqTeamDto {

    @NotBlank(message = "팀 이름은 필수입니다.")
    @Size(max = 30, message = "팀 이름은 30자 이하로 입력해주세요.")
    private String teamName;

    private String teamRegion;

    private Boolean recruitmentStatus;

    @NotBlank(message = "팀 소개는 필수입니다.")
    @Size(max = 300, message = "팀 소개는 300자 이하로 입력해주세요.")
    private String teamIntroduction;

    private MultipartFile teamImageFile;

    private List<String> recruitingPositions;

    private Double teamRatingAverage;

    private Long teamId;

}

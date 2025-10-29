package com.multi.matchon.matchup.dto.req;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReqMatchupBoardEditDto {


    @NotBlank(message = "스포츠 종목을 선택해야합니다.")
    private String sportsTypeName;

    @NotBlank(message = "팀 이름이 선택되야합니다.")
    private String teamName;

    @NotBlank(message = "팀 소개를 입력해야합니다.")
    @Size(max=300, message = "팀 소개는 1~300자 내에 입력되야합니다.")
    private String teamIntro;

    @NotNull
    private MultipartFile reservationFile;

    @NotBlank(message = "경기장명을 입력해야합니다.")
    @Size(max=100, message = "경기장명은 1~100자 내에 입력되야합니다.")
    private String sportsFacilityName;

    @NotBlank(message = "경기장 주소를 입력해야합니다.")
    @Size(max=100, message = "경기장 주소는 1~100자 내에 입력되어야합니다.")
    private String sportsFacilityAddress;

    @NotNull(message = "현재 참가 인원을 입력해야합니다.")
    @Min(value = 1)
    @Max(value = 30)
    private Integer currentParticipantCount;

    @NotNull(message = "총 모집 인원을 입력해야합니다.")
    @Min(value = 1)
    @Max(value = 31)
    private Integer maxParticipants;

    @NotNull(message = "하한 매너 온도를 입력해야합니다.")
    @Min(value = 30)
    @Max(value = 40)
    private Double minMannerTemperature;

    @NotBlank(message = "경기 방식 소개를 입력해야합니다.")
    @Size(max=1000, message = "경기 방식 소개는 1000자까지 입력 가능합니다.")
    private String matchDescription;

    // 등록할 때와 달리 수정할 때 추가로 필요한 정보
    // 사용자가 입력하는 정보가 아니라 validation check는 안함

    private Long boardId;

    private String writerEmail;

    private String writerName;

    private String originalName;

    private String savedName;

    private String savedPath;

    private Double myMannerTemperature;




}

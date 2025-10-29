package com.multi.matchon.matchup.dto.req;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

import java.time.LocalTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReqMatchupBoardDto {


    @NotBlank(message = "Matchup 스포츠 종목을 선택해야합니다.")
    private String sportsTypeName;

    @NotBlank(message = "Matchup 팀 이름이 선택되야합니다.")
    private String teamName;

    @NotBlank(message = "Matchup 팀 소개를 입력해야합니다.")
    @Size(max=300, message = "Matchup 팀 소개는 1~300자 내에 입력되야합니다.")
    private String teamIntro;

    @NotNull
    private MultipartFile reservationFile;

    @NotBlank(message = "Matchup 경기장명을 입력해야합니다.")
    @Size(max=100, message = "Matchup 경기장명은 1~100자 내에 입력되야합니다.")
    private String sportsFacilityName;

    @NotBlank(message = "Matchup 경기장 주소를 입력해야합니다.")
    @Size(max=100, message = "Matchup 경기장 주소는 1~100자 내에 입력되어야합니다.")
    private String sportsFacilityAddress;

    @NotNull(message ="Matchup 경기 날짜를 입력해야합니다.")
    @Future(message = "Matchup 경기 시작 시간은 현재 시간 이후만 가능합니다.")
    private LocalDateTime matchDatetime;

    @NotNull(message = "Matchup 경기 진행 시간을 입력해야합니다.")
    @Min(value=30, message = "Matchup 30분 이상만 가능합니다.")
    @Max(value=180, message = "Matchup 180분 이하만 가능합니다.")
    private Integer matchDuration;

    @NotNull(message = "Matchup 현재 참가 인원을 입력해야합니다.")
    @Min(value = 1, message = "Matchup 참가 인원은 1명 이상이어야 합니다.")
    @Max(value = 30, message = "Matchup 참가 인원은 30명이하만 가능합니다.")
    private Integer currentParticipantCount;

    @NotNull(message = "Matchup 총 모집 인원을 입력해야합니다.")
    @Min(value = 1, message = "Matchup 총 참가 인원은 1명 이상이어야 합니다.")
    @Max(value = 31, message = "Matchup 총 참가 인원은 31명까지 가능합니다.")
    private Integer maxParticipants;

    @NotNull(message = "Matchup 하한 매너 온도를 입력해야합니다.")
    @Min(value = 30, message = "Matchup 하한 매너 온도는 30도 이상이어야 합니다.")
    @Max(value = 40, message = "Matchup 하한 매너 온도는 40도 이하여야합니다.")
    private Double minMannerTemperature;

    @NotBlank(message = "Matchup 경기 방식 소개를 입력해야합니다.")
    @Size(max=1000, message = "Matchup 경기 방식 소개는 1000자까지 입력 가능합니다.")
    private String matchDescription;




}

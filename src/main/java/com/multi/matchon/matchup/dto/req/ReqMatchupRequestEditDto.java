package com.multi.matchon.matchup.dto.req;

import com.multi.matchon.common.domain.SportsTypeName;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
public class ReqMatchupRequestEditDto {


    @NotNull
    private Long requestId;

    @NotNull
    @Future
    private LocalDateTime matchDatetime;

    @NotNull
    @Future
    private LocalDateTime matchEndtime;

    @NotBlank
    private String selfIntro;

    @NotNull
    private Integer participantCount;


}

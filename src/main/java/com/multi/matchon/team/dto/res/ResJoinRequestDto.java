package com.multi.matchon.team.dto.res;

import com.multi.matchon.common.domain.Status;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResJoinRequestDto {
    private Long requestId;
    private String applicantName;
    private String applicantEmail;
    private String introduction;
    private Status joinRequestStatus;

    public static ResJoinRequestDto from(com.multi.matchon.team.domain.TeamJoinRequest entity) {
        return ResJoinRequestDto.builder()
                .requestId(entity.getId())
                .applicantName(entity.getMember().getMemberName())
                .applicantEmail(entity.getMember().getMemberEmail())
                .introduction(entity.getIntroduction())
                .joinRequestStatus(entity.getJoinRequestStatus())
                .build();
    }
}

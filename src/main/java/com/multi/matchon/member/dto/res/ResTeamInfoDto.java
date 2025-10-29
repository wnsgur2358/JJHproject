package com.multi.matchon.member.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResTeamInfoDto {
    private String teamName;
    private String teamIntro;
}

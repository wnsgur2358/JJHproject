package com.multi.matchon.member.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponseDto {
    private String accessToken;
    private String refreshToken;
    private boolean isTemporaryPassword;

    public TokenResponseDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.isTemporaryPassword = false; // 기본값 처리
    }
}

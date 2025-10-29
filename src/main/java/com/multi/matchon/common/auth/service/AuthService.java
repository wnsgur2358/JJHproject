package com.multi.matchon.common.auth.service;

import com.multi.matchon.member.domain.Member;
import com.multi.matchon.member.dto.req.LoginRequestDto;
import com.multi.matchon.member.dto.req.SignupRequestDto;
import com.multi.matchon.member.dto.res.TokenResponseDto;

public interface AuthService {

    void signupUser(SignupRequestDto dto);

    void signupHost(SignupRequestDto dto);

    void logout(String token);

    TokenResponseDto login(LoginRequestDto dto);

    TokenResponseDto reissue(String refreshToken);

    void changePassword(String newPassword, String confirmPassword, Member member);
}

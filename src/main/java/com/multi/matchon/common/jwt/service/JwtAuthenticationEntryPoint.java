package com.multi.matchon.common.jwt.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        log.error("[401 Unauthorized] 인증 실패: {}", authException.getMessage());

        String requestedWith = request.getHeader("X-Requested-With");
        boolean isAjax = "XMLHttpRequest".equals(requestedWith);

        if (isAjax) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인이 필요한 서비스입니다.");
        } else {
            response.sendRedirect("/login");
        }
    }
}

package com.multi.matchon.common.jwt.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        log.error("[403 Forbidden] 인가 실패: {}", accessDeniedException.getMessage());
        response.sendRedirect("/error/authorities");

//        response.sendError(HttpServletResponse.SC_FORBIDDEN, "접근 권한이 없습니다.");
    }
}

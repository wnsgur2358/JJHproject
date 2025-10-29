package com.multi.matchon.common.jwt.service;

import com.multi.matchon.common.auth.dto.CustomUser;
import com.multi.matchon.common.auth.service.CustomUserDetailsService;
import com.multi.matchon.member.domain.MemberRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();
        String token = resolveToken(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            String email = jwtTokenProvider.getEmailFromToken(token);
            CustomUser userDetails = (CustomUser) customUserDetailsService.loadUserByUsername(email);

            // ✅ 정지된 회원이면 로그인 차단 및 쿠키 제거 + 리다이렉트
            if (userDetails.getMember().isSuspended()) {
                log.warn("[JwtFilter] 정지된 계정 accessToken 로그인 차단: {}", email);
                sendSuspendedResponse(response, userDetails);
                return;
            }

            // 정상 인증 처리
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.info("[JwtFilter] 인증 성공 - 사용자: {}", email);
        }

        else {
            String refreshToken = resolveRefreshToken(request);

            if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
                log.info("[JwtFilter] accessToken 없음 또는 만료됨 → refreshToken으로 재발급");

                String email = jwtTokenProvider.getEmailFromToken(refreshToken);
                MemberRole role = jwtTokenProvider.getRoleFromToken(refreshToken);
                CustomUser userDetails = (CustomUser) customUserDetailsService.loadUserByUsername(email);

                // ✅ refreshToken 인증에서도 정지된 회원 차단
                if (userDetails.getMember().isSuspended()) {
                    log.warn("[JwtFilter] 정지된 계정 refreshToken 인증 차단: {}", email);
                    sendSuspendedResponse(response, userDetails);
                    return;
                }

                // accessToken 재발급
                String newAccessToken = jwtTokenProvider.createAccessToken(email, role);
                Cookie accessCookie = new Cookie("Authorization", newAccessToken);
                accessCookie.setHttpOnly(false);
                accessCookie.setPath("/");
                accessCookie.setMaxAge(60 * 60);
                response.addCookie(accessCookie);

                // SecurityContext 갱신
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                filterChain.doFilter(request, response);
                return;
            }

            // accessToken + refreshToken 모두 만료 → 인증 필요
            else if (!isExcludedPath(uri)) {
                log.warn("[JwtFilter] 모든 토큰 없음 또는 만료 → 로그인 필요");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * ✅ 정지된 회원 응답 처리: 쿠키 제거 + 로그인 페이지로 리다이렉트
     */
    private void sendSuspendedResponse(HttpServletResponse response, CustomUser userDetails) throws IOException {
        // ❗ 쿠키 삭제 (무한 리다이렉트 방지)
        Cookie deleteAccess = new Cookie("Authorization", null);
        deleteAccess.setPath("/");
        deleteAccess.setMaxAge(0);
        response.addCookie(deleteAccess);

        Cookie deleteRefresh = new Cookie("Refresh-Token", null);
        deleteRefresh.setPath("/");
        deleteRefresh.setMaxAge(0);
        response.addCookie(deleteRefresh);

        // ❗ 로그인 페이지로 리다이렉트 + 정지 사유 전달
        String redirectUrl = "/login?error=suspended";
        if (userDetails.getMember().getSuspendedUntil() != null) {
            String formattedDateTime = userDetails.getMember()
                    .getSuspendedUntil()
                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME); // "2025-06-15T15:30:00"

            redirectUrl += "&date=" + URLEncoder.encode(formattedDateTime, StandardCharsets.UTF_8);
        }

        response.sendRedirect(redirectUrl);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("Authorization".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    private String resolveRefreshToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("Refresh-Token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private boolean isExcludedPath(String uri) {
        return uri.equals("/") ||
                uri.startsWith("/main") ||
                uri.startsWith("/login") ||
                uri.startsWith("/signup") ||
                uri.startsWith("/auth") ||
                uri.startsWith("/css") ||
                uri.startsWith("/js") ||
                uri.startsWith("/img") ||
                uri.startsWith("/redirect") ||
                uri.equals("/favicon.ico") ||
                uri.equals("/introduction");
    }
}

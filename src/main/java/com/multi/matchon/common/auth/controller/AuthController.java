package com.multi.matchon.common.auth.controller;

import com.multi.matchon.common.auth.dto.CustomUser;
import com.multi.matchon.common.auth.service.AuthService;
import com.multi.matchon.common.auth.service.MailService;
import com.multi.matchon.common.jwt.repository.RefreshTokenRepository;
import com.multi.matchon.common.jwt.service.JwtTokenProvider;
import com.multi.matchon.member.domain.Member;
import com.multi.matchon.member.dto.req.LoginRequestDto;
import com.multi.matchon.member.dto.req.SignupRequestDto;
import com.multi.matchon.member.dto.res.TokenResponseDto;
import com.multi.matchon.member.repository.MemberRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;



    @PostMapping("/signup/user")
    public ResponseEntity<String> signupUser(@RequestBody SignupRequestDto requestDto) {
        authService.signupUser(requestDto);
        return ResponseEntity.ok("사용자 회원가입 성공");
    }

    @PostMapping("/signup/host")
    public ResponseEntity<String> signupHost(@RequestBody SignupRequestDto requestDto) {
        authService.signupHost(requestDto);
        return ResponseEntity.ok("주최자 회원가입 성공");
    }

    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam String email) {
        // 삭제되지 않은 계정 기준으로 체크
        boolean exists = memberRepository.findByMemberEmailAndIsDeletedFalse(email).isPresent();
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    // 현재 로그인 상태 확인 API (main.html 에서 fetch로 확인)
    @GetMapping("/check")
    public ResponseEntity<?> checkLogin(@AuthenticationPrincipal CustomUser user) {
        if (user != null) {
            // 사용자 역할 정보도 함께 전달
            return ResponseEntity.ok(Map.of(
                    "message", "로그인됨",
                    "role", user.getMember().getMemberRole().name(),
                    "isTemporaryPassword", user.isTemporaryPassword()
            ));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 필요");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody LoginRequestDto requestDto) {
        TokenResponseDto tokenResponse = authService.login(requestDto);

        // accessToken → HttpOnly 쿠키로 발급 (JS 접근 불가)
        ResponseCookie accessTokenCookie = ResponseCookie.from("Authorization", tokenResponse.getAccessToken())
                .httpOnly(false)
                .path("/")
                .maxAge(Duration.ofHours(1))
                .build();

        // refreshToken HttpOnly 쿠키로 발급
        ResponseCookie refreshTokenCookie = ResponseCookie.from("Refresh-Token", tokenResponse.getRefreshToken())
                .httpOnly(true)
                .path("/")
                .maxAge(Duration.ofDays(14))
                .build();

        // 헤더에 두 쿠키를 같이 실어 보냄
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        // refreshToken은 body에 보내지 않고, 쿠키에만 담음
        return ResponseEntity.ok()
                .headers(headers)
                .body(new TokenResponseDto(tokenResponse.getAccessToken(), null, tokenResponse.isTemporaryPassword())); // refreshToken은 응답에서 제거
    }



    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String token = resolveToken(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            authService.logout(token); // DB에서 refreshToken 삭제

            // accessToken 쿠키 제거
            ResponseCookie deleteAccessToken = ResponseCookie.from("Authorization", "")
                    .path("/")
                    .maxAge(0)
                    .httpOnly(true)
                    .build();

            // refreshToken 쿠키 제거
            ResponseCookie deleteRefreshToken = ResponseCookie.from("Refresh-Token", "")
                    .path("/")
                    .maxAge(0)
                    .httpOnly(true)
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, deleteAccessToken.toString())
                    .header(HttpHeaders.SET_COOKIE, deleteRefreshToken.toString())
                    .body("로그아웃 완료");
        }

        return ResponseEntity.badRequest().body("유효한 토큰이 없습니다.");
    }

    // 토큰 재발급 요청
    @PostMapping("/reissue")
    public ResponseEntity<TokenResponseDto> reissue(@CookieValue("Refresh-Token") String refreshToken) {

        // refreshToken 더 이상 헤더로 받지 않고, 쿠키로 받음
        TokenResponseDto tokenResponse = authService.reissue(refreshToken);

        // accessToken 새로 발급 → 쿠키로 다시 설정
        ResponseCookie newAccessTokenCookie = ResponseCookie.from("Authorization", tokenResponse.getAccessToken())
                .httpOnly(true)
                .path("/")
                .maxAge(Duration.ofHours(1))
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, newAccessTokenCookie.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(new TokenResponseDto(tokenResponse.getAccessToken(), null));
    }

    // accessToken 추출 (Authorization 쿠키 또는 Bearer 헤더)
    private String resolveToken(HttpServletRequest request) {

        // 1. Authorization 헤더
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        // 2. 쿠키에서 Authorization
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("Authorization".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    // 사용자 이메일로 임시 비밀번호 발송
    @PostMapping("/reset-password")
    public ResponseEntity<String> sendTemporaryPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        Optional<Member> optional = memberRepository.findByMemberEmailAndIsDeletedFalse(email);

        if (optional.isEmpty()) {
            return ResponseEntity.badRequest().body("존재하지 않는 이메일입니다.");
        }

        String tempPassword = UUID.randomUUID().toString().substring(0, 10);
        String encoded = passwordEncoder.encode(tempPassword);

        Member member = optional.get();
        member.setTemporaryPassword(encoded);     // 기존 비밀번호는 유지
        member.setIsTemporaryPassword(true);      // 플래그 ON
        memberRepository.save(member);

        mailService.sendTemporaryPassword(email, tempPassword);
        return ResponseEntity.ok("임시 비밀번호가 이메일로 전송되었습니다.");
    }

    // 로그인 후 새 비밀번호로 변경
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @AuthenticationPrincipal CustomUser user,
            @RequestBody Map<String, String> request) {

        try {
            String newPassword = request.get("newPassword");
            String confirmPassword = request.get("confirmPassword");

            Member member = user.getMember();
            authService.changePassword(newPassword, confirmPassword, member);

            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private boolean isValidPassword(String password) {
        return password.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()]).{8,}$");
    }
}
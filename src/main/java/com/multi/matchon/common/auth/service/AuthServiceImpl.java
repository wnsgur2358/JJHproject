package com.multi.matchon.common.auth.service;

import com.multi.matchon.common.domain.SportsType;
import com.multi.matchon.common.domain.SportsTypeName;
import com.multi.matchon.common.jwt.domain.RefreshToken;
import com.multi.matchon.common.jwt.repository.RefreshTokenRepository;
import com.multi.matchon.common.jwt.service.JwtTokenProvider;
import com.multi.matchon.common.repository.PositionsRepository;
import com.multi.matchon.common.repository.SportsTypeRepository;
import com.multi.matchon.event.domain.HostProfile;
import com.multi.matchon.event.repository.HostProfileRepository;
import com.multi.matchon.member.domain.Member;
import com.multi.matchon.member.domain.MemberRole;
import com.multi.matchon.member.dto.req.LoginRequestDto;
import com.multi.matchon.member.dto.req.SignupRequestDto;
import com.multi.matchon.member.dto.res.TokenResponseDto;
import com.multi.matchon.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final HostProfileRepository hostProfileRepository;

    @Override
    @Transactional
    public void signupUser(SignupRequestDto dto) {
        validatePassword(dto.getPassword());
        Optional<Member> existing = memberRepository.findByMemberEmail(dto.getEmail());

        if (existing.isPresent()) {
            Member member = existing.get();
            if (member.getIsDeleted()) {
                member.restoreAsUser(passwordEncoder.encode(dto.getPassword()), dto.getName());
                memberRepository.save(member);
                return;
            }
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        Member member = Member.builder()
                .memberEmail(dto.getEmail())
                .memberPassword(passwordEncoder.encode(dto.getPassword()))
                .memberName(dto.getName())
                .memberRole(MemberRole.USER)
                .myTemperature(36.5)
                .pictureAttachmentEnabled(true)
                .emailAgreement(Boolean.TRUE.equals(dto.getEmailAgreement()))
                .isDeleted(false)
                .build();

        memberRepository.save(member);
    }

    @Override
    @Transactional
    public void signupHost(SignupRequestDto dto) {
        validatePassword(dto.getPassword());
        Optional<Member> existing = memberRepository.findByMemberEmail(dto.getEmail());

        if (existing.isPresent()) {
            Member member = existing.get();
            if (member.getIsDeleted()) {
                member.restoreAsHost(passwordEncoder.encode(dto.getPassword()), dto.getName());
                memberRepository.save(member);

                if (hostProfileRepository.findByMember(member).isEmpty()) {
                    HostProfile hostProfile = HostProfile.builder()
                            .member(member)
                            .hostName(null)
                            .build();
                    hostProfileRepository.save(hostProfile);
                }
                return;
            }
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        Member member = Member.builder()
                .memberEmail(dto.getEmail())
                .memberPassword(passwordEncoder.encode(dto.getPassword()))
                .memberName(dto.getName())
                .memberRole(MemberRole.HOST)
                .pictureAttachmentEnabled(true)
                .emailAgreement(Boolean.TRUE.equals(dto.getEmailAgreement()))
                .isDeleted(false)
                .build();

        Member savedMember = memberRepository.saveAndFlush(member);

        HostProfile hostProfile = HostProfile.builder()
                .member(savedMember)
                .hostName(null)
                .build();

        hostProfileRepository.save(hostProfile);
    }

    @Override
    @Transactional
    public void logout(String token) {
        String email = jwtTokenProvider.getEmailFromToken(token);

        Member member = memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        refreshTokenRepository.deleteByMember(member);
    }

    @Override
    @Transactional
    public TokenResponseDto login(LoginRequestDto dto) {
        Member member = memberRepository.findByMemberEmailAndIsDeletedFalse(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        boolean normalLogin = passwordEncoder.matches(dto.getPassword(), member.getMemberPassword());
        boolean tempLogin = member.getIsTemporaryPassword()
                && member.getTemporaryPassword() != null
                && passwordEncoder.matches(dto.getPassword(), member.getTemporaryPassword());

        if (!normalLogin && !tempLogin) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtTokenProvider.createAccessToken(member.getMemberEmail(), member.getMemberRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getMemberEmail(), member.getMemberRole());

        refreshTokenRepository.findByMember(member).ifPresentOrElse(
                existing -> existing.update(refreshToken, LocalDateTime.now().plusDays(14)),
                () -> refreshTokenRepository.save(
                        RefreshToken.builder()
                                .member(member)
                                .refreshTokenData(refreshToken)
                                .refreshTokenExpiredDate(LocalDateTime.now().plusDays(14))
                                .build()
                )
        );

        // 정상 로그인일 경우 임시 로그인 플래그 해제
        if (normalLogin && member.getIsTemporaryPassword()) {
            member.setIsTemporaryPassword(false);
            member.setTemporaryPassword(null);
            memberRepository.save(member);
        }

        return new TokenResponseDto(accessToken, refreshToken, tempLogin);
    }

    @Override
    @Transactional
    public TokenResponseDto reissue(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("유효하지 않은 RefreshToken입니다.");
        }

        String email = jwtTokenProvider.getEmailFromToken(refreshToken);

        Member member = memberRepository.findByMemberEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        RefreshToken saved = refreshTokenRepository.findByMember(member)
                .orElseThrow(() -> new RuntimeException("저장된 RefreshToken 없음"));

        if (!saved.getRefreshTokenData().equals(refreshToken)) {
            throw new RuntimeException("RefreshToken 불일치");
        }

        String newAccessToken = jwtTokenProvider.createAccessToken(email, member.getMemberRole());
        return new TokenResponseDto(newAccessToken, refreshToken);
    }

    // 비밀번호 체크
    private void validatePassword(String password) {
        if (!password.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>])[A-Za-z\\d!@#$%^&*(),.?\":{}|<>]{8,}$")) {
            throw new IllegalArgumentException("비밀번호는 대문자, 소문자, 숫자, 특수문자를 포함한 8자 이상이어야 합니다.");
        }

        // 같은 문자 3번 이상 연속 사용 금지
        if (password.matches("(.)\\1\\1")) {
            throw new IllegalArgumentException("같은 문자를 연속으로 사용할 수 없습니다.");
        }
    }

    @Override
    @Transactional
    public void changePassword(String newPassword, String confirmPassword, Member memberParam) {

        // 사용자 정보 다시 조회
        Member member = memberRepository.findById(memberParam.getId())
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        // 비밀번호 일치 여부 확인
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 유효성 검사
        if (!isValidPassword(newPassword)) {
            throw new IllegalArgumentException("비밀번호는 8자 이상이며 숫자, 대소문자, 특수문자를 포함해야 합니다.");
        }

        // 현재 비밀번호와 동일한지 확인
        if (passwordEncoder.matches(newPassword, member.getMemberPassword())) {
            throw new IllegalArgumentException("현재 사용 중인 비밀번호와 동일합니다.");
        }

        // 직전 비밀번호와 동일한지 확인
        if (member.getPreviousPassword() != null &&
                passwordEncoder.matches(newPassword, member.getPreviousPassword())) {
            throw new IllegalArgumentException("직전에 사용한 비밀번호와 동일합니다.");
        }

        // 비밀번호 변경 및 이전 비밀번호 저장
        if (member.getIsTemporaryPassword()) {
            // 임시 비밀번호 사용자는 previousPassword를 업데이트하지 않음
            member.updatePassword(passwordEncoder.encode(newPassword));
        } else {
            // 일반 사용자 → 이전 비밀번호 저장
            member.updatePasswordWithHistory(passwordEncoder.encode(newPassword));
        }
        member.setIsTemporaryPassword(false); // 임시 비밀번호 해제
        memberRepository.save(member);

        System.out.println("새 비밀번호 설정 완료: 사용자 이메일 = " + member.getMemberEmail());
    }

    private boolean isValidPassword(String password) {
        return password.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()]).{8,}$");
    }
}

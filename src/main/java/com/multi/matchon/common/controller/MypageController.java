package com.multi.matchon.common.controller;

import com.multi.matchon.common.auth.dto.CustomUser;
import com.multi.matchon.common.domain.PositionName;
import com.multi.matchon.common.domain.TimeType;
import com.multi.matchon.common.service.MypageService;
import com.multi.matchon.event.repository.HostProfileRepository;
import com.multi.matchon.member.domain.Member;
import com.multi.matchon.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MypageController {

    private final MemberService memberService;
    private final MypageService mypageService;
    private final HostProfileRepository hostProfileRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    @Transactional(readOnly = true)
    public String getMypage(@AuthenticationPrincipal CustomUser user, Model model) {
        Member member = memberService.findForMypage(user.getUsername());
        Map<String, Object> data = mypageService.getMypageInfo(member);
        data.forEach(model::addAttribute);

        model.addAttribute("myPosition", member.getPositions() != null ? member.getPositions().getPositionName().name() : "");
        model.addAttribute("myTimeType", member.getTimeType() != null ? member.getTimeType().name() : "");
        model.addAttribute("emailAgreement", member.getEmailAgreement());
        return "mypage/mypage";
    }

    @PostMapping("/uploadProfile")
    public ResponseEntity<String> uploadProfile(@AuthenticationPrincipal CustomUser user,
                                                @RequestParam MultipartFile profileImage) {
        try {
            Member member = memberService.findForMypage(user.getUsername());
            mypageService.uploadProfileImage(member, profileImage);
            return ResponseEntity.ok("업로드 성공");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("프로필 이미지 업로드 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("업로드 중 문제가 발생했습니다.");
        }
    }

    @PostMapping("/hostName")
    public ResponseEntity<String> updateHostName(@AuthenticationPrincipal CustomUser user,
                                                 @RequestParam String hostName) {
        Member member = memberService.findForMypage(user.getUsername());
        mypageService.updateHostName(member, hostName);
        return ResponseEntity.ok("기관명 저장 완료");
    }

    @GetMapping("/hostName/exist")
    @ResponseBody
    public ResponseEntity<Boolean> checkHostNameExist(@RequestParam String name) {
        boolean exists = hostProfileRepository.findByHostName(name).isPresent();
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/enums")
    @ResponseBody
    public Map<String, Object> getEnums() {
        Map<String, Object> result = new HashMap<>();
        result.put("positionNames", Arrays.stream(PositionName.values()).map(Enum::name).toList());
        result.put("timeTypes", Arrays.stream(TimeType.values()).map(Enum::name).toList());
        return result;
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateMypage(@AuthenticationPrincipal CustomUser user,
                                               @RequestBody Map<String, Object> payload) {
        try {
            PositionName positionName = PositionName.valueOf((String) payload.get("positionName"));
            TimeType timeType = TimeType.valueOf((String) payload.get("timeType"));
            //Double temperature = Double.valueOf(payload.get("temperature").toString());

            mypageService.updateMypage(user.getUsername(), positionName, timeType);
            return ResponseEntity.ok("수정 완료");
        } catch (Exception e) {
            log.error("마이페이지 수정 실패", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("수정 실패: " + e.getMessage());
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(@AuthenticationPrincipal CustomUser user,
                                           @RequestParam String password) {
        Member member = memberService.findByEmail(user.getUsername());

        if (!passwordEncoder.matches(password, member.getMemberPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("비밀번호가 일치하지 않습니다.");
        }

        mypageService.withdraw(member);
        return ResponseEntity.ok("탈퇴 완료");
    }

    @DeleteMapping("/deleteProfile")
    public ResponseEntity<String> deleteProfile(@AuthenticationPrincipal CustomUser user) {
        Member member = memberService.findForMypage(user.getUsername());
        mypageService.deleteProfileImage(member);
        return ResponseEntity.ok("삭제 완료");

    }

    // 이메일 동의
    @PutMapping("/email-agreement")
    public ResponseEntity<String> updateEmailAgreement(@AuthenticationPrincipal CustomUser user,
                                                       @RequestBody Map<String, Boolean> payload) {
        boolean agreement = payload.getOrDefault("emailAgreement", false);
        Member member = memberService.findByEmail(user.getUsername());
        mypageService.updateEmailAgreement(member, agreement);
        return ResponseEntity.ok("변경 완료");
    }
}

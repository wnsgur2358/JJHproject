package com.multi.matchon.member.controller;

import com.multi.matchon.common.auth.dto.CustomUser;
import com.multi.matchon.common.dto.res.ApiResponse;
import com.multi.matchon.member.dto.res.ResTeamInfoDto;
import com.multi.matchon.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;

    @ResponseBody
    @GetMapping("/search/team-name")
    public ResponseEntity<ApiResponse<ResTeamInfoDto>> getResTeamInfoByMember(@AuthenticationPrincipal CustomUser user){
        ResTeamInfoDto resTeamInfoDto = memberService.findResTeamInfoByMember(user.getMember());
        return ResponseEntity.ok().body(ApiResponse.ok(resTeamInfoDto));
    }


    @ResponseBody
    @GetMapping("/search/my-temperature")
    public ResponseEntity<ApiResponse<Double>> getMyTemperatureByMember(@AuthenticationPrincipal CustomUser user){
        Double myTemperature = memberService.findMyTemperatureByMember(user.getMember());
        return ResponseEntity.ok().body(ApiResponse.ok(myTemperature));
    }

    @ResponseBody
    @PostMapping("/suspend")
    public ResponseEntity<ApiResponse<String>> suspendMember(@RequestParam Long memberId,
                                                             @RequestParam(required = false) Integer days) {
        // days가 null이면 영구정지
        memberService.suspendMemberById(memberId, days);
        return ResponseEntity.ok(ApiResponse.ok("회원이 정지되었습니다."));
    }

    @ResponseBody
    @PostMapping("/unsuspend")
    public ResponseEntity<ApiResponse<String>> unsuspendMember(@RequestParam Long memberId) {
        memberService.unsuspendMemberById(memberId);
        return ResponseEntity.ok(ApiResponse.ok("회원의 정지가 해제되었습니다."));
    }


}

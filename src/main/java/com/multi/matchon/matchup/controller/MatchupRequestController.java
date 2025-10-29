package com.multi.matchon.matchup.controller;

import com.multi.matchon.common.auth.dto.CustomUser;
import com.multi.matchon.common.dto.res.ApiResponse;
import com.multi.matchon.common.dto.res.PageResponseDto;
import com.multi.matchon.matchup.dto.req.ReqMatchupRequestDto;
import com.multi.matchon.matchup.dto.req.ReqMatchupRequestEditDto;
import com.multi.matchon.matchup.dto.res.*;
import com.multi.matchon.matchup.service.MatchupBoardService;
import com.multi.matchon.matchup.service.MatchupRequestService;
import com.multi.matchon.matchup.service.MatchupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/matchup/request")
@Slf4j
@RequiredArgsConstructor
public class MatchupRequestController {

    private final MatchupService matchupService;
    private final MatchupRequestService matchupRequestService;
    private final MatchupBoardService matchupBoardService;

    // 참가 요청
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping()
    public ModelAndView showMatchupRequestRegisterPage(@RequestParam("boardId") Long boardId, ModelAndView mv) throws RuntimeException {
        ReqMatchupRequestDto reqMatchupRequestDto = matchupRequestService.findReqMatchupRequestDtoByBoardId(boardId);
        mv.addObject("reqMatchupRequestDto",reqMatchupRequestDto);
        mv.setViewName("matchup/matchup-request-register");

        return mv;
    }
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping()
    public String registerMatchupRequest(@Valid @ModelAttribute ReqMatchupRequestDto reqMatchupRequestDto, @AuthenticationPrincipal CustomUser user){

        matchupRequestService.registerMatchupRequest(reqMatchupRequestDto, user);
        log.info("matchup request 참가 요청 완료");
        return "redirect:/matchup/request/my";
    }

    // 참가 요청 상세보기
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/detail")
    public ModelAndView showMatchupRequestDetailPage(@RequestParam("request-id") Long requestId, ModelAndView mv){

        ResMatchupRequestDto resMatchupRequestDto = matchupRequestService.findResMatchRequestDtoByRequestId(requestId);
        mv.addObject("resMatchupRequestDto",resMatchupRequestDto);
        mv.setViewName("matchup/matchup-request-detail");
        return mv;
    }

    // 내 참가 요청 목록
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/my")
    public String showMyMatchupRequestPage(){
        return "matchup/matchup-request-my";
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/my/list")
    @ResponseBody
    public ResponseEntity<ApiResponse<PageResponseDto<ResMatchupRequestListDto>>> listMyMatchupRequestByFilter(@RequestParam("page") int page, @RequestParam(value="size", required = false, defaultValue = "4") int size , @AuthenticationPrincipal CustomUser user, @RequestParam("sportsType") String sportsType, @RequestParam("date") String date, @RequestParam("availableFilter") Boolean availableFilter){
        PageRequest pageRequest = PageRequest.of(page,size);
        PageResponseDto<ResMatchupRequestListDto> pageResponseDto = matchupRequestService.findAllMyMatchupRequestWithPaging(pageRequest, user, sportsType, date, availableFilter);
        return ResponseEntity.ok(ApiResponse.ok(pageResponseDto));
    }

    // 게시물에 참가요청 목록
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/board")
    public ModelAndView showMatchupRequestByBoardPage(@RequestParam("board-id") Long boardId, ModelAndView mv){

        ResMatchupBoardOverviewDto resMatchupBoardOverviewDto = matchupBoardService.findResMatchupOverviewDto(boardId);
        mv.addObject("resMatchupBoardOverviewDto",resMatchupBoardOverviewDto);
        mv.setViewName("matchup/matchup-request-board-list");
        return mv;
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/board/list")
    @ResponseBody
    public ResponseEntity<ApiResponse<PageResponseDto<ResMatchupRequestOverviewListDto>>> listMatchupRequestByBoardAndFilter(@RequestParam("page") int page, @RequestParam(value="size", required = false, defaultValue = "4") int size , @RequestParam("board-id") Long boardId){
        PageRequest pageRequest = PageRequest.of(page,size);
        PageResponseDto<ResMatchupRequestOverviewListDto> pageResponseDto = matchupRequestService.findAllMatchupRequestByBoardWithPaging(pageRequest, boardId);
        return ResponseEntity.ok(ApiResponse.ok(pageResponseDto));
    }


    // 참가자: 참가 요청 수정 페이지 이동
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/edit")
    public ModelAndView showMatchupRequestEditPage(@RequestParam("request-id") Long requestId, ModelAndView mv){
        ResMatchupRequestDto resMatchupRequestDto = matchupRequestService.findResMatchRequestDtoByRequestIdAndModifyStatus(requestId);
        mv.addObject("resMatchupRequestDto",resMatchupRequestDto);
        mv.setViewName("matchup/matchup-request-edit");
        return mv;
    }

    // 참가자: 참가 요청 수정
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/edit")
    public String editMatchupRequest(@Valid @ModelAttribute ReqMatchupRequestEditDto reqMatchupRequestEditDto, @RequestParam("request-id") Long requestId, @AuthenticationPrincipal CustomUser user){
        matchupRequestService.updateMatchupRequest(reqMatchupRequestEditDto, requestId, user);
        return "redirect:/matchup/request/my";
    }

    // 참가자: 참가 취소
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/cancel")
    public String cancelMatchupRequestBeforeApproval(@RequestParam("board-id") Long boardId, @RequestParam("request-id") Long requestId, @AuthenticationPrincipal CustomUser user){
        matchupRequestService.cancelMatchupRequestBeforeApproval(boardId, requestId, user);
        return "redirect:/matchup/request/my";
    }

    // 참가자: 재요청
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/retry")
    public ModelAndView showMatchupRequestEditPage(@RequestParam("board-id") Long boardId, @RequestParam("request-id") Long requestId, @AuthenticationPrincipal CustomUser user, ModelAndView mv){
        ResMatchupRequestDto resMatchupRequestDto = matchupRequestService.checkRetryMatchupRequest(boardId, requestId, user);
        mv.addObject("resMatchupRequestDto",resMatchupRequestDto);
        mv.setViewName("matchup/matchup-request-edit");
        return mv;
    }

    // 참가자: 승인 취소 요청
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/cancel-request")
    public String matchupRequestCancelAfterApproval(@RequestParam("board-id") Long boardId, @RequestParam("request-id") Long requestId, @AuthenticationPrincipal CustomUser user){
        matchupRequestService.matchupRequestCancelAfterApproval(boardId, requestId, user);
        return "redirect:/matchup/request/my";
    }

    //작성자 승인- 참가 요청 승인, 취소 요청 승인
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/approve")
    public String approveRequest(@RequestParam("board-id") Long boardId, @RequestParam("request-id") Long requestId,  @AuthenticationPrincipal CustomUser user){ //신원 확인
        matchupRequestService.approveMatchupRequest(boardId, requestId, user);
        return "redirect:/matchup/request/board?board-id="+boardId;
    }

    // 작성자 반려- 참가 요청 반려, 취소 요청 반려
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/deny")
    public String denyRequest(@RequestParam("board-id") Long boardId, @RequestParam("request-id") Long requestId,  @AuthenticationPrincipal CustomUser user){ //신원 확인
        matchupRequestService.denyMatchupRequest(boardId, requestId, user);
        return "redirect:/matchup/request/board?board-id="+boardId;
    }

}

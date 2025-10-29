package com.multi.matchon.matchup.controller;

import com.multi.matchon.common.auth.dto.CustomUser;
import com.multi.matchon.common.dto.res.ApiResponse;
import com.multi.matchon.common.dto.res.PageResponseDto;
import com.multi.matchon.common.dto.res.ResNotificationDto;
import com.multi.matchon.matchup.dto.req.ReqMatchupBoardDto;
import com.multi.matchon.matchup.dto.req.ReqMatchupBoardEditDto;
import com.multi.matchon.matchup.dto.res.ResMatchupBoardDto;
import com.multi.matchon.matchup.dto.res.ResMatchupBoardListDto;
import com.multi.matchon.matchup.service.MatchupBoardService;
import com.multi.matchon.matchup.service.MatchupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/matchup/board")
@Slf4j
@RequiredArgsConstructor
public class MatchupBoardController {

    private final MatchupService matchupService;
    private final MatchupBoardService matchupBoardService;
    private final SimpMessageSendingOperations messageTemplate;

    // 게시글 작성하기

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/register")
    public ModelAndView showMatchupBoardRegisterPage (ModelAndView mv){
        mv.setViewName("matchup/matchup-board-register");
        mv.addObject("reqMatchupBoardDto",new ReqMatchupBoardDto());
        return mv;
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/register")
    public String registerMatchupBoard(@Valid @ModelAttribute ReqMatchupBoardDto reqMatchupBoardDto, @AuthenticationPrincipal CustomUser user){
        matchupBoardService.registerMatchupBoard(reqMatchupBoardDto, user);
        log.info("Matchup 게시글 등록 완료");
        return "redirect:/matchup/board";
    }

    // 게시글 상세 조회
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/detail")
    public ModelAndView getMatchupBoardDetail(@RequestParam("matchup-board-id") Long boardId, ModelAndView mv, @AuthenticationPrincipal CustomUser user){
        log.info("matchup-board-id: {}",boardId);
        ResMatchupBoardDto resMatchupBoardDto = matchupBoardService.findMatchupBoardByBoardId(boardId, user);
        mv.addObject("resMatchupBoardDto",resMatchupBoardDto);
        mv.setViewName("matchup/matchup-board-detail");
        return mv;
    }

    // 게시글 전체 목록 조회
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping
    public ModelAndView showMatchupBoardPage(ModelAndView mv){
        mv.setViewName("matchup/matchup-board-list");
        return mv;
    }


    /*
    * 전체 Matchup 게시글 목록을 가져오는 메서드
    * */
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/list")
    @ResponseBody
    public ResponseEntity<ApiResponse<PageResponseDto<ResMatchupBoardListDto>>> listMatchupBoardByFilter(@RequestParam("page") int page, @RequestParam(value="size", required = false, defaultValue = "4") int size, @RequestParam("sportsType") String sportsType, @RequestParam("region") String region, @RequestParam("date") String date, @RequestParam("availableFilter") Boolean availableFilter, @AuthenticationPrincipal CustomUser user){
        PageRequest pageRequest = PageRequest.of(page,size);
        PageResponseDto<ResMatchupBoardListDto> pageResponseDto = matchupBoardService.findAllMatchupBoardsWithPaging(pageRequest, sportsType, region, date, availableFilter, user);
        return ResponseEntity.ok(ApiResponse.ok(pageResponseDto));
    }

    // 게시글 내가 작성한 글 목록 조회
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/my")
    public String showMyBoardPage(){
        return "matchup/matchup-board-my";
    }

    /*
     * 내가 작성한 Matchup 게시글 목록을 가져오는 메서드
     * */
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/my/list")
    @ResponseBody
    public ResponseEntity<ApiResponse<PageResponseDto<ResMatchupBoardListDto>>> listMyMatchupBoardByFilter(@RequestParam("page") int page, @RequestParam(value="size", required = false, defaultValue = "4") int size , @AuthenticationPrincipal CustomUser user, @RequestParam("sportsType") String sportsType, @RequestParam("date") String date, @RequestParam("availableFilter") Boolean availableFilter){
        PageRequest pageRequest = PageRequest.of(page,size);
        PageResponseDto<ResMatchupBoardListDto> pageResponseDto = matchupBoardService.findAllMyMatchupBoardsWithPaging(pageRequest, user,sportsType, date, availableFilter);
        return ResponseEntity.ok(ApiResponse.ok(pageResponseDto));
    }

    // 게시글 수정/삭제

    /*
    * Matchup 게시글 수정하기 페이지로 이동
    * */
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/edit")
    public ModelAndView showMatchupBoardEditPage(@RequestParam("boardId") Long boardId, ModelAndView mv, @AuthenticationPrincipal CustomUser user){
        ResMatchupBoardDto resMatchupBoardDto = matchupBoardService.findMatchupBoardByBoardId(boardId, user);
        mv.addObject("resMatchupBoardDto",resMatchupBoardDto);
        mv.setViewName("matchup/matchup-board-edit");
        return mv;
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/edit")
    public ModelAndView editMatchupBoard(@Valid @ModelAttribute ReqMatchupBoardEditDto reqMatchupBoardEditDto, ModelAndView mv, @AuthenticationPrincipal CustomUser user){
        matchupBoardService.updateBoard(reqMatchupBoardEditDto, user);
        //ResMatchupBoardDto updateResMatchupBoardDto = matchupBoardService.findMatchupBoardByBoardId(resMatchupBoardDto.getBoardId(), user);
        //mv.addObject("resMatchupBoardDto", updateResMatchupBoardDto);
//        mv.setViewName("matchup/matchup-board-detail");
        mv.setViewName("redirect:/matchup/board/detail?matchup-board-id="+reqMatchupBoardEditDto.getBoardId());
        return mv;
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/delete")
    public String softDeleteBoard(@RequestParam("boardId") Long boardId, @AuthenticationPrincipal CustomUser user){
        matchupBoardService.softDeleteMatchupBoard(boardId, user);
        log.info("matchup 게시글 삭제 완료");
        return "redirect:/matchup/board/my";
    }

}

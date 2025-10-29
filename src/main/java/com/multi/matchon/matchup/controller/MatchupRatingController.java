package com.multi.matchon.matchup.controller;

import com.multi.matchon.common.auth.dto.CustomUser;
import com.multi.matchon.common.dto.res.ApiResponse;
import com.multi.matchon.common.dto.res.PageResponseDto;
import com.multi.matchon.matchup.dto.req.ReqMatchupRatingDto;
import com.multi.matchon.matchup.dto.res.ResMatchupMyGameListDto;
import com.multi.matchon.matchup.dto.res.ResMatchupRatingDto;
import com.multi.matchon.matchup.dto.res.ResMatchupRatingListDto;
import com.multi.matchon.matchup.service.MatchupRatingService;
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
@RequestMapping("/matchup")
@Slf4j
@RequiredArgsConstructor
public class MatchupRatingController {

    private final MatchupRatingService matchupRatingService;


    // 등록

    /*
    * 내가 작성한 Matchup Board에 대한 매너 온도 평가 세팅
    * */
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/rating/setting")
    @ResponseBody
    public ResponseEntity<ApiResponse<?>> setMannerTemperatureSetting(@RequestParam("boardId") Long boardId, @AuthenticationPrincipal CustomUser user){
            matchupRatingService.setMannerTemperatureSettingByBoardId(boardId, user);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }


//    /*
//    * 매너 온도 평가 하기
//    * 평가 하는 사람이 실제 평가자가 맞는지 체크
//    * 실제 평가가 진행되지 않았는지 체크
//    * */
//    @GetMapping("/rating/register")
//    public ModelAndView showRatingRegisterPage(@RequestParam("boardId") Long boardId, @RequestParam("evalId") Long evalId, @RequestParam("targetId") Long targetId, @AuthenticationPrincipal CustomUser user, ModelAndView mv){
//        ResMatchupRatingDto resMatchupRatingDto = matchupRatingService.findResMatchupRatingDto(boardId, evalId, targetId, user);
//
//        mv.setViewName("matchup/matchup-rating-register");
//        mv.addObject("resMatchupRatingDto", resMatchupRatingDto);
//        return mv;
//    }

    /*
    * 평가자가 입력한 form을 받는 메서드
    * DB에 저장후 /matchup/rating/page로 redirect, boardId 필요
    * */
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/rating/register")
    public String registerRating(@Valid @ModelAttribute ReqMatchupRatingDto reqMatchupRatingDto, @AuthenticationPrincipal CustomUser user){
        matchupRatingService.registerMatchupRating(reqMatchupRatingDto,user);
        return "redirect:/matchup/rating/page?boardId="+reqMatchupRatingDto.getBoardId();
    }


    // 조회

    /*
     * 내가 참여한 게임 목록 보여주는 페이지 이동
     * */
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/mygame/page")
    public String showMatchupMyGameListPage(){
        return "matchup/matchup-mygame-list";
    }


    /*
    * 내가 참여한 게임 목록 반환
    * */
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/mygame/list")
    @ResponseBody
    public ResponseEntity<ApiResponse<PageResponseDto<ResMatchupMyGameListDto>>> findMyMatupMyGames(@RequestParam("page") int page, @RequestParam(value="size", required = false, defaultValue = "4") int size, @AuthenticationPrincipal CustomUser user){

        PageRequest pageRequest = PageRequest.of(page,size);
        PageResponseDto<ResMatchupMyGameListDto> pageResponseDto = matchupRatingService.findAllMyGames(pageRequest, user);
        return ResponseEntity.ok((ApiResponse.ok(pageResponseDto)));
    }


    /*
    * 내가 참여한 게임에 대한 매너온도 평가 페이지 이동
    * */
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/rating/page")
    public ModelAndView showRatingPage(@RequestParam("boardId") Long boardId, ModelAndView mv){
        mv.addObject("boardId", boardId);
        mv.setViewName("matchup/matchup-mygame-rating");
        return mv;
    }

    /*
    * 내가 참여한 게임에 대한 매너 온도 평가 목록 조회
    * */
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/rating/list")
    @ResponseBody
    public ResponseEntity<ApiResponse<PageResponseDto<ResMatchupRatingListDto>>> findMyMatupRatings(@RequestParam("page") int page, @RequestParam(value="size", required = false, defaultValue = "4") int size, @RequestParam("boardId") Long boardId, @AuthenticationPrincipal CustomUser user){

        PageRequest pageRequest = PageRequest.of(page,size);
        PageResponseDto<ResMatchupRatingListDto> pageResponseDto = matchupRatingService.findAllMyRatings(pageRequest, user, boardId);
        return ResponseEntity.ok((ApiResponse.ok(pageResponseDto)));
    }
//
//    /*
//    * 평가한 매너온도 보여주는 사이트
//    * */
//    @GetMapping("/rating/detail")
//    public ModelAndView showRatingDetailPage(@RequestParam("boardId") Long boardId, @RequestParam("evalId") Long evalId, @RequestParam("targetId") Long targetId, ModelAndView mv){
//        ResMatchupRatingDto resMatchupRatingDto = matchupRatingService.findDetailResMatchupRatingDto(boardId, evalId, targetId);
//
//        mv.addObject("resMatchupRatingDto",resMatchupRatingDto);
//        mv.setViewName("matchup/matchup-rating-detail");
//        return mv;
//
//    }


    // 수정

    // 삭제
}

package com.multi.matchon.team.controller;

import com.multi.matchon.common.auth.dto.CustomUser;
import com.multi.matchon.common.dto.res.ApiResponse;
import com.multi.matchon.common.dto.res.PageResponseDto;
import com.multi.matchon.member.domain.Member;
import com.multi.matchon.member.repository.MemberRepository;
import com.multi.matchon.team.dto.req.ReqReviewDto;
import com.multi.matchon.team.dto.req.ReqTeamDto;
import com.multi.matchon.team.dto.req.ReqTeamJoinDto;
import com.multi.matchon.team.dto.res.ResJoinRequestDetailDto;
import com.multi.matchon.team.dto.res.ResJoinRequestDto;
import com.multi.matchon.team.dto.res.ResReviewDto;
import com.multi.matchon.team.dto.res.ResTeamDto;
import com.multi.matchon.team.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/team")
@Slf4j
@RequiredArgsConstructor
public class TeamController {

    private final MemberRepository memberRepository;

    private final TeamService teamService;

//    @GetMapping
//    public String memberAllList(){
//        //matchupService.findAll();
//        return "team/team-list";
//    }

    @GetMapping("/team/register")
    public ModelAndView teamRegister(ModelAndView mv){
        mv.setViewName("team/team-register");
        mv.addObject("formActionUrl", "/team/register");
        mv.addObject("ReqTeamDto",new ReqTeamDto());
        return mv;
    }
    @PostMapping("/team/register")
    public String teamRegister(@Valid @ModelAttribute ReqTeamDto reqTeamDto, BindingResult result,
                               @AuthenticationPrincipal CustomUser user){

        if (result.hasErrors()) {
            return "team/team-register";
        }

        teamService.teamRegister(reqTeamDto, user);

        log.info("team 등록 완료");
        return "redirect:/team";
    }
    @GetMapping
    public ModelAndView showTeamListPage(ModelAndView mv){
        List<ResTeamDto> teams = teamService.findAllWithoutPaging();
        //PageRequest pageRequest = PageRequest.of(0,4);
        //PageResponseDto<ResMatchupBoardListDto> pageResponseDto = matchupService.findAllWithPaging(pageRequest);
        mv.setViewName("team/team-list");
        mv.addObject("teams", teams);
        //mv.addObject("pageResponseDto",pageResponseDto);
        mv.addObject("myTeamView", false); // ⬅️ explicitly hide 목록으로
        return mv;
    }

    @GetMapping("/team/list")
    @ResponseBody
    public ResponseEntity<ApiResponse<PageResponseDto<ResTeamDto>>> findAllWithPaging(
            @RequestParam("page") int page,
            @RequestParam(value="size", required = false, defaultValue = "5") int size,
            @RequestParam(value = "recruitingPosition", required = false) String recruitingPosition,
            @RequestParam(value = "region", required = false) String region,
            @RequestParam(value = "teamRatingAverage", required = false) Double teamRatingAverage,
            @RequestParam(value = "recruitmentStatus", required = false) Boolean recruitmentStatus  // ✅ NEW
    ){

        log.info("⭐ Rating Filter Received: {}", teamRatingAverage);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        PageResponseDto<ResTeamDto> pageResponseDto = teamService.findAllWithPaging(pageRequest, recruitingPosition, region, teamRatingAverage, recruitmentStatus);
        return ResponseEntity.ok(ApiResponse.ok(pageResponseDto));
    }

    @GetMapping("/team/{teamId}")
    public ModelAndView viewTeamDetail(@PathVariable Long teamId, @AuthenticationPrincipal CustomUser user) {
        ModelAndView mv = new ModelAndView("team/team-detail");

        ResTeamDto teamDto = teamService.findTeamById(teamId);
        mv.addObject("team", teamDto);

        // Add team leader status flag
        boolean isLeader = teamService.isTeamLeader(teamId, user.getUsername()); // you'll add this method
        mv.addObject("isTeamLeader", isLeader);

        // NEW: Resolve leaderId from createdBy
        Member leader = memberRepository.findByMemberEmail(teamDto.getCreatedBy())
                .orElseThrow(() -> new IllegalArgumentException("팀장 정보를 찾을 수 없습니다."));

        teamDto.setLeaderId(leader.getId()); // ✅ inject leaderId into the DTO
        mv.addObject("teamLeaderId", leader.getId());


        // 4. Determine if user is in any team
        boolean userHasTeam = user.getMember().getTeam() != null;

        // 5. Check if user is in this team (even if not a leader)
        boolean isMemberOfThisTeam = userHasTeam && user.getMember().getTeam().getId().equals(teamId);


        // NEW: Calculate user role
        String userRole;
        if (isLeader) {
            userRole = "LEADER";
        } else if (user.getMember().getTeam() != null && user.getMember().getTeam().getId().equals(teamId)) {
            userRole = "MEMBER";
        } else {
            userRole = "GUEST";
        }


        mv.addObject("userRole", userRole);
        mv.addObject("hasTeam", userHasTeam);
        mv.addObject("isLeaderOfCurrentTeam", isLeader); // 👈 for tab disabling logic
        mv.addObject("userHasTeam", userHasTeam);         // 👈 for tab disabling logic



        return mv;
    }
    @PostMapping("/join/{teamId}")
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> joinTeam(
            @PathVariable Long teamId,
            @RequestBody ReqTeamJoinDto joinRequest,
            @AuthenticationPrincipal CustomUser user) {
        
        try {
            teamService.processTeamJoinRequest(teamId, joinRequest, user);
            return ResponseEntity.ok(ApiResponse.ok(null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(e.getMessage()));
        }


    }
    @PostMapping("/team/{teamId}/review")
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> submitReview(
            @PathVariable Long teamId,
            @RequestBody ReqReviewDto reviewDto,
            @AuthenticationPrincipal CustomUser user) {

        teamService.saveReview(teamId, user, reviewDto);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @GetMapping("/team/{teamId}/reviews")
    @ResponseBody
    public ResponseEntity<ApiResponse<List<ResReviewDto>>> getReviews(@PathVariable Long teamId) {
        return ResponseEntity.ok(ApiResponse.ok(teamService.getReviewsForTeam(teamId)));
    }

    @PutMapping("/team/review/{reviewId}")
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> updateReview(
            @PathVariable Long reviewId,
            @RequestBody @Valid ReqReviewDto reviewDto,
            @AuthenticationPrincipal CustomUser user) {
        
        teamService.updateReview(reviewId, user, reviewDto);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @DeleteMapping("/team/review/{reviewId}")
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal CustomUser user) {
        
        teamService.deleteReview(reviewId, user);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @GetMapping("/team/{teamId}/my-reviews")
    @ResponseBody
    public ResponseEntity<ApiResponse<List<ResReviewDto>>> getMyReviews(
            @PathVariable Long teamId,
            @AuthenticationPrincipal CustomUser user) {

        log.info("🔍 [Controller] user principal: {}", user);

        List<ResReviewDto> myReviews = teamService.getMyReviewsForTeam(teamId, user.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(myReviews));
    }

    @PostMapping("/team/{teamId}/join-request")
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> sendJoinRequest(@PathVariable Long teamId,
                                                             @RequestBody ReqTeamJoinDto joinDto, @AuthenticationPrincipal CustomUser user) {

        teamService.sendJoinRequest(teamId, user, joinDto);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @GetMapping("/team/{teamId}/join-requests")
    @ResponseBody
    public ResponseEntity<ApiResponse<PageResponseDto<ResJoinRequestDto>>> getPendingJoinRequests(
            @PathVariable Long teamId, @AuthenticationPrincipal CustomUser user,  @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        PageResponseDto<ResJoinRequestDto> pagedRequests = teamService.getJoinRequestsForTeam(teamId, user, pageRequest);
        return ResponseEntity.ok(ApiResponse.ok(pagedRequests));
    }

    @PostMapping("/team/join-request/{requestId}/approve")
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> approveJoinRequest(@PathVariable Long requestId) {
        try {
            teamService.approveJoinRequest(requestId);
            return ResponseEntity.ok(ApiResponse.ok(null));
        } catch (IllegalStateException e) {
            // This is where the alert-worthy message like "이미 팀이 있는 사용자 입니다" is sent back
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            // Generic fallback error
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail("요청 처리 중 문제가 발생했습니다."));
        }
    }

    @PostMapping("/team/join-request/{requestId}/reject")
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> rejectJoinRequest(@PathVariable Long requestId) {
        teamService.rejectJoinRequest(requestId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @GetMapping("/team/edit/{teamId}")
    public ModelAndView editTeamPage(@PathVariable Long teamId, @AuthenticationPrincipal CustomUser user) {
        ReqTeamDto dto = teamService.getTeamEditForm(teamId, user); // include validation to ensure leader
        ModelAndView mv = new ModelAndView("team/team-edit");
        mv.addObject("ReqTeamDto", dto);
        return mv;
    }

    @DeleteMapping("/team/{teamId}")
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> deleteTeam(@PathVariable Long teamId, @AuthenticationPrincipal CustomUser user) {
        teamService.deleteTeam(teamId, user);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PostMapping("/team/update")
    public String updateTeam(@ModelAttribute ReqTeamDto reqTeamDto,
                             @AuthenticationPrincipal CustomUser user) {


        teamService.updateTeam(reqTeamDto, user);


        return "redirect:/team"; // or wherever you want to redirect after update

    }

    @PostMapping("/team/review/{reviewId}/response")

    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> writeResponseToReview(
            @PathVariable Long reviewId,
            @RequestBody Map<String, String> payload,
            @AuthenticationPrincipal CustomUser user) {

        String reviewResponse = payload.get("reviewResponse");
        teamService.writeReviewResponse(reviewId, reviewResponse, user);
        return ResponseEntity.ok(ApiResponse.ok(null));

    }

    @GetMapping("/team/{teamId}/all-reviews")
    @ResponseBody
    public ResponseEntity<ApiResponse<List<ResReviewDto>>> getAllReviewsWithResponses(
            @PathVariable Long teamId,
            @AuthenticationPrincipal CustomUser user) {

        List<ResReviewDto> allReviews = teamService.getAllReviewsWithResponses(teamId, user);
        return ResponseEntity.ok(ApiResponse.ok(allReviews));
    }

    @GetMapping("/team/{teamId}/answered-reviews")
    @ResponseBody
    public ResponseEntity<ApiResponse<List<ResReviewDto>>> getAnsweredReviews(
            @PathVariable Long teamId,
            @AuthenticationPrincipal CustomUser user) {

        List<ResReviewDto> answeredReviews = teamService.getAnsweredReviews(teamId, user);
        return ResponseEntity.ok(ApiResponse.ok(answeredReviews));

    }


    @PutMapping("/team/review/response/{responseId}")
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> updateResponse(
            @PathVariable Long responseId,
            @RequestBody Map<String, String> payload,
            @AuthenticationPrincipal CustomUser user) {

        String updatedText = payload.get("updatedText");
        teamService.updateReviewResponse(responseId, updatedText, user);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }


    @DeleteMapping("/team/review/response/{responseId}")
    public ResponseEntity<ApiResponse<?>> deleteResponse(@PathVariable Long responseId, @AuthenticationPrincipal CustomUser user) {
        teamService.deleteResponse(responseId, user);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }



    @GetMapping("/team/{teamId}/join-request/{requestId}")

    public ModelAndView viewJoinRequestDetail(@PathVariable Long requestId,
                                              @AuthenticationPrincipal CustomUser user) {
        ModelAndView mv = new ModelAndView("team/join-request-detail");

        ResJoinRequestDetailDto joinRequestDto = teamService.getJoinRequestDetail(requestId, user);
        mv.addObject("joinRequest", joinRequestDto);

        return mv;
    }


    @GetMapping("/team/my-team-info")
    @ResponseBody
    public ResponseEntity<ApiResponse<ResTeamDto>> getMyTeamInfo(@AuthenticationPrincipal CustomUser user) {
        ResTeamDto myTeam = teamService.findMyTeam(user);
        if (myTeam == null) {
            return ResponseEntity.ok(ApiResponse.ok(null));
        }
        return ResponseEntity.ok(ApiResponse.ok(myTeam));
    }

    @GetMapping("/team/my")
    public ModelAndView viewMyTeamList(@AuthenticationPrincipal CustomUser user) {
        ModelAndView mv = new ModelAndView("team/team-list");
        List<ResTeamDto> teams = new ArrayList<>();

        try {
            ResTeamDto myTeam = teamService.findMyTeam(user);
            if (myTeam != null) {
                teams.add(myTeam);
                mv.addObject("teams", teams);
                mv.addObject("myTeamView", true);
            } else {
                mv.addObject("teams", teams);
                mv.addObject("myTeamView", true);
            }
        } catch (Exception e) {
            mv.addObject("teams", teams);
            mv.addObject("myTeamView", true);
        }

        return mv;
    }
    @GetMapping("/my")
    public ModelAndView myTeamView(@AuthenticationPrincipal CustomUser user) {
        ResTeamDto myTeam = teamService.findMyTeam(user);

        ModelAndView mv = new ModelAndView("team/team-list"); // reuse team-list.html
        mv.addObject("teams", myTeam == null ? List.of() : List.of(myTeam));
        mv.addObject("myTeamView", true); // this triggers the "전체 목록으로 돌아가기" button
        return mv;
    }
    @GetMapping("/team/{teamId}/reviews/paged")
    @ResponseBody
    public ResponseEntity<ApiResponse<PageResponseDto<ResReviewDto>>> getReviewsPaged(
            @PathVariable Long teamId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageRequest pageRequest = PageRequest.of(page, size);
        PageResponseDto<ResReviewDto> response = teamService.getPagedReviews(teamId, pageRequest);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
    @GetMapping("/team/{teamId}/join-request/count")
    @ResponseBody
    public ResponseEntity<ApiResponse<Integer>> getPendingRequestCount(@PathVariable Long teamId) {
        int count = teamService.countPendingJoinRequests(teamId);
        return ResponseEntity.ok(ApiResponse.ok(count));
    }


}



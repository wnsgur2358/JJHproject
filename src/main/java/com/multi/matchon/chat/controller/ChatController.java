package com.multi.matchon.chat.controller;

import com.multi.matchon.chat.dto.res.ResChatDto;
import com.multi.matchon.chat.dto.res.ResGroupChatParticipantListDto;
import com.multi.matchon.chat.dto.res.ResMyChatListDto;
import com.multi.matchon.chat.service.ChatService;
import com.multi.matchon.common.auth.dto.CustomUser;
import com.multi.matchon.common.dto.res.ApiResponse;
import com.multi.matchon.common.exception.custom.CustomException;
import com.multi.matchon.team.domain.TeamMember;
import com.multi.matchon.team.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/chat")
public class ChatController {

    private final TeamMemberRepository teamMemberRepository;

    private final ChatService chatService;

    // 등록
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/my/room")
    public ModelAndView showPrivateChatPageByRoomId(@RequestParam("roomId") Long roomId, ModelAndView mv){
        mv.setViewName("chat/private-chat");
        mv.addObject("roomId",roomId);
        return mv;
    }

    // private-chat page로 이동
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/private")
    public ModelAndView showPrivateChatPageByReceiverId(@RequestParam("receiverId") Long receiverId, ModelAndView mv){
        mv.setViewName("chat/private-chat");
        mv.addObject("receiverId",receiverId);
        return mv;
    }

    // private-chat room 생성 또는 조회
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @ResponseBody
    @GetMapping("/room/private")
    public ResponseEntity<ApiResponse<Long>> getPrivateChatRoom(@RequestParam Long receiverId, @AuthenticationPrincipal CustomUser user){

        Long roomId = chatService.findPrivateChatRoom(receiverId, user.getMember().getId());
        return ResponseEntity.ok().body(ApiResponse.ok(roomId));
    }

    // 조회

    /*
    * private chat room 페이지 이동
    * */
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/my/rooms")
    public String showMyChatRooms(){
        return "chat/my-chat-list";
    }

    /*
    * 내가 속한 채팅방 목록 전달하는 메서드
    * */
    //팀 채팅 목록에서 팀 채팅 만 보이도록 수정
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/my/rooms")
    @ResponseBody
    public ResponseEntity<ApiResponse<List<ResMyChatListDto>>> getMyChatRooms(@AuthenticationPrincipal CustomUser user) {
        //List<ResMyChatListDto> chatRooms;

        List<ResMyChatListDto> resMyChatListDtos = chatService.findAllMyChatRoom(user);

        return ResponseEntity.ok().body(ApiResponse.ok(resMyChatListDtos));

//        TeamMember teamMember = teamMemberRepository.findByMemberId(user.getMember().getId())
//                .orElse(null);
//
//        boolean isLeader = teamMember != null && teamMember.getTeamLeaderStatus();
//        boolean hasTeam = user.getMember().getTeam() != null;
//
//        if (isLeader && hasTeam) {
//            Long leaderId = user.getMember().getId();
//            Long teamId = user.getMember().getTeam().getId();
//            chatRooms = chatService.findRelevantRoomsForLeader(leaderId, teamId); // ✅ filtered
//        } else {
//            chatRooms = chatService.findAllRoomsForUser(user.getMember().getId()); // fallback
//        }
//
//        return ResponseEntity.ok().body(ApiResponse.ok(chatRooms));
    }

    /*
    * 내가 속한 채팅방에서 대화 기록 전달하는 메서드
    * */
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/history{roomId}")
    @ResponseBody
    public ResponseEntity<ApiResponse<List<ResChatDto>>> getChatHistory(@RequestParam("roomId") Long roomId, @AuthenticationPrincipal CustomUser user){
        List<ResChatDto> resChatDtos = chatService.findAllChatHistory(roomId, user);
        return ResponseEntity.ok().body(ApiResponse.ok(resChatDtos));
    }


    /*
    * 로그인한 유저가 지정한 roomId에 채팅 참여자 인지 체크
    * */
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/check/my/rooms")
    @ResponseBody
    public ResponseEntity<ApiResponse<?>> checkRoomParticipant(@RequestParam("roomId") Long roomId, @AuthenticationPrincipal CustomUser user){

        chatService.checkRoomParticipant(user,roomId);

        return ResponseEntity.ok().build();
    }

    /*
    * 특정 group chat room 페이지 이동
    * */
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/group/room")
    public ModelAndView showGroupChatPageByRoomId(@RequestParam("roomId") Long roomId, ModelAndView mv){
        mv.setViewName("chat/group-chat");
        mv.addObject("roomId",roomId);
        return mv;
    }

    /*
    * 그룹 채팅방 참가자 불러오기
    * */
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/group/participants")
    @ResponseBody ResponseEntity<ApiResponse<List<ResGroupChatParticipantListDto>>> getGroupChatAllParticipant(@RequestParam("roomId") Long roomId, @AuthenticationPrincipal CustomUser user){
        List<ResGroupChatParticipantListDto> resGroupChatParticipantListDtos = chatService.findGroupChatAllParticipant(roomId, user);
        return ResponseEntity.ok(ApiResponse.ok(resGroupChatParticipantListDtos));
    }



    //수정
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/room/read")
    @ResponseBody
    public ResponseEntity<?> readAllMessage(@RequestParam("roomId") Long roomId, @AuthenticationPrincipal CustomUser user){
        chatService.readAllMessage(roomId, user);
        return ResponseEntity.ok().build();
    }


    // 1대1 채팅에서 상대방을 차단
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/room/private/block")
    public String blockUser(@RequestParam("roomId") Long roomId, @AuthenticationPrincipal CustomUser user){
        chatService.blockUser(roomId, user);
        return "redirect:/chat/my/rooms";
    }

    //api용, 1대1 채팅에서 상대방을 차단
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/room/private/api/block")
    @ResponseBody
    public ResponseEntity<?> blockUserWithApi(@RequestParam("roomId") Long roomId, @AuthenticationPrincipal CustomUser user) {
        chatService.blockUserWithApi(roomId, user);
        return ResponseEntity.ok().build();
    }

    // 1대1 채팅에서 상대방을 차단해제
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/room/private/unblock")
    public String unblockUser(@RequestParam("roomId") Long roomId, @AuthenticationPrincipal CustomUser user){
        chatService.unblockUser(roomId, user);
        return "redirect:/chat/my/rooms";
    }

    //api용, 1대1 채팅에서 상대방을 차단해제
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/room/private/api/unblock")
    @ResponseBody
    public ResponseEntity<?> unblockUserWithApi(@RequestParam("roomId") Long roomId, @AuthenticationPrincipal CustomUser user) {
        chatService.unblockUserWithApi(roomId, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/group/room-id")
    @ResponseBody
    public ResponseEntity<ApiResponse<Long>> getTeamChatRoomId(@RequestParam Long teamId) {
        Long roomId = chatService.findTeamChatRoomByTeamId(teamId);
        return ResponseEntity.ok(ApiResponse.ok(roomId));
    }
    // 삭제


    @GetMapping("/my/rooms/team")
    @ResponseBody
    public ResponseEntity<ApiResponse<List<ResMyChatListDto>>> getMyTeamChatRooms(@AuthenticationPrincipal CustomUser user) {
        Long memberId = user.getMember().getId();
        Long teamId = user.getMember().getTeam() != null ? user.getMember().getTeam().getId() : null;

        List<ResMyChatListDto> chatRooms = chatService.findAllTeamRelatedChats(memberId, teamId);

        return ResponseEntity.ok().body(ApiResponse.ok(chatRooms));
    }

    @GetMapping("/my/rooms/team/view")
    public String viewMyTeamChatRooms(Model model, @AuthenticationPrincipal CustomUser user) {
        Long memberId = user.getMember().getId();
        Long teamId = user.getMember().getTeam() != null ? user.getMember().getTeam().getId() : null;

        List<ResMyChatListDto> teamRooms = chatService.findOnlyTeamChatRooms(memberId, teamId);
        model.addAttribute("teamRooms", teamRooms);

        return "chat/team-chat-rooms"; // ✅ Create this Thymeleaf template
    }

    @PostMapping("/my/rooms/private")
    @ResponseBody
    public ResponseEntity<ApiResponse<List<ResMyChatListDto>>> getPrivateChats(@AuthenticationPrincipal CustomUser user) {
        Long memberId = user.getMember().getId();
        List<ResMyChatListDto> privateRooms = chatService.findOnlyPrivateChats(memberId); // you implement this
        return ResponseEntity.ok(ApiResponse.ok(privateRooms));
    }

    // 👉 차단 (Team Chat List 용)
    @GetMapping("/room/private/team/block")
    public String blockUserFromTeamChatList(@RequestParam("roomId") Long roomId,
                                            @AuthenticationPrincipal CustomUser user) {
        chatService.blockUser(roomId, user);
        return "redirect:/chat/my/rooms/team/view"; // 🟦 redirect to team chat list page
    }

    // 👉 차단 해제 (Team Chat List 용)
    @GetMapping("/room/private/team/unblock")
    public String unblockUserFromTeamChatList(@RequestParam("roomId") Long roomId,
                                              @AuthenticationPrincipal CustomUser user) {
        chatService.unblockUser(roomId, user);
        return "redirect:/chat/my/rooms/team/view"; // 🟦 same as above
    }
}

package com.multi.matchon.chat.service;

import com.multi.matchon.chat.domain.*;
import com.multi.matchon.chat.dto.res.ResChatDto;
import com.multi.matchon.chat.dto.res.ResGroupChatParticipantListDto;
import com.multi.matchon.chat.dto.res.ResMyChatListDto;
import com.multi.matchon.chat.event.NotificationEvent;
import com.multi.matchon.chat.exception.custom.ChatBlockException;

import com.multi.matchon.chat.exception.custom.NotChatParticipantException;

import com.multi.matchon.chat.repository.*;
import com.multi.matchon.common.auth.dto.CustomUser;

import com.multi.matchon.common.exception.custom.ApiCustomException;
import com.multi.matchon.common.exception.custom.CustomException;


import com.multi.matchon.matchup.domain.MatchupBoard;
import com.multi.matchon.matchup.repository.MatchupBoardRepository;
import com.multi.matchon.member.domain.Member;
import com.multi.matchon.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.context.ApplicationEventPublisher;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j

public class ChatService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MessageReadLogRepository messageReadLogRepository;
    private final ChatUserBlockRepository chatUserBlockRepository;
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher evetPublisher;
    private final MatchupBoardRepository matchupBoardRepository;


    // 등록More actions
    @Transactional
    public Long findPrivateChatRoom(Long receiverId, Long senderId) {
        // 차단 검사


        Member receiver = memberRepository.findByIdAndIsDeletedFalse(receiverId).orElseThrow(() -> new ApiCustomException("Chat 해당 회원 번호를 가진 회원은 존재하지 않습니다."));


        Member sender = memberRepository.findByIdAndIsDeletedFalse(senderId).orElseThrow(() -> new ApiCustomException("Chat 해당 회원 번호를 가진 회원은 존재하지 않습니다."));

        // 서로서로 차단했는지 확인

        //Boolean isBlock = chatUserBlockRepository.isBlockByReceiver(receiver);

        // 여기까지 왔다는 것은 receiverId와 senderId가 유효
        Optional<ChatRoom> chatRoom = chatParticipantRepository.findPrivateChatRoomByReceiverIdAndSenderId(receiverId, senderId);
        if (chatRoom.isPresent()) {
            return chatRoom.get().getId();
        }

        String chatName = "[ " + receiver.getMemberName() + " & " + sender.getMemberName() + " ] 1:1 채팅";

        String identifierChatRoomName = "(" + UUID.randomUUID().toString().replace("-", "").substring(0, 8) + ")";

        ChatRoom newChatRoom = ChatRoom.builder()
                .isGroupChat(false)
                .chatRoomName(chatName + identifierChatRoomName)
                .build();

        chatRoomRepository.save(newChatRoom);

        addParticipantToRoom(newChatRoom, receiver);
        addParticipantToRoom(newChatRoom, sender);

//        //1대1 채팅 상대방에게 메시지 보내기
//        sendNotification(receiver, "[1대1 채팅] "+sender.getMemberName()+"님이 1대1 채팅을 걸었습니다. 지금 바로 확인해보세요!. ", "/chat/my/room?"+"roomId="+newChatRoom.getId());

        evetPublisher.publishEvent(new NotificationEvent(this, receiver, "[1대1 채팅] " + sender.getMemberName() + "님이 1대1 채팅을 걸었습니다. 지금 바로 확인해보세요!.", "/chat/my/room?" + "roomId=" + newChatRoom.getId()));

        return newChatRoom.getId();
    }

    /*
     * 생성된 채팅방에 참여자를 추가할 때 사용
     * */
    @Transactional
    public void addParticipantToRoom(ChatRoom chatRoom, Member member) {

        ChatParticipant chatParticipant = ChatParticipant.builder()
                .member(member)
                .build();

        chatParticipant.changeChatRoom(chatRoom);

        chatParticipantRepository.save(chatParticipant);
    }

    @Transactional
    public void saveMessage(Long roomId, ResChatDto resChatDto) {
        ChatRoom chatRoom = chatRoomRepository.findByIdAndIsDeletedFalse(roomId).orElseThrow(() -> new CustomException("Chat 해당 채팅방 번호를 가진 채팅방은 존재하지 않습니다."));

        Member sender = memberRepository.findByMemberEmailAndIsDeletedFalse(resChatDto.getSenderEmail()).orElseThrow(() -> new CustomException("Chat 해당 회원 번호를 가진 회원은 존재하지 않습니다."));

        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .member(sender)
                .content(resChatDto.getContent())
                .build();

        chatMessageRepository.save(chatMessage);

        List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoomWithMember(chatRoom);

        for (ChatParticipant c : chatParticipants) {
            MessageReadLog messageReadLog = MessageReadLog.builder()
                    .chatRoom(chatRoom)
                    .member(c.getMember())
                    .chatMessage(chatMessage)
                    .isRead(c.getMember().equals(sender))
                    .build();

            messageReadLogRepository.save(messageReadLog);
        }
    }

    /*
     * Matchup board 작성할 때, group chat room을 생성하는 메서드
     * */
    @Transactional
    public ChatRoom registerGroupChatRoom(Member matchupWriter, String chatName) {


        ChatRoom newChatRoom = ChatRoom.builder()
                .isGroupChat(true)
                .chatRoomName(chatName)
                .build();

        chatRoomRepository.save(newChatRoom);

        addParticipantToRoom(newChatRoom, matchupWriter);

        return newChatRoom;

    }

    /*
     * Matchup board에 대응 되는 group chat room에 사용자를 추가
     * */
    @Transactional
    public void addParticipantToGroupChat(ChatRoom groupChatRoom, Member applicant) {
        addParticipantToRoom(groupChatRoom, applicant);
    }

    /*
     * Matchup board에 대응 되는 group chat room에 사용자를 제거
     * */
    @Transactional
    public void removeParticipantToGroupChat(ChatRoom groupChatRoom, Member applicant) {
        ChatParticipant removeChatParticipant = chatParticipantRepository.findByChatRoomAndMember(groupChatRoom, applicant).orElseThrow(() -> new CustomException("Matchup 해당 참여자는 그룹 채팅에 참여자가 아니에요."));

        removeChatParticipant.deleteParticipant(true);
    }


    // 조회

    @Transactional(readOnly = true)
    public List<ResMyChatListDto> findAllMyChatRoom(CustomUser user) { // 차단 검사

        List<ChatParticipant> chatParticipants = chatParticipantRepository.findAllByMemberIdAndIsDeletedFalse(user.getMember().getId());

//        List<ChatUserBlock> chatUserBlocks = chatUserBlockRepository.findAllByBlocker(user.getMember());
//        Set<Long> blockedIds = chatUserBlocks.stream()
//                .map(chatUserBlock -> chatUserBlock.getBlocked().getId())
//                .collect(Collectors.toSet());
        Set<Long> blockedIds = chatUserBlockRepository.findAllByBlocker(user.getMember()).stream()
                .map(chatUserBlock -> chatUserBlock.getBlocked().getId())
                .collect(Collectors.toSet());

        List<ResMyChatListDto> resMyChatListDtos = new ArrayList<>();

        for (ChatParticipant c : chatParticipants) {
            Long count = messageReadLogRepository.countByChatRoomAndMemberAndIsReadFalse(c.getChatRoom(), user.getMember());
            Boolean isBlock = false;
            if (!c.getChatRoom().getIsGroupChat()) {
                Member opponent = c.getChatRoom().getChatParticipants().stream()
                        .filter(chatParticipant -> !chatParticipant.getMember().getId().equals(user.getMember().getId()) && !chatParticipant.getIsDeleted())
                        .findFirst()
                        .map(ChatParticipant::getMember)
                        .orElse(null);
                if (opponent != null)
                    isBlock = blockedIds.contains(opponent.getId());
            }

            ResMyChatListDto resMyChatListDto = ResMyChatListDto.builder()
                    .roomId(c.getChatRoom().getId())
                    .roomName(c.getChatRoom().getChatRoomName())
                    .isGroupChat(c.getChatRoom().getIsGroupChat())
                    .isBlock(isBlock)
                    .unReadCount(count)
                    .build();

            resMyChatListDtos.add(resMyChatListDto);
        }

        return resMyChatListDtos;

    }

    @Transactional(readOnly = true)
    public List<ResChatDto> findAllChatHistory(Long roomId, CustomUser user) {
        ChatRoom chatRoom = chatRoomRepository.findByIdAndIsDeletedFalse(roomId).orElseThrow(() -> new ApiCustomException("Chat 해당 채팅방 번호를 가진 채팅방은 존재하지 않습니다."));

        Member sender = memberRepository.findByMemberEmailAndIsDeletedFalse(user.getMember().getMemberEmail()).orElseThrow(() -> new ApiCustomException("Chat 해당 회원은 존재하지 않습니다."));

        List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoomWithMember(chatRoom);

        Boolean check = false;
        LocalDateTime joinedDate = LocalDateTime.now();
        for (ChatParticipant c : chatParticipants) {
            if (c.getMember().equals(sender)) {
                check = true;
                joinedDate = c.getCreatedDate();
                break;
            }
        }

        if (!check)
            throw new ApiCustomException("Chat 본인이 속하지 않은 채팅방입니다.");

        List<ChatMessage> chatMessages = chatMessageRepository.findByChatRoomAndJoinedDateOrderByCreatedTimeAscWithMember(chatRoom, joinedDate);
        List<ResChatDto> resChatDtos = new ArrayList<>();

        for (ChatMessage c : chatMessages) {
            ResChatDto resChatDto = ResChatDto.builder()
                    .content(c.getContent())
                    .senderEmail(c.getMember().getMemberEmail())
                    .senderName(c.getMember().getMemberName())
                    .createdDate(c.getCreatedDate())
                    .build();

            resChatDtos.add(resChatDto);
        }
        return resChatDtos;
    }

    @Transactional(readOnly = true)
    public void checkRoomParticipant(CustomUser user, Long roomId) {

        ChatRoom chatRoom = chatRoomRepository.findByIdAndIsDeletedFalse(roomId).orElseThrow(() -> new ApiCustomException("Chat 해당 채팅방 번호를 가진 채팅방은 존재하지 않습니다."));

        Member sender = memberRepository.findByIdAndIsDeletedFalse(user.getMember().getId()).orElseThrow(() -> new ApiCustomException("Chat 해당 회원은 존재하지 않습니다."));

        if (!chatParticipantRepository.isRoomParticipantByChatRoomAndMember(chatRoom, sender)) {
            throw new NotChatParticipantException("Chat 해당 채팅방에 참여자가 아닙니다.");

        }
//        return false;
    }


    /*
     * 1대1 채팅방 == roomId에 대응되는 참여자들이 서로서로 차단했는지 체크
     * 한 사람이라도 차단했으면 예외 발생 → @MessageExceptionHandler에서 처리
     * 두 사람 모두 차단안한경우 예외 발생 안함 → 메시지 저장 → room을 subscribe한 유저들에게 메시지 전달
     * */
    @Transactional(readOnly = true)
    public void checkBlock(Long roomId) {
        List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoomIdWithMember(roomId);

        if (chatParticipants.isEmpty())
            throw new CustomException("Chat 해당 채팅방은 존재하지 않습니다.");


//        if(chatParticipants.size()!=2)
//            throw new CustomException("Chat 해당 채팅방은 1대1 채팅방이 아닙니다.");


        // 그룹 채팅이라면 더 이상 진행하지 않음
        if (chatRoomRepository.isGroupChat(roomId))
            return;

        // 한 사람이라도 차단했는지 체크
        if (chatUserBlockRepository.isBlockByTwoMember(chatParticipants.get(0).getMember(), chatParticipants.get(1).getMember())) {

            throw new ChatBlockException("ChatBlockException 발생");
        }
    }

    /*
     * 특정 그룹 채팅방 참가자들을 조회
     * */
    @Transactional(readOnly = true)
    public List<ResGroupChatParticipantListDto> findGroupChatAllParticipant(Long roomId, CustomUser user) {

        // 특정 그룹 채팅방에 참가자들 조회
        List<ChatParticipant> chatParticipants = chatParticipantRepository.findGroupChatAllParticipantByRoomId(roomId);

        //1대1 채팅 차단한 유저 목록 조회
        List<ChatUserBlock> chatUserBlocks = chatUserBlockRepository.findAllByBlocker(user.getMember());

        // 내가 참여하고 있는 1대1 채팅 참여자 목록 조회
        List<ChatParticipant> myPrivateChatParticipants = chatParticipantRepository.findAllPrivateChatParticipantByMember(user.getMember());

        // 전달할 Dto 생성
        List<ResGroupChatParticipantListDto> resGroupChatParticipantListDtos = new ArrayList<>();

        for (ChatParticipant chatParticipant : chatParticipants) {

            //내가 차단한 참여자 인지 체크
            Boolean isBlock = false;
            for (ChatUserBlock chatUserBlock : chatUserBlocks) {
                if (chatParticipant.getMember().getId().equals(chatUserBlock.getBlocked().getId())) {
                    isBlock = true;
                    break;
                }
            }

            //나와 연결된 1대1 채팅방이 있는지 체크
            Boolean isMyPrivateChatPartner = false;
            for (ChatParticipant myPrivateChatPartner : myPrivateChatParticipants) {
                if (chatParticipant.getMember().getId().equals(myPrivateChatPartner.getMember().getId())) {
                    isMyPrivateChatPartner = true;
                    ResGroupChatParticipantListDto resGroupChatParticipantListDto = ResGroupChatParticipantListDto.builder()
                            .memberId(chatParticipant.getMember().getId())
                            .memberName(chatParticipant.getMember().getMemberName())
                            .memberEmail(chatParticipant.getMember().getMemberEmail())
                            .isBlock(isBlock)
                            .isMyPrivateChatPartner(isMyPrivateChatPartner)
                            .privateRoomId(myPrivateChatPartner.getChatRoom().getId())
                            .build();
                    resGroupChatParticipantListDtos.add(resGroupChatParticipantListDto);
                    break;
                }
            }

            if (!isMyPrivateChatPartner) {
                ResGroupChatParticipantListDto resGroupChatParticipantListDto = ResGroupChatParticipantListDto.builder()
                        .memberId(chatParticipant.getMember().getId())
                        .memberName(chatParticipant.getMember().getMemberName())
                        .memberEmail(chatParticipant.getMember().getMemberEmail())
                        .isBlock(isBlock)
                        .isMyPrivateChatPartner(isMyPrivateChatPartner)
                        .privateRoomId(-10L)
                        .build();
                resGroupChatParticipantListDtos.add(resGroupChatParticipantListDto);
            }


        }
        return resGroupChatParticipantListDtos;

    }


    // 수정

    @Transactional
    public void readAllMessage(Long roomId, CustomUser user) {
        ChatRoom chatRoom = chatRoomRepository.findByIdAndIsDeletedFalse(roomId).orElseThrow(() -> new ApiCustomException("Chat 해당 채팅방 번호를 가진 채팅방은 존재하지 않습니다."));

        Member sender = memberRepository.findByMemberEmailAndIsDeletedFalse(user.getMember().getMemberEmail()).orElseThrow(() -> new ApiCustomException("Chat 해당 회원은 존재하지 않습니다."));

        int count = messageReadLogRepository.updateMessagesRead(chatRoom, sender);
        log.info("읽음 처리 메시지: {}", count);

    }


    /*
     * 1대1 채팅에서 상대 유저를 차단하는 메서드
     * */
    @Transactional
    public void blockUser(Long roomId, CustomUser user) {

        Member blocked = chatParticipantRepository.findByRoomIdAndMemberAndRoleMember(roomId, user.getMember()).stream().map(ChatParticipant::getMember).findFirst().orElseThrow(() -> new CustomException("Chat blockUser 차단할 대상이 없습니다."));

        // chatUserBlock에서 자신과 상대방이 있는지 조회
        Optional<ChatUserBlock> chatUserBlock = chatUserBlockRepository.findByBlockerAndBlocked(user.getMember(), blocked);

        if (chatUserBlock.isPresent()) {
            throw new CustomException("Chat blockUser 이미 차단한 유저입니다.");
        }

        // 상대방을 차단
        ChatUserBlock newChatUserBlock = ChatUserBlock.builder()
                .blocker(user.getMember())
                .blocked(blocked)
                .build();
        chatUserBlockRepository.save(newChatUserBlock);

        log.info("blockUser: {} → {}", user.getMember().getId(), blocked.getId());

    }

    /*
     * Api로 1대1 채팅에서 상대 유저를 차단하는 메서드
     * */
    @Transactional
    public void blockUserWithApi(Long roomId, CustomUser user) {

        Member blocked = chatParticipantRepository.findByRoomIdAndMemberAndRoleMember(roomId, user.getMember()).stream().map(ChatParticipant::getMember).findFirst().orElseThrow(() -> new ApiCustomException("Chat blockUser 차단할 대상이 없습니다."));

        // chatUserBlock에서 자신과 상대방이 있는지 조회
        Optional<ChatUserBlock> chatUserBlock = chatUserBlockRepository.findByBlockerAndBlocked(user.getMember(), blocked);

        if (chatUserBlock.isPresent()) {
            throw new ApiCustomException("Chat blockUser 이미 차단한 유저입니다.");
        }

        // 상대방을 차단
        ChatUserBlock newChatUserBlock = ChatUserBlock.builder()
                .blocker(user.getMember())
                .blocked(blocked)
                .build();
        chatUserBlockRepository.save(newChatUserBlock);

        log.info("blockUser: {} → {}", user.getMember().getId(), blocked.getId());
    }


    /*
     * 1대1 채팅에서 상대 유저를 차단해제 하는 메서드
     * */
    @Transactional
    public void unblockUser(Long roomId, CustomUser user) {

        Member unblocked = chatParticipantRepository.findByRoomIdAndMemberAndRoleMember(roomId, user.getMember()).stream().map(ChatParticipant::getMember).findFirst().orElseThrow(() -> new CustomException("blockUser 차단할 대상이 없습니다."));

        // chatUserBlock에서 자신과 상대방이 있는지 조회
        Optional<ChatUserBlock> chatUserBlock = chatUserBlockRepository.findByBlockerAndBlocked(user.getMember(), unblocked);

        if (chatUserBlock.isPresent()) {
            chatUserBlockRepository.delete(chatUserBlock.get());
        } else {
            throw new CustomException("blockUser 차단된 유저가 없습니다.");
        }

    }

    @Transactional
    public void unblockUserWithApi(Long roomId, CustomUser user) {

        Member unblocked = chatParticipantRepository.findByRoomIdAndMemberAndRoleMember(roomId, user.getMember()).stream().map(ChatParticipant::getMember).findFirst().orElseThrow(() -> new ApiCustomException("Chat blockUser 차단할 대상이 없습니다."));

        // chatUserBlock에서 자신과 상대방이 있는지 조회
        Optional<ChatUserBlock> chatUserBlock = chatUserBlockRepository.findByBlockerAndBlocked(user.getMember(), unblocked);

        if (chatUserBlock.isPresent()) {
            chatUserBlockRepository.delete(chatUserBlock.get());
        } else {
            throw new ApiCustomException("Chat blockUser 차단된 유저가 없습니다.");
        }
    }


    // 삭제


    @Transactional(readOnly = true)
    public Long findTeamChatRoomByTeamId(Long teamId) {

        return chatRoomRepository.findTeamGroupChatRoom(teamId)

                .orElseThrow(() -> new CustomException("팀 채팅방이 존재하지 않습니다."))
                .getId();
    }

//    public List<ResMyChatListDto> findRelevantRoomsForLeader(Long leaderId, Long teamId) {
//        // 1️⃣ Fetch private chats
//        List<ChatRoom> privateChats = chatParticipantRepository.findAllPrivateChatsForLeader(leaderId);
//
//        // 2️⃣ Fetch the team group chat room (if any)
//        Optional<ChatRoom> groupChatOpt = chatRoomRepository.findTeamGroupChatRoom(teamId);
//
//        // 3️⃣ Combine all relevant rooms
//        List<ChatRoom> allRelevantRooms = new ArrayList<>(privateChats);
//        groupChatOpt.ifPresent(allRelevantRooms::add); // ✅ safe add
//
//        // 4️⃣ Convert to DTOs
//        Member leader = memberRepository.findById(leaderId)
//                .orElseThrow(() -> new IllegalArgumentException("리더 정보를 찾을 수 없습니다."));
//
//        return allRelevantRooms.stream()
//                .map(room -> {
//                    boolean isBlocked = chatUserBlockRepository.isBlocked(room.getId(), leaderId);
//                    long unreadCount = messageReadLogRepository.countByChatRoomAndMemberAndIsReadFalse(room, leader);
//                    return ResMyChatListDto.from(room, leaderId, isBlocked, unreadCount);
//                })
//                .toList();
//    }

//    @Transactional(readOnly = true)
//    public List<ResMyChatListDto> findAllRoomsForUser(Long userId) {
//        Member member = memberRepository.findByIdAndIsDeletedFalse(userId)
//                .orElseThrow(() -> new CustomException("해당 회원이 존재하지 않습니다."));
//
//        List<ChatParticipant> chatParticipants = chatParticipantRepository.findAllByMemberIdAndIsDeletedFalse(userId);
//
//        Set<Long> blockedIds = chatUserBlockRepository.findAllByBlocker(member).stream()
//                .map(chatUserBlock -> chatUserBlock.getBlocked().getId())
//                .collect(Collectors.toSet());
//
//        List<ResMyChatListDto> resMyChatListDtos = new ArrayList<>();
//
//        for (ChatParticipant c : chatParticipants) {
//            Long count = messageReadLogRepository.countByChatRoomAndMemberAndIsReadFalse(c.getChatRoom(), member);
//            boolean isBlock = false;
//
//            if (!c.getChatRoom().getIsGroupChat()) {
//                Member opponent = c.getChatRoom().getChatParticipants().stream()
//                        .filter(p -> !p.getMember().getId().equals(member.getId()) && !p.getIsDeleted())
//                        .findFirst()
//                        .map(ChatParticipant::getMember)
//                        .orElse(null);
//                if (opponent != null) isBlock = blockedIds.contains(opponent.getId());
//            }
//
//            ResMyChatListDto dto = ResMyChatListDto.builder()
//                    .roomId(c.getChatRoom().getId())
//                    .roomName(c.getChatRoom().getChatRoomName())
//                    .isGroupChat(c.getChatRoom().getIsGroupChat())
//                    .isBlock(isBlock)
//                    .unReadCount(count)
//                    .build();
//
//            resMyChatListDtos.add(dto);
//        }
//
//        return resMyChatListDtos;
//    }

    public List<ResMyChatListDto> findOnlyTeamChatRooms(Long memberId, Long teamId) {
        if (teamId == null) return Collections.emptyList();

        Member member = memberRepository.findByIdAndIsDeletedFalse(memberId)
                .orElseThrow(() -> new CustomException("해당 회원이 존재하지 않습니다."));

        // Fetch group chat room for the team
        Optional<ChatRoom> teamGroupRoomOpt = chatRoomRepository.findTeamGroupChatRoom(teamId);

        // Collect to list (just one in this case)
        List<ChatRoom> relevantRooms = new ArrayList<>();
        teamGroupRoomOpt.ifPresent(relevantRooms::add);

        // ✅ Deduplicate by ChatRoom ID just in case
        Set<Long> seenRoomIds = new HashSet<>();

        return relevantRooms.stream()
                .filter(room -> seenRoomIds.add(room.getId())) // prevents duplicates
                .map(room -> {
                    boolean isBlocked = chatUserBlockRepository.isBlocked(room.getId(), memberId);
                    long unreadCount = messageReadLogRepository.countByChatRoomAndMemberAndIsReadFalse(room, member);
                    return ResMyChatListDto.from(room, memberId, isBlocked, unreadCount);
                })
                .toList();
    }

  

    public List<ResMyChatListDto> findOnlyPrivateChats(Long memberId) {
        List<ChatRoom> privateRooms = chatRoomRepository.findPrivateChatsByMemberId(memberId);

        return privateRooms.stream()
                .map(room -> {
                    Long unReadCount = messageReadLogRepository.countUnreadMessages(memberId, room.getId());
                    boolean isBlocked = chatUserBlockRepository.isBlocked(memberId, room.getId());

                    return ResMyChatListDto.from(room, memberId, isBlocked, unReadCount);
                })
                .collect(Collectors.toList());
    }


        @Transactional
        public Integer removeGroupChatsAfterThreeDaysOfMatch () {
            //현재 시간 보다 3일 전 시간 구하기
            LocalDateTime thresholdTime = LocalDateTime.now().minusDays(3);
            Set<ChatRoom> chatRoomsToDelete = new HashSet<>();

            List<ChatParticipant> chatParticipants = chatParticipantRepository.findAfterThreeDaysOfMatchWithChatParticipantAndChatRoom(thresholdTime);
            for (ChatParticipant chatParticipant : chatParticipants) {
                chatParticipant.deleteParticipant(true);
                chatRoomsToDelete.add(chatParticipant.getChatRoom());
            }
            for(ChatRoom chatRoom: chatRoomsToDelete){
                chatRoom.deleteChatRoom(true);
            }


            return chatParticipants.size();

        }
    @Transactional
    public List<ResMyChatListDto> findAllTeamRelatedChats(Long memberId, Long teamId) {
        if (teamId == null) return Collections.emptyList();

        Member currentUser = memberRepository.findByIdAndIsDeletedFalse(memberId)
                .orElseThrow(() -> new CustomException("해당 회원이 존재하지 않습니다."));

        // ✅ Step 1: Get all chat participations for the current user
        List<ChatParticipant> myParticipants = chatParticipantRepository.findAllByMemberIdAndIsDeletedFalse(memberId);

        // ✅ Step 2: Get all chat rooms
        Set<ChatRoom> privateWithTeam = myParticipants.stream()
                .map(ChatParticipant::getChatRoom)
                .filter(room -> !room.getIsGroupChat())
                .filter(room ->
                        room.getChatParticipants().stream()
                                .anyMatch(p ->
                                        p.getMember().getTeam() != null &&
                                                p.getMember().getTeam().getId().equals(teamId)
                                )
                )
                .collect(Collectors.toSet());

        // ✅ Step 3: Get team group chat room
        chatRoomRepository.findTeamGroupChatRoom(teamId).ifPresent(privateWithTeam::add);

        // ✅ Step 4: Convert to DTOs, deduplicated
        return privateWithTeam.stream()
                .sorted(Comparator.comparing(ChatRoom::getCreatedDate))
                .map(room -> {
                    boolean isBlocked = false;

                    if (!room.getIsGroupChat()) {
                        Optional<Member> opponent = room.getChatParticipants().stream()
                                .map(ChatParticipant::getMember)
                                .filter(member -> !member.getId().equals(memberId))
                                .findFirst();

                        if (opponent.isPresent()) {
                            isBlocked = chatUserBlockRepository
                                    .findByBlockerAndBlocked(currentUser, opponent.get())
                                    .isPresent();
                        }
                    }
                    long unreadCount = messageReadLogRepository.countByChatRoomAndMemberAndIsReadFalse(room, currentUser);
                    return ResMyChatListDto.from(room, memberId, isBlocked, unreadCount);
                })
                .toList();
    }

}



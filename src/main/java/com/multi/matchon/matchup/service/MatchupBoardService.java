package com.multi.matchon.matchup.service;

import com.multi.matchon.chat.domain.ChatRoom;
import com.multi.matchon.chat.service.ChatService;
import com.multi.matchon.common.auth.dto.CustomUser;
import com.multi.matchon.common.domain.Attachment;
import com.multi.matchon.common.domain.BoardType;
import com.multi.matchon.common.domain.SportsTypeName;
import com.multi.matchon.common.domain.Status;
import com.multi.matchon.common.dto.res.PageResponseDto;
import com.multi.matchon.common.exception.custom.CustomException;
import com.multi.matchon.common.repository.AttachmentRepository;
import com.multi.matchon.common.repository.SportsTypeRepository;
import com.multi.matchon.common.service.NotificationService;
import com.multi.matchon.matchup.domain.MatchupBoard;
import com.multi.matchon.matchup.domain.MatchupRequest;
import com.multi.matchon.matchup.dto.req.ReqMatchupBoardDto;
import com.multi.matchon.matchup.dto.req.ReqMatchupBoardEditDto;
import com.multi.matchon.matchup.dto.req.ReqMatchupRequestDto;
import com.multi.matchon.matchup.dto.res.ResMatchupBoardDto;
import com.multi.matchon.matchup.dto.res.ResMatchupBoardListDto;
import com.multi.matchon.matchup.dto.res.ResMatchupBoardOverviewDto;
import com.multi.matchon.matchup.repository.MatchupBoardRepository;
import com.multi.matchon.matchup.repository.MatchupRequestRepository;
import com.multi.matchon.member.domain.Member;
import com.sun.jdi.request.DuplicateRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchupBoardService {

    private final SportsTypeRepository sportsTypeRepository;

    private final MatchupBoardRepository matchupBoardRepository;
    private final MatchupService matchupService;
    private final AttachmentRepository attachmentRepository;
    private final ChatService chatService;
    private final MatchupRequestRepository matchupRequestRepository;
    private final NotificationService notificationService;


    // 등록


    /*
    * Matchup 게시글 작성한 내용을 서버에 저장하는 메서드
    * group chat 생성
    * */
    @Transactional
    public void registerMatchupBoard(ReqMatchupBoardDto reqMatchupBoardDto, CustomUser user) {


        // 1. 게시글을 24시간에 2번만 작성할 수 있도록 검사
        Long numberOfTodayMatchupBoards = matchupBoardRepository.countTodayMatchupBoards(user.getMember().getId(), LocalDateTime.now().minusHours(24));
        if(numberOfTodayMatchupBoards>=2){
            throw new CustomException("Matchup 게시글은 하루에 2번만 작성할 수 있습니다.");
        }

        // 2. 내가 등록하고자 하는 경기날짜가 기존에 내가 작성한 게시글에서 경기 시간과 겹치는 지 체크
        // 기존 경기 시간과 새로 등록하려는 경기 시간이 겹치는 지 체크, 3시 끝나고 다음 경기가 3시에 시작인 경우는 허용함
        LocalDateTime endTime = reqMatchupBoardDto.getMatchDatetime().plusHours(reqMatchupBoardDto.getMatchDuration()/60).plusMinutes(reqMatchupBoardDto.getMatchDuration()%60);


        Boolean isDuplicate = matchupBoardRepository.findByMemberAndStartTimeAndEndTime(user.getMember(),reqMatchupBoardDto.getMatchDatetime(),endTime);

        if(isDuplicate)
            throw new CustomException("Matchup 등록하신 경기 날짜는 이전에 작성하신 게시글의 경기 날짜와 겹칩니다. 확인 후 다시 작성해주세요.");

        // 3. 참가 요청 내역과 비교

        // 3-1. 내가 참가 요청한 것들 가져옴
        // 조건: 기존 경기 시간과 새로 등록하려는 경기 시간이 겹치는 것
        // 조건: Matchup 게시글이 삭제가 안된 것

        List<MatchupRequest> duplicatedMatchupRequests = matchupRequestRepository.findByMemberAndStartTimeAndEndTime(user.getMember(),reqMatchupBoardDto.getMatchDatetime(),endTime);

        // 3-2. 가져온 요청들 중에서 등록해도 되는 것과 등록하면 안되는 것을 구분
        // 등록하면 안되는 상태: 승인 대기, 승인됨, 승인 취소 요청, 취소 요청 반려
        // 등록 허용해도 되지만 추가적인 제한이 필요한 것: 요청 취소됨(재요청 불가능 하도록 제한), 반려됨(재요청 불가능 하도록 제한),
        // 등록 허용 가능한 상태: 취소 요청 승인

        for(MatchupRequest mr:duplicatedMatchupRequests){
            //승인 대기: 다른 곳에서 겹치는 시간대에 참가 요청한 것이 있기 때문에, MatchupBoard 게시글 등록을 허용하면 안됨
            if(
                (mr.getMatchupStatus()== Status.PENDING && mr.getMatchupRequestSubmittedCount()==1 && mr.getMatchupCancelSubmittedCount()==0 && !mr.getIsDeleted()) ||
                (mr.getMatchupStatus()== Status.PENDING && mr.getMatchupRequestSubmittedCount()==2 && mr.getMatchupCancelSubmittedCount()==0 && !mr.getIsDeleted())
            ){
                throw new CustomException("Matchup 등록하신 경기 날짜는 이전에 참가 요청하신 게시글의 경기 날짜와 겹칩니다. 확인 후 다시 작성해주세요");
            }
            // 요청 취소됨-1: 재요청이 가능하기 때문에, 재요청파트에서 제한을 걸어줘야됨
            // 요청 취소됨-2: 재요청이 불가능하기 때문에, 재요청파트에서 제한을 걸지 않아도됨
           /* else if(
                    (mr.getMatchupStatus()== Status.PENDING && mr.getMatchupRequestSubmittedCount()==1 && mr.getMatchupCancelSubmittedCount()==0 && mr.getIsDeleted()) ||
                    (mr.getMatchupStatus()== Status.PENDING && mr.getMatchupRequestSubmittedCount()==2 && mr.getMatchupCancelSubmittedCount()==0 && mr.getIsDeleted())
            )
            {
                continue;
            }*/
            // 참가 요청 승인: 다른 곳에서 겹치는 시간대에 참가 요청이 승인 됐기 때문에, MatchupBoard 게시글 등록을 허용하면 안됨
            else if(
                    (mr.getMatchupStatus()==Status.APPROVED && mr.getMatchupRequestSubmittedCount()==1 && mr.getMatchupCancelSubmittedCount()==0 && !mr.getIsDeleted())||
                    (mr.getMatchupStatus()==Status.APPROVED && mr.getMatchupRequestSubmittedCount()==2 && mr.getMatchupCancelSubmittedCount()==0 && !mr.getIsDeleted())
            ){
                throw new CustomException("Matchup 등록하신 경기 날짜는 이전에 참가 요청하신 게시글의 경기 날짜와 겹칩니다. 확인 후 다시 작성해주세요");
            }
            // 참가 요청 반려-1: 재요청이 가능하기 때문에, 재요청 파트에서 제한을 걸어줘야됨
            // 참가 요청 반려-2: 재요청이 불가능하기 때문에, 재요청파트에서 제한을 걸지 않아도됨
           /* else if(
                    (mr.getMatchupStatus()==Status.DENIED && mr.getMatchupRequestSubmittedCount()==1 && mr.getMatchupCancelSubmittedCount()==0 && !mr.getIsDeleted())||
                    (mr.getMatchupStatus()==Status.DENIED && mr.getMatchupRequestSubmittedCount()==2 && mr.getMatchupCancelSubmittedCount()==0 && !mr.getIsDeleted())
            ){
                continue;
            }*/
            //승인 취소 요청: 승인 취소 요청 상태이지만, 그래도 다른 곳에서 겹치는 시간대에 참가 요청이 승인 됐기 때문에, MatchupBoard 게시글 등록을 허용하면 안됨
            else if(
                    (mr.getMatchupStatus()==Status.CANCELREQUESTED && mr.getMatchupRequestSubmittedCount()==1 && mr.getMatchupCancelSubmittedCount()==1 && !mr.getIsDeleted())||
                    (mr.getMatchupStatus()==Status.CANCELREQUESTED && mr.getMatchupRequestSubmittedCount()==2 && mr.getMatchupCancelSubmittedCount()==1 && !mr.getIsDeleted())
            ){
                throw new CustomException("Matchup 등록하신 경기 날짜는 이전에 참가 요청하신 게시글의 경기 날짜와 겹칩니다. 확인 후 다시 작성해주세요");
            }
            // 취소 요청 승인: 취소 요청 승인이 되어, MatchupBoard 게시글에 등록을 허용해도됨
           /* else if(
                    (mr.getMatchupStatus()==Status.CANCELREQUESTED && mr.getMatchupRequestSubmittedCount()==1 && mr.getMatchupCancelSubmittedCount()==1 && mr.getIsDeleted())||
                    (mr.getMatchupStatus()==Status.CANCELREQUESTED && mr.getMatchupRequestSubmittedCount()==2 && mr.getMatchupCancelSubmittedCount()==1 && mr.getIsDeleted())
            ){
                continue;
            }*/
            // 취소 요청 반려: 다른 곳에서 겹치는 시간대에 참가 요청이 승인 됐기 때문에, MatchupBoard 게시글 등록을 허용하면 안됨
            else if(
                    (mr.getMatchupStatus()==Status.APPROVED && mr.getMatchupRequestSubmittedCount()==1 && mr.getMatchupCancelSubmittedCount()==1 && !mr.getIsDeleted())||
                    (mr.getMatchupStatus()==Status.APPROVED && mr.getMatchupRequestSubmittedCount()==2 && mr.getMatchupCancelSubmittedCount()==1 && !mr.getIsDeleted())
            ){
                throw new CustomException("Matchup 등록하신 경기 날짜는 이전에 참가 요청하신 게시글의 경기 날짜와 겹칩니다. 확인 후 다시 작성해주세요");

            }
            // 경기 시작 이전만 고려하면 되므로 자동 참가는 고려하지 않음
        }




        //
        // 월: reqMatchupBoardDto.getMatchDatetime().getMonthValue()
        // 일: reqMatchupBoardDto.getMatchDatetime().getDayOfMonth()
        // 종목: reqMatchupBoardDto.getSportsTypeName()
        // 시/도: reqMatchupBoardDto.getSportsFacilityAddress().split(" ")[0]
        // 시/군/구: reqMatchupBoardDto.getSportsFacilityAddress().split(" ")[1]
        String chatName =  "작성자: " +user.getMember().getMemberName() + " [ "+ reqMatchupBoardDto.getSportsTypeName()+" "+" "+reqMatchupBoardDto.getSportsFacilityAddress().split(" ")[1]+" ] " + reqMatchupBoardDto.getMatchDatetime().getMonthValue()+" / "+ reqMatchupBoardDto.getMatchDatetime().getDayOfMonth();

        String identifierChatRoomName = "("+ UUID.randomUUID().toString().replace("-","").substring(0,8)+")";


        ChatRoom chatRoom = chatService.registerGroupChatRoom(user.getMember(), chatName+identifierChatRoomName);

        // Matchup Board 생성하면서 group chat 생성
        MatchupBoard newMatchupBoard = MatchupBoard.builder()
                .writer(user.getMember())
                .sportsType(sportsTypeRepository.findBySportsTypeName(SportsTypeName.valueOf(reqMatchupBoardDto.getSportsTypeName())).orElseThrow(()-> new CustomException("Matchup "+reqMatchupBoardDto.getSportsTypeName()+"는 에서 지원하지 않는 종목입니다.")))
                .reservationAttachmentEnabled(true)
                .teamIntro(reqMatchupBoardDto.getTeamIntro())
                .sportsFacilityName(reqMatchupBoardDto.getSportsFacilityName())
                .sportsFacilityAddress(reqMatchupBoardDto.getSportsFacilityAddress())
                .matchDatetime(reqMatchupBoardDto.getMatchDatetime())
                .matchEndtime(endTime)
                .currentParticipantCount(reqMatchupBoardDto.getCurrentParticipantCount())
                .maxParticipants(reqMatchupBoardDto.getMaxParticipants())
                .minMannerTemperature(reqMatchupBoardDto.getMinMannerTemperature())
                .matchDescription(reqMatchupBoardDto.getMatchDescription())
                //.chatRoom(chatRoom)
                .build();
        MatchupBoard matchupBoard = matchupBoardRepository.save(newMatchupBoard);
        matchupBoard.changeChatRoom(chatRoom);

        //경기장 예약 내역 S3에 업로드
        matchupService.insertFile(reqMatchupBoardDto.getReservationFile(), matchupBoard);
    }

    // 조회


    /*
    * Matchup 게시글 상세조회 페이지로 나갈 정보
    * +
    * Matchup 게시글 수정하기 페이지로 나갈 정보
    * */
    @Transactional(readOnly = true)
    public ResMatchupBoardDto findMatchupBoardByBoardId(Long boardId, CustomUser user) {

        MatchupBoard matchupBoard = matchupBoardRepository.findMatchupBoardByBoardId(boardId).orElseThrow(()->new CustomException("Matchup"+boardId +"번 게시글이 존재하지 않습니다."));

        List<Attachment> attachments = attachmentRepository.findAllByBoardTypeAndBoardNumber(BoardType.MATCHUP_BOARD, boardId);

        if(attachments.isEmpty()&&matchupBoard.getReservationAttachmentEnabled())
            throw new CustomException("Matchup"+boardId +"번 게시글의 첨부파일이 존재해야하는데 없습니다.");

        return ResMatchupBoardDto.builder()
                .boardId(matchupBoard.getId())
                .writerId(matchupBoard.getWriter().getId())
                .writerEmail(matchupBoard.getWriter().getMemberEmail())
                .writerName(matchupBoard.getWriter().getMemberName())
                .teamName(matchupBoard.getWriter().getTeam().getTeamName())
                .teamIntro(matchupBoard.getTeamIntro())
                .sportsTypeName(matchupBoard.getSportsType().getSportsTypeName())
                .sportsFacilityName(matchupBoard.getSportsFacilityName())
                .sportsFacilityAddress(matchupBoard.getSportsFacilityAddress())
                .matchDatetime(matchupBoard.getMatchDatetime())
                .matchEndtime(matchupBoard.getMatchEndtime())
                .currentParticipantCount(matchupBoard.getCurrentParticipantCount())
                .maxParticipants(matchupBoard.getMaxParticipants())
                .minMannerTemperature(matchupBoard.getMinMannerTemperature())
                .myMannerTemperature(user.getMember().getMyTemperature()) //matchupBoard.getMember().getMyTemperature()
                .matchDescription(matchupBoard.getMatchDescription())
                .originalName(attachments.get(0).getOriginalName())
                .savedName(attachments.get(0).getSavedName())
                .savedPath(attachments.get(0).getSavePath())
                .build();

    }


    /*
    * 전체 Matchup 게시글 목록을 가져오는 메서드
    * */
    @Transactional(readOnly = true)
    public PageResponseDto<ResMatchupBoardListDto> findAllMatchupBoardsWithPaging(PageRequest pageRequest, String sportsType, String region, String date, Boolean availableFilter, CustomUser user) {
        SportsTypeName sportsTypeName;
        if(sportsType.isBlank())
            sportsTypeName = null;
        else
            sportsTypeName = SportsTypeName.valueOf(sportsType);

        if(region.isBlank())
            region = null;

        LocalDate matchDate = null;
        if(!date.isBlank())
            matchDate = LocalDate.parse(date);

        Page<ResMatchupBoardListDto> page = matchupBoardRepository.findAllMatchupBoardsWithPaging(pageRequest, sportsTypeName, region, matchDate, availableFilter, user.getMember().getMyTemperature());
        return PageResponseDto.<ResMatchupBoardListDto>builder()
                .items(page.getContent())
                .pageInfo(PageResponseDto.PageInfoDto.builder()
                        .page(page.getNumber())
                        .size(page.getNumberOfElements())
                        .totalElements(page.getTotalElements())
                        .totalPages(page.getTotalPages())
                        .isFirst(page.isFirst())
                        .isLast(page.isLast())
                        .build())
                .build();
    }

    /*
     * 내가 작성한 Matchup 게시글 목록을 가져오는 메서드
     * */
    @Transactional(readOnly = true)
    public PageResponseDto<ResMatchupBoardListDto> findAllMyMatchupBoardsWithPaging(PageRequest pageRequest, CustomUser user, String sportsType, String date, Boolean availableFilter ) {
        SportsTypeName sportsTypeName;
        if(sportsType.isBlank())
            sportsTypeName = null;
        else
            sportsTypeName = SportsTypeName.valueOf(sportsType);

        LocalDate matchDate = null;
        if(!date.isBlank())
            matchDate = LocalDate.parse(date);


        Page<ResMatchupBoardListDto> page = matchupBoardRepository.findAllResMatchupBoardListDtosByMemberWithPaging(pageRequest, user.getMember(), sportsTypeName, matchDate, availableFilter);
        return PageResponseDto.<ResMatchupBoardListDto>builder()
                .items(page.getContent())
                .pageInfo(PageResponseDto.PageInfoDto.builder()
                        .page(page.getNumber())
                        .size(page.getNumberOfElements())
                        .totalElements(page.getTotalElements())
                        .totalPages(page.getTotalPages())
                        .isFirst(page.isFirst())
                        .isLast(page.isLast())
                        .build())
                .build();
    }

    // 수정


    /*
    * matchup board 수정
    * 경기 시작 시간과 경기 진행 시간 수정 불가능하게 변경함
    * */
    @Transactional
    public void updateBoard(ReqMatchupBoardEditDto reqMatchupBoardEditDto, CustomUser user) {
        MatchupBoard findMatchupBoard = matchupBoardRepository.findMatchupBoardByBoardIdAndIsDeleted(reqMatchupBoardEditDto.getBoardId(), user.getMember()).orElseThrow(()->new CustomException("Matchup"+reqMatchupBoardEditDto.getBoardId()+"번 게시글이 없습니다."));

        // 경기 시작 시간이 지난 경우
        if(findMatchupBoard.getMatchDatetime().isBefore(LocalDateTime.now()))
            throw new CustomException("Matchup 경기 시작 시간이 지나 수정할 수 없습니다.");


        //모집인원 체크
        if(findMatchupBoard.getCurrentParticipantCount()>reqMatchupBoardEditDto.getMaxParticipants())
            throw new CustomException("Matchup 총 모집 인원은 현재 모집된 인원 이상이여야 합니다.");

        if(reqMatchupBoardEditDto.getMinMannerTemperature()>reqMatchupBoardEditDto.getMyMannerTemperature())
            throw new CustomException("Matchup 하한 매너 온도는 내 매너온도 이상이어야 합니다.");

        findMatchupBoard.update(
                sportsTypeRepository.findBySportsTypeName(SportsTypeName.valueOf(reqMatchupBoardEditDto.getSportsTypeName())).orElseThrow(()-> new CustomException("Matchup"+reqMatchupBoardEditDto.getSportsTypeName()+"는 에서 지원하지 않는 종목입니다.")),
                reqMatchupBoardEditDto.getTeamIntro(),
                reqMatchupBoardEditDto.getSportsFacilityName(),
                reqMatchupBoardEditDto.getSportsFacilityAddress(),
                reqMatchupBoardEditDto.getCurrentParticipantCount(),
                reqMatchupBoardEditDto.getMaxParticipants(),
                reqMatchupBoardEditDto.getMinMannerTemperature(),
                reqMatchupBoardEditDto.getMatchDescription()
        );

        if(!Objects.requireNonNull(reqMatchupBoardEditDto.getReservationFile().getOriginalFilename()).isBlank()){
            matchupService.updateFile(reqMatchupBoardEditDto.getReservationFile(), findMatchupBoard);
        }

        // 현재 참가 신청한 참여자들에게 게시글 수정 알리기
        /*
        * 보내야하는 대상:
        * 승인 대기, 승인됨, 승인 취소 요청, 취소 요청 반려
        * */

        List<Member> applicants = matchupRequestRepository.findByBoardIdAndActiveRequests(findMatchupBoard.getId());
        for(Member applicant: applicants){
            notificationService.sendNotificationWithoutMail(applicant, "[게시글 수정] 참가 요청하신 "+user.getMember().getMemberName()+"님의 게시글이 수정되었습니다.", "/matchup/board/detail?"+"matchup-board-id="+reqMatchupBoardEditDto.getBoardId());
        }

    }

    // 삭제


    /*
    * Matchup 게시글 삭제하기
    * S3 파일은 삭제안함, soft delete라
    * */
    @Transactional
    public void softDeleteMatchupBoard(Long boardId, CustomUser user) {
        MatchupBoard findMatchupBoard = matchupBoardRepository.findMatchupBoardByBoardIdAndIsDeleted(boardId, user.getMember()).orElseThrow(()->new CustomException("Matchup "+boardId+"번 게시글이 없습니다."));

        if(findMatchupBoard.getMatchDatetime().isBefore(LocalDateTime.now()))
            throw new CustomException("Matchup 경기 시작 시간이 지나 삭제할 수 없습니다.");


        // 현재 참가 신청한 참여자들에게 게시글 삭제 알리기
        /*
         * 보내야하는 대상:
         * 승인 대기, 승인됨, 승인 취소 요청, 취소 요청 반려
         * */
        List<Member> applicants = matchupRequestRepository.findByBoardIdAndActiveRequests(findMatchupBoard.getId());
        for(Member applicant: applicants){
            notificationService.sendNotificationWithoutMail(applicant, "[게시글 삭제] 참가 요청하신 "+user.getMember().getMemberName()+"님의 게시글이 삭제되었습니다.", null);
        }

        findMatchupBoard.delete(true);



        List<Attachment> findAttachments = attachmentRepository.findAllByBoardTypeAndBoardNumber(BoardType.MATCHUP_BOARD, boardId);
        if(findAttachments.isEmpty())
            throw new CustomException("Matchup "+BoardType.MATCHUP_BOARD+"타입, "+findMatchupBoard.getId()+"번에는 첨부파일이 없습니다.");
        findAttachments.get(0).delete(true);



    }

    @Transactional(readOnly = true)
    public ResMatchupBoardOverviewDto findResMatchupOverviewDto(Long boardId) {

        return matchupBoardRepository.findResMatchupOverviewDto(boardId).orElseThrow(()->new CustomException("Matchup "+boardId+"번 게시글이 없습니다."));
    }

    /*
    * 경기 3시간 전에 참가자들에게 알림 메시지를 보내기
    * */
    @Transactional
    public Long notifyAllParticipantsBeforeStart() {

        // 목적: 경기 3시간 전 이고 참가자들에게 알림 보내지 않은 MatchupBoard를 모두 가져옴
        // 알림 받는 참가자: 승인대기, 승인됨, 승인 취소 요청, 취소 요청 반려,

        // 1. 현재 시간 3시간 후 LocalDatetime 세팅
        LocalDateTime threeHoursLater = LocalDateTime.now().plusHours(3);

        // 2. 알림 대상이 되는 MatchupBoard 목록을 가져옴
        List<MatchupBoard> matchupBoardsToNotifyBeforeThreeHours = matchupBoardRepository.findUnnotifiedBoardsAtThreeHoursBeforeMatch(threeHoursLater);

        if(matchupBoardsToNotifyBeforeThreeHours.isEmpty())
            return 0L;

        // 3. 알림을 받아야하는 요청 목록들을 가져옴

        List<MatchupRequest> matchupRequestsToNotifyBeforeThreeHours = matchupRequestRepository.findUnnotifiedRequestsAtThreeHoursBeforeMatch(threeHoursLater);


        // 4. 알림 보내기
        Long count = 0L; // 알림 갯수
        for(MatchupBoard matchupBoard: matchupBoardsToNotifyBeforeThreeHours){

            //참가자들에게 보내기
            count += matchupRequestsToNotifyBeforeThreeHours.stream()
                    .filter(matchupreq ->matchupreq.getMatchupBoard().getId().equals(matchupBoard.getId()))
                    .peek(matchupreq ->{
                        notificationService.sendNotificationWithoutMail(matchupreq.getMember(),"[경기 예정]"+matchupBoard.getMatchDatetime()+"에 경기가 진행될 예정입니다.","/matchup/board/detail?matchup-board-id="+matchupBoard.getId());
                    })
                    .count();

            // 작성자에게 보내기
            notificationService.sendNotificationWithoutMail(matchupBoard.getWriter(),"[경기 예정]"+matchupBoard.getMatchDatetime()+"에 경기가 진행될 예정입니다.","/matchup/board/detail?matchup-board-id="+matchupBoard.getId());
            count++;

            matchupBoard.updateIsNotified(true);
        }

        return count;
    }



    // ========================================================================================================
    //                                                    테스트 해본 코드
    // ========================================================================================================

    //    public List<MatchupBoard> findAll() {
//        List<MatchupBoard> matchupBoards = matchupBoardRepository.findAll();
//        List<MatchupBoard> matchupBoards1 = matchupBoardRepository.findAllWithMember();
//        List<MatchupBoard> matchupBoards2 = matchupBoardRepository.findAllWithMemberAndWithSportsType();
//
//        return matchupBoards;
//    }




}

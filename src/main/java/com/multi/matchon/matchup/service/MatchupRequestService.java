package com.multi.matchon.matchup.service;


import com.multi.matchon.chat.dto.res.ResChatErrorDto;
import com.multi.matchon.chat.service.ChatService;
import com.multi.matchon.chat.service.RedisPubSubService;
import com.multi.matchon.common.auth.dto.CustomUser;
import com.multi.matchon.common.domain.SportsTypeName;
import com.multi.matchon.common.domain.Status;
import com.multi.matchon.common.dto.res.PageResponseDto;
import com.multi.matchon.common.exception.custom.CustomException;

import com.multi.matchon.common.service.NotificationService;
import com.multi.matchon.matchup.domain.MatchupBoard;
import com.multi.matchon.matchup.domain.MatchupRequest;
import com.multi.matchon.matchup.dto.req.ReqMatchupRequestDto;
import com.multi.matchon.matchup.dto.req.ReqMatchupRequestEditDto;
import com.multi.matchon.matchup.dto.res.ResMatchupRequestDto;
import com.multi.matchon.matchup.dto.res.ResMatchupRequestListDto;
import com.multi.matchon.matchup.dto.res.ResMatchupRequestOverviewListDto;
import com.multi.matchon.matchup.repository.MatchupBoardRepository;
import com.multi.matchon.matchup.repository.MatchupRequestRepository;
import com.multi.matchon.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchupRequestService {

    private final MatchupBoardRepository matchupBoardRepository;
    private final MatchupRequestRepository matchupRequestRepository;
    private final MemberRepository memberRepository;
    private final ChatService chatService;
    //private final SimpMessageSendingOperations messageTemplate;
    private final NotificationService notificationService;
    private final RedisPubSubService redisPubSubService;

    // 등록

    @Transactional
    public void registerMatchupRequest(ReqMatchupRequestDto reqMatchupRequestDto, CustomUser user) {

      /*  // 1. 2회 이상 취소이력 검사
        Boolean isCanceled = matchupRequestRepository.hasCanceledMatchRequestMoreThanOnce(reqMatchupRequestDto.getBoardId(), user.getMember().getId());
        if(isCanceled)
            throw new hasCanceledMatchRequestMoreThanOnceException("1번의 취소 이력이 있어 Matchup 참가 요청이 불가능합니다.");

        // 2. 중복 검사
        Boolean isDuplicate = matchupRequestRepository.isAlreadyMatchupRequestedByBoardIdAndMemberId(reqMatchupRequestDto.getBoardId(), user.getMember().getId());
        if(isDuplicate)
            throw new DuplicateRequestException("중복된 Matchup 참가 요청입니다.");


        // 3. 재요청이 3번 이상인지 체크(2번까지는 요청 가능)
        Boolean isExceed = matchupRequestRepository.hasExceededTwoMatchupRequestsByBoardIdAndMemberId(reqMatchupRequestDto.getBoardId(), user.getMember().getId());
        if(isExceed)
            throw new MatchupRequestLimitExceededException("Matchup 참가 요청을 2번 하셔서 더 이상 요청은 불가능 합니다.");

         MatchupRequest matchupRequest = MatchupRequest.builder()
                    .matchupBoard(matchupBoardRepository.findById(reqMatchupRequestDto.getBoardId()).orElseThrow(()-> new IllegalArgumentException(reqMatchupRequestDto.getBoardId()+"번 게시글은 없습니다.")))
                    .member(user.getMember())
                    .selfIntro(reqMatchupRequestDto.getSelfIntro())
                    .matchupRequestResubmittedCount(0)
                    .participantCount(reqMatchupRequestDto.getParticipantCount())
                    .build();
            matchupRequestRepository.save(matchupRequest);*/


        // 1. 참가 요청할 게시물이 있는지 확인

        MatchupBoard findMatchupBoard = matchupBoardRepository.findByIdAndIsDeletedFalse(reqMatchupRequestDto.getBoardId()).orElseThrow(()->new CustomException("Matchup "+reqMatchupRequestDto.getBoardId()+"번 게시글이 존재하지 않습니다."));

        if(findMatchupBoard.getMatchDatetime().isBefore(LocalDateTime.now())){
            throw new CustomException("Matchup 경기 시작 시간이 지나 참가 요청할 수 없습니다.");
        }

        if(findMatchupBoard.getMinMannerTemperature()>user.getMember().getMyTemperature())
            throw new CustomException("Matchup 최소 매너 온도를 충족하지 못해 참가 요청을 할 수 없습니다.");


        if(findMatchupBoard.getCurrentParticipantCount()>findMatchupBoard.getMaxParticipants()){
            throw new CustomException("Matchup 현재 모집 인원을 초과해서 참가 요청을 할 수 없습니다.");
        }

        if(findMatchupBoard.getCurrentParticipantCount()+reqMatchupRequestDto.getParticipantCount()>findMatchupBoard.getMaxParticipants()){
            throw new CustomException("Matchup 신청 하신 인원은 현재 모집원을 초과해서 신청할 수 없습니다.");
        }

        // 2. 작성한 기존 게시글 경기 시간과  내가 참가 요청하는 경기 시간이 겹치는 지 체크, 3시 끝나고 다음 경기가 3시에 시작인 경우는 허용함
        Boolean isDuplicate = matchupBoardRepository.findByMemberAndStartTimeAndEndTime(user.getMember(),findMatchupBoard.getMatchDatetime(),findMatchupBoard.getMatchEndtime());
        if(isDuplicate)
            throw new CustomException("Matchup 참가 요청하신 경기 날짜는 이전에 작성하신 게시글의 경기 날짜와 겹칩니다. 확인 후 다시 작성해주세요.");

        // 3.  // 참가 요청 내역과 비교

        // 3-1. 내가 참가 요청한 것들 가져옴
        // 조건: 기존 경기 시간과 참가 요청하려는 게시글의 경기 시간이 겹치는 것
        // 조건: Matchup 게시글이 삭제가 안된 것
        List<MatchupRequest> duplicatedMatchupRequests = matchupRequestRepository.findByMemberAndStartTimeAndEndTime(user.getMember(),findMatchupBoard.getMatchDatetime(),findMatchupBoard.getMatchEndtime());

        for(MatchupRequest mr:duplicatedMatchupRequests){
            //승인 대기: 다른 곳에서 겹치는 시간대에 참가 요청한 것이 있기 때문에, 참가 요청 등록을 허용하면 안됨
            if(
                    (mr.getMatchupStatus()== Status.PENDING && mr.getMatchupRequestSubmittedCount()==1 && mr.getMatchupCancelSubmittedCount()==0 && !mr.getIsDeleted()) ||
                            (mr.getMatchupStatus()== Status.PENDING && mr.getMatchupRequestSubmittedCount()==2 && mr.getMatchupCancelSubmittedCount()==0 && !mr.getIsDeleted())
            ){
                throw new CustomException("Matchup 참가 요청하신 경기 날짜는 이전에 참가 요청하신 게시글의 경기 날짜와 겹칩니다. 확인 후 다시 작성해주세요");
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
            // 참가 요청 승인: 다른 곳에서 겹치는 시간대에 참가 요청이 승인 됐기 때문에, 참가 요청 등록을 허용하면 안됨
            else if(
                    (mr.getMatchupStatus()==Status.APPROVED && mr.getMatchupRequestSubmittedCount()==1 && mr.getMatchupCancelSubmittedCount()==0 && !mr.getIsDeleted())||
                            (mr.getMatchupStatus()==Status.APPROVED && mr.getMatchupRequestSubmittedCount()==2 && mr.getMatchupCancelSubmittedCount()==0 && !mr.getIsDeleted())
            ){
                throw new CustomException("Matchup 참가 요청하신 경기 날짜는 이전에 참가 요청하신 게시글의 경기 날짜와 겹칩니다. 확인 후 다시 작성해주세요");
            }
            // 참가 요청 반려-1: 재요청이 가능하기 때문에, 재요청 파트에서 제한을 걸어줘야됨
            // 참가 요청 반려-2: 재요청이 불가능하기 때문에, 재요청파트에서 제한을 걸지 않아도됨
           /* else if(
                    (mr.getMatchupStatus()==Status.DENIED && mr.getMatchupRequestSubmittedCount()==1 && mr.getMatchupCancelSubmittedCount()==0 && !mr.getIsDeleted())||
                    (mr.getMatchupStatus()==Status.DENIED && mr.getMatchupRequestSubmittedCount()==2 && mr.getMatchupCancelSubmittedCount()==0 && !mr.getIsDeleted())
            ){
                continue;
            }*/
            //승인 취소 요청: 승인 취소 요청 상태이지만, 그래도 다른 곳에서 겹치는 시간대에 참가 요청이 승인 됐기 때문에, 참가 요청 등록을 허용하면 안됨
            else if(
                    (mr.getMatchupStatus()==Status.CANCELREQUESTED && mr.getMatchupRequestSubmittedCount()==1 && mr.getMatchupCancelSubmittedCount()==1 && !mr.getIsDeleted())||
                            (mr.getMatchupStatus()==Status.CANCELREQUESTED && mr.getMatchupRequestSubmittedCount()==2 && mr.getMatchupCancelSubmittedCount()==1 && !mr.getIsDeleted())
            ){
                throw new CustomException("Matchup 참가 요청하신 경기 날짜는 이전에 참가 요청하신 게시글의 경기 날짜와 겹칩니다. 확인 후 다시 작성해주세요");
            }
            // 취소 요청 승인: 취소 요청 승인이 되어, 참가 요청 등록을 허용해도됨
           /* else if(
                    (mr.getMatchupStatus()==Status.CANCELREQUESTED && mr.getMatchupRequestSubmittedCount()==1 && mr.getMatchupCancelSubmittedCount()==1 && mr.getIsDeleted())||
                    (mr.getMatchupStatus()==Status.CANCELREQUESTED && mr.getMatchupRequestSubmittedCount()==2 && mr.getMatchupCancelSubmittedCount()==1 && mr.getIsDeleted())
            ){
                continue;
            }*/
            // 취소 요청 반려: 다른 곳에서 겹치는 시간대에 참가 요청이 승인 됐기 때문에, 참가 요청 등록을 허용하면 안됨
            else if(
                    (mr.getMatchupStatus()==Status.APPROVED && mr.getMatchupRequestSubmittedCount()==1 && mr.getMatchupCancelSubmittedCount()==1 && !mr.getIsDeleted())||
                            (mr.getMatchupStatus()==Status.APPROVED && mr.getMatchupRequestSubmittedCount()==2 && mr.getMatchupCancelSubmittedCount()==1 && !mr.getIsDeleted())
            ){
                throw new CustomException("Matchup 참가 요청하신 경기 날짜는 이전에 참가 요청하신 게시글의 경기 날짜와 겹칩니다. 확인 후 다시 작성해주세요");

            }
            // 경기 시작 이전만 고려하면 되므로 자동 참가는 고려하지 않음
        }

        // 4. boardId와 applicantId에 대응되는 request가 있는 지 판단

        Optional<MatchupRequest> findMatchupRequestOptional = matchupRequestRepository.findByMatchupBoardIdAndApplicantId(reqMatchupRequestDto.getBoardId(), user.getMember().getId());

        if(findMatchupRequestOptional.isEmpty()){
            MatchupRequest newMatchupRequest = MatchupRequest.builder()
                    .matchupBoard(findMatchupBoard)
                    .member(user.getMember()) //applicant
                    .selfIntro(reqMatchupRequestDto.getSelfIntro())
                    .participantCount(reqMatchupRequestDto.getParticipantCount())
                    .matchupStatus(Status.PENDING)
                    .build();
            matchupRequestRepository.save(newMatchupRequest);
        }
        else{
            throw new CustomException("Matchup 요청 이력이 있어 현재 참가 요청을 할 수 없습니다.");
        }

        /*
        * 5. 작성자에게 알림 보내기
        * */
        notificationService.sendNotificationWithoutMail(findMatchupBoard.getWriter() , "[참가 요청]"+user.getMember().getMemberName()+"님의 참가 요청이 있습니다.", "/matchup/request/board?"+"board-id="+findMatchupBoard.getId());

    }

    // 조회

    @Transactional(readOnly = true)
    public PageResponseDto<ResMatchupRequestListDto> findAllMyMatchupRequestWithPaging(PageRequest pageRequest, CustomUser user, String sportsType, String date, Boolean availableFilter) {

        SportsTypeName sportsTypeName;
        if(sportsType.isBlank())
            sportsTypeName = null;
        else
            sportsTypeName = SportsTypeName.valueOf(sportsType);

        LocalDate matchDate = null;
        if(!date.isBlank())
            matchDate = LocalDate.parse(date);


        Page<ResMatchupRequestListDto> page = matchupRequestRepository.findAllResMatchupRequestListDtosByMemberIdAndSportsTypeAndMatchDateWithPaging(pageRequest,user.getMember().getId(), sportsTypeName, matchDate, availableFilter);

        return PageResponseDto.<ResMatchupRequestListDto>builder()
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

    // 참가요청 등록시 사용, board찾음
    @Transactional(readOnly = true)
    public ReqMatchupRequestDto findReqMatchupRequestDtoByBoardId(Long boardId) {

        ReqMatchupRequestDto reqMatchupRequestDto =  matchupBoardRepository.findReqMatchupRequestDtoByBoardId(boardId).orElseThrow(()->new CustomException("Matchup"+boardId+"번 게시글이 없습니다."));

        // 경기 시작 시간이 지났는지 확인
        if(reqMatchupRequestDto.getMatchDatetime().isBefore(LocalDateTime.now())){
            throw new CustomException("Matchup 경기 시작 시간이 지나 참가 요청할 수 없습니다.");
        }


        //인원 수 체크

        return reqMatchupRequestDto;
    }

    // 상세보기용
    @Transactional(readOnly = true)
    public ResMatchupRequestDto findResMatchRequestDtoByRequestId(Long requestId) {

        return matchupRequestRepository.findResMatchupRequestDtoByRequestId(requestId).orElseThrow(()->new CustomException("Matchup "+requestId+"번 요청은 없습니다."));

    }

    /*
    * 특정 게시글에 들어온 요청 목록
    * */
    public PageResponseDto<ResMatchupRequestOverviewListDto> findAllMatchupRequestByBoardWithPaging(PageRequest pageRequest, Long boardId) {

        Page<ResMatchupRequestOverviewListDto> page = matchupRequestRepository.findAllResMatchupRequestOverviewListDtoByBoardIdAndSportsTypeWithPaging(pageRequest,boardId);

        return PageResponseDto.<ResMatchupRequestOverviewListDto>builder()
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


    //수정하기 조회용, 상태 체크를 해야됨
    @Transactional(readOnly = true)
    public ResMatchupRequestDto findResMatchRequestDtoByRequestIdAndModifyStatus(Long requestId) {

        ResMatchupRequestDto resMatchupRequestDto = matchupRequestRepository.findResMatchupRequestDtoByRequestId(requestId).orElseThrow(()->new CustomException("Matchup "+requestId+"번 요청은 없습니다."));

        // 0. 모집된 인원이 총 모집 인원보다 크거나 같은 경우 예외 발생
        if(resMatchupRequestDto.getCurrentParticipantCount()>=resMatchupRequestDto.getMaxParticipants()){
            throw new CustomException("Matchup 현재 정원이 가득 찼습니다. 수정이 불가능합니다.");
        }

        // 1. 경기 시작 시간이 지났는지 체크

        if(resMatchupRequestDto.getMatchDatetime().isBefore(LocalDateTime.now())){
            throw new CustomException("Matchup 경기 시작 시간이 지나 수정할 수 없습니다.");
        }

        // 2. 수정가능한 상태인지 체크
        if(
                (resMatchupRequestDto.getMatchupStatus()==Status.PENDING && resMatchupRequestDto.getMatchupRequestSubmittedCount()==1 && resMatchupRequestDto.getMatchupCancelSubmittedCount()==0 && !resMatchupRequestDto.getIsDeleted()) ||
                (resMatchupRequestDto.getMatchupStatus()==Status.PENDING && resMatchupRequestDto.getMatchupRequestSubmittedCount()==2 && resMatchupRequestDto.getMatchupCancelSubmittedCount()==0 && !resMatchupRequestDto.getIsDeleted())
        ){
            return resMatchupRequestDto;
        }else{
            throw new CustomException("Matchup 참가 요청이 승인 되어 수정이 불가능합니다. 이력을 참고해주세요.");
        }

    }


    // 수정
    @Transactional
    public void updateMatchupRequest(ReqMatchupRequestEditDto reqMatchupRequestEditDto, Long requestId, CustomUser user){
        MatchupRequest findMatchupRequest = matchupRequestRepository.findById(requestId).orElseThrow(()->new CustomException("Matchup "+requestId+"번 요청은 없습니다."));

        // 1. 경기 시작 시간이 지났는지 체크

        if(reqMatchupRequestEditDto.getMatchDatetime().isBefore(LocalDateTime.now())){
            throw new CustomException("Matchup 경기 시작 시간이 지나 수정할 수 없습니다.");
        }

        // 수정 가능한 상태인지 체크
        if(
                (findMatchupRequest.getMatchupStatus()==Status.PENDING && findMatchupRequest.getMatchupRequestSubmittedCount()==1 && findMatchupRequest.getMatchupCancelSubmittedCount()==0 && !findMatchupRequest.getIsDeleted())||
                (findMatchupRequest.getMatchupStatus()==Status.PENDING && findMatchupRequest.getMatchupRequestSubmittedCount()==2 && findMatchupRequest.getMatchupCancelSubmittedCount()==0 && !findMatchupRequest.getIsDeleted())
        ){
                findMatchupRequest.update(reqMatchupRequestEditDto.getSelfIntro(), reqMatchupRequestEditDto.getParticipantCount());
            /*
             * 작성자에게 알림 보내기
             * */
            notificationService.sendNotificationWithoutMail(findMatchupRequest.getMatchupBoard().getWriter() , "[참가 요청 수정]"+findMatchupRequest.getMember().getMemberName()+"님의 참가 요청이 수정되었습니다.", "/matchup/request/board?"+"board-id="+findMatchupRequest.getMatchupBoard().getId());

        }
        // 재요청인 경우: 상태 업데이트 해야됨
        else if(findMatchupRequest.getMatchupStatus()==Status.PENDING && findMatchupRequest.getMatchupRequestSubmittedCount()==1 && findMatchupRequest.getMatchupCancelSubmittedCount()==0 && findMatchupRequest.getIsDeleted()){

            // 1. 작성한 기존 게시글 경기 시간과  내가 참가 요청하는 경기 시간이 겹치는 지 체크, 3시 끝나고 다음 경기가 3시에 시작인 경우는 허용함
            Boolean isDuplicate = matchupBoardRepository.findByMemberAndStartTimeAndEndTime(user.getMember(),reqMatchupRequestEditDto.getMatchDatetime(),reqMatchupRequestEditDto.getMatchEndtime());
            if(isDuplicate)
                throw new CustomException("Matchup 참가 요청하신 경기 날짜는 이전에 작성하신 게시글의 경기 날짜와 겹칩니다. 확인 후 다시 작성해주세요.");

            // 2.  // 참가 요청 내역과 비교

            // 2-1. 내가 참가 요청한 것들 가져옴
            // 조건: 기존 경기 시간과 참가 요청하려는 게시글의 경기 시간이 겹치는 것
            // 조건: Matchup 게시글이 삭제가 안된 것
            List<MatchupRequest> duplicatedMatchupRequests = matchupRequestRepository.findByMemberAndStartTimeAndEndTime(user.getMember(),reqMatchupRequestEditDto.getMatchDatetime(),reqMatchupRequestEditDto.getMatchEndtime());

            for(MatchupRequest mr:duplicatedMatchupRequests){
                //승인 대기: 다른 곳에서 겹치는 시간대에 참가 요청한 것이 있기 때문에, 참가 요청 등록을 허용하면 안됨
                if(
                        (mr.getMatchupStatus()== Status.PENDING && mr.getMatchupRequestSubmittedCount()==1 && mr.getMatchupCancelSubmittedCount()==0 && !mr.getIsDeleted()) ||
                                (mr.getMatchupStatus()== Status.PENDING && mr.getMatchupRequestSubmittedCount()==2 && mr.getMatchupCancelSubmittedCount()==0 && !mr.getIsDeleted())
                ){
                    throw new CustomException("Matchup 참가 요청하신 경기 날짜는 이전에 참가 요청하신 게시글의 경기 날짜와 겹칩니다. 확인 후 다시 작성해주세요");
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
                // 참가 요청 승인: 다른 곳에서 겹치는 시간대에 참가 요청이 승인 됐기 때문에, 참가 요청 등록을 허용하면 안됨
                else if(
                        (mr.getMatchupStatus()==Status.APPROVED && mr.getMatchupRequestSubmittedCount()==1 && mr.getMatchupCancelSubmittedCount()==0 && !mr.getIsDeleted())||
                                (mr.getMatchupStatus()==Status.APPROVED && mr.getMatchupRequestSubmittedCount()==2 && mr.getMatchupCancelSubmittedCount()==0 && !mr.getIsDeleted())
                ){
                    throw new CustomException("Matchup 참가 요청하신 경기 날짜는 이전에 참가 요청하신 게시글의 경기 날짜와 겹칩니다. 확인 후 다시 작성해주세요");
                }
                // 참가 요청 반려-1: 재요청이 가능하기 때문에, 재요청 파트에서 제한을 걸어줘야됨
                // 참가 요청 반려-2: 재요청이 불가능하기 때문에, 재요청파트에서 제한을 걸지 않아도됨
           /* else if(
                    (mr.getMatchupStatus()==Status.DENIED && mr.getMatchupRequestSubmittedCount()==1 && mr.getMatchupCancelSubmittedCount()==0 && !mr.getIsDeleted())||
                    (mr.getMatchupStatus()==Status.DENIED && mr.getMatchupRequestSubmittedCount()==2 && mr.getMatchupCancelSubmittedCount()==0 && !mr.getIsDeleted())
            ){
                continue;
            }*/
                //승인 취소 요청: 승인 취소 요청 상태이지만, 그래도 다른 곳에서 겹치는 시간대에 참가 요청이 승인 됐기 때문에, 참가 요청 등록을 허용하면 안됨
                else if(
                        (mr.getMatchupStatus()==Status.CANCELREQUESTED && mr.getMatchupRequestSubmittedCount()==1 && mr.getMatchupCancelSubmittedCount()==1 && !mr.getIsDeleted())||
                                (mr.getMatchupStatus()==Status.CANCELREQUESTED && mr.getMatchupRequestSubmittedCount()==2 && mr.getMatchupCancelSubmittedCount()==1 && !mr.getIsDeleted())
                ){
                    throw new CustomException("Matchup 참가 요청하신 경기 날짜는 이전에 참가 요청하신 게시글의 경기 날짜와 겹칩니다. 확인 후 다시 작성해주세요");
                }
                // 취소 요청 승인: 취소 요청 승인이 되어, 참가 요청 등록을 허용해도됨
           /* else if(
                    (mr.getMatchupStatus()==Status.CANCELREQUESTED && mr.getMatchupRequestSubmittedCount()==1 && mr.getMatchupCancelSubmittedCount()==1 && mr.getIsDeleted())||
                    (mr.getMatchupStatus()==Status.CANCELREQUESTED && mr.getMatchupRequestSubmittedCount()==2 && mr.getMatchupCancelSubmittedCount()==1 && mr.getIsDeleted())
            ){
                continue;
            }*/
                // 취소 요청 반려: 다른 곳에서 겹치는 시간대에 참가 요청이 승인 됐기 때문에, 참가 요청 등록을 허용하면 안됨
                else if(
                        (mr.getMatchupStatus()==Status.APPROVED && mr.getMatchupRequestSubmittedCount()==1 && mr.getMatchupCancelSubmittedCount()==1 && !mr.getIsDeleted())||
                                (mr.getMatchupStatus()==Status.APPROVED && mr.getMatchupRequestSubmittedCount()==2 && mr.getMatchupCancelSubmittedCount()==1 && !mr.getIsDeleted())
                ){
                    throw new CustomException("Matchup 참가 요청하신 경기 날짜는 이전에 참가 요청하신 게시글의 경기 날짜와 겹칩니다. 확인 후 다시 작성해주세요");

                }
                // 경기 시작 이전만 고려하면 되므로 자동 참가는 고려하지 않음
            }


            findMatchupRequest.updateRequestMangementInfo(Status.PENDING, 2, 0, false);
            findMatchupRequest.update(reqMatchupRequestEditDto.getSelfIntro(), reqMatchupRequestEditDto.getParticipantCount());

            /*
             * 3. 작성자에게 알림 보내기
             * */
            notificationService.sendNotificationWithoutMail(findMatchupRequest.getMatchupBoard().getWriter() , "[참가 재요청]"+findMatchupRequest.getMember().getMemberName()+"님이 참가 재요청 했습니다.", "/matchup/request/board?"+"board-id="+findMatchupRequest.getMatchupBoard().getId());

        }else if(findMatchupRequest.getMatchupStatus()==Status.DENIED && findMatchupRequest.getMatchupRequestSubmittedCount()==1 && findMatchupRequest.getMatchupCancelSubmittedCount()==0 && !findMatchupRequest.getIsDeleted()){

            // 1. 작성한 기존 게시글 경기 시간과  내가 참가 요청하는 경기 시간이 겹치는 지 체크, 3시 끝나고 다음 경기가 3시에 시작인 경우는 허용함
            Boolean isDuplicate = matchupBoardRepository.findByMemberAndStartTimeAndEndTime(user.getMember(),reqMatchupRequestEditDto.getMatchDatetime(),reqMatchupRequestEditDto.getMatchEndtime());
            if(isDuplicate)
                throw new CustomException("Matchup 참가 요청하신 경기 날짜는 이전에 작성하신 게시글의 경기 날짜와 겹칩니다. 확인 후 다시 작성해주세요.");

            // 2.  // 참가 요청 내역과 비교

            // 2-1. 내가 참가 요청한 것들 가져옴
            // 조건: 기존 경기 시간과 참가 요청하려는 게시글의 경기 시간이 겹치는 것
            // 조건: Matchup 게시글이 삭제가 안된 것
            List<MatchupRequest> duplicatedMatchupRequests = matchupRequestRepository.findByMemberAndStartTimeAndEndTime(user.getMember(),reqMatchupRequestEditDto.getMatchDatetime(),reqMatchupRequestEditDto.getMatchEndtime());

            for(MatchupRequest mr:duplicatedMatchupRequests){
                //승인 대기: 다른 곳에서 겹치는 시간대에 참가 요청한 것이 있기 때문에, 참가 요청 등록을 허용하면 안됨
                if(
                        (mr.getMatchupStatus()== Status.PENDING && mr.getMatchupRequestSubmittedCount()==1 && mr.getMatchupCancelSubmittedCount()==0 && !mr.getIsDeleted()) ||
                                (mr.getMatchupStatus()== Status.PENDING && mr.getMatchupRequestSubmittedCount()==2 && mr.getMatchupCancelSubmittedCount()==0 && !mr.getIsDeleted())
                ){
                    throw new CustomException("Matchup 참가 요청하신 경기 날짜는 이전에 참가 요청하신 게시글의 경기 날짜와 겹칩니다. 확인 후 다시 작성해주세요");
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
                // 참가 요청 승인: 다른 곳에서 겹치는 시간대에 참가 요청이 승인 됐기 때문에, 참가 요청 등록을 허용하면 안됨
                else if(
                        (mr.getMatchupStatus()==Status.APPROVED && mr.getMatchupRequestSubmittedCount()==1 && mr.getMatchupCancelSubmittedCount()==0 && !mr.getIsDeleted())||
                                (mr.getMatchupStatus()==Status.APPROVED && mr.getMatchupRequestSubmittedCount()==2 && mr.getMatchupCancelSubmittedCount()==0 && !mr.getIsDeleted())
                ){
                    throw new CustomException("Matchup 참가 요청하신 경기 날짜는 이전에 참가 요청하신 게시글의 경기 날짜와 겹칩니다. 확인 후 다시 작성해주세요");
                }
                // 참가 요청 반려-1: 재요청이 가능하기 때문에, 재요청 파트에서 제한을 걸어줘야됨
                // 참가 요청 반려-2: 재요청이 불가능하기 때문에, 재요청파트에서 제한을 걸지 않아도됨
           /* else if(
                    (mr.getMatchupStatus()==Status.DENIED && mr.getMatchupRequestSubmittedCount()==1 && mr.getMatchupCancelSubmittedCount()==0 && !mr.getIsDeleted())||
                    (mr.getMatchupStatus()==Status.DENIED && mr.getMatchupRequestSubmittedCount()==2 && mr.getMatchupCancelSubmittedCount()==0 && !mr.getIsDeleted())
            ){
                continue;
            }*/
                //승인 취소 요청: 승인 취소 요청 상태이지만, 그래도 다른 곳에서 겹치는 시간대에 참가 요청이 승인 됐기 때문에, 참가 요청 등록을 허용하면 안됨
                else if(
                        (mr.getMatchupStatus()==Status.CANCELREQUESTED && mr.getMatchupRequestSubmittedCount()==1 && mr.getMatchupCancelSubmittedCount()==1 && !mr.getIsDeleted())||
                                (mr.getMatchupStatus()==Status.CANCELREQUESTED && mr.getMatchupRequestSubmittedCount()==2 && mr.getMatchupCancelSubmittedCount()==1 && !mr.getIsDeleted())
                ){
                    throw new CustomException("Matchup 참가 요청하신 경기 날짜는 이전에 참가 요청하신 게시글의 경기 날짜와 겹칩니다. 확인 후 다시 작성해주세요");
                }
                // 취소 요청 승인: 취소 요청 승인이 되어, 참가 요청 등록을 허용해도됨
           /* else if(
                    (mr.getMatchupStatus()==Status.CANCELREQUESTED && mr.getMatchupRequestSubmittedCount()==1 && mr.getMatchupCancelSubmittedCount()==1 && mr.getIsDeleted())||
                    (mr.getMatchupStatus()==Status.CANCELREQUESTED && mr.getMatchupRequestSubmittedCount()==2 && mr.getMatchupCancelSubmittedCount()==1 && mr.getIsDeleted())
            ){
                continue;
            }*/
                // 취소 요청 반려: 다른 곳에서 겹치는 시간대에 참가 요청이 승인 됐기 때문에, 참가 요청 등록을 허용하면 안됨
                else if(
                        (mr.getMatchupStatus()==Status.APPROVED && mr.getMatchupRequestSubmittedCount()==1 && mr.getMatchupCancelSubmittedCount()==1 && !mr.getIsDeleted())||
                                (mr.getMatchupStatus()==Status.APPROVED && mr.getMatchupRequestSubmittedCount()==2 && mr.getMatchupCancelSubmittedCount()==1 && !mr.getIsDeleted())
                ){
                    throw new CustomException("Matchup 참가 요청하신 경기 날짜는 이전에 참가 요청하신 게시글의 경기 날짜와 겹칩니다. 확인 후 다시 작성해주세요");

                }
                // 경기 시작 이전만 고려하면 되므로 자동 참가는 고려하지 않음
            }


            findMatchupRequest.updateRequestMangementInfo(Status.PENDING, 2, 0, false);
            findMatchupRequest.update(reqMatchupRequestEditDto.getSelfIntro(), reqMatchupRequestEditDto.getParticipantCount());

            /*
             * 3. 작성자에게 알림 보내기
             * */
            notificationService.sendNotificationWithoutMail(findMatchupRequest.getMatchupBoard().getWriter() , "[참가 재요청]"+findMatchupRequest.getMember().getMemberName()+"님이 참가 재요청 했습니다.", "/matchup/request/board?"+"board-id="+findMatchupRequest.getMatchupBoard().getId());

        } else{
            throw new CustomException("Matchup 수정하기 또는 재요청이 불가능합니다. 요청 이력을 참고해주세요.");
        }

    }



    // 참가 취소
    @Transactional
    public void cancelMatchupRequestBeforeApproval(Long boardId, Long requestId, CustomUser user) {
        MatchupRequest findMatchupRequest = matchupRequestRepository.findMatchupRequestWithMatchupBoardByRequestIdAndBoardIDAndApplicantId(requestId, boardId, user.getMember().getId()).orElseThrow(()->new CustomException("Matchup 요청을 찾을 수 없습니다."));

        //날짜
        if(findMatchupRequest.getMatchupBoard().getMatchDatetime().isBefore(LocalDateTime.now())){
            throw new CustomException("Matchup 경기 시작 시간이 지나 참가 요청할 수 없습니다.");
        }
        //상태
        if(findMatchupRequest.getMatchupStatus()==Status.PENDING && findMatchupRequest.getMatchupRequestSubmittedCount()==1 && findMatchupRequest.getMatchupCancelSubmittedCount() ==0 && !findMatchupRequest.getIsDeleted()){

            findMatchupRequest.updateRequestMangementInfo(Status.PENDING, findMatchupRequest.getMatchupRequestSubmittedCount(), findMatchupRequest.getMatchupCancelSubmittedCount(), !findMatchupRequest.getIsDeleted());

        } else if(findMatchupRequest.getMatchupStatus()==Status.PENDING && findMatchupRequest.getMatchupRequestSubmittedCount()==2 && findMatchupRequest.getMatchupCancelSubmittedCount() ==0 && !findMatchupRequest.getIsDeleted()){

            findMatchupRequest.updateRequestMangementInfo(Status.PENDING, findMatchupRequest.getMatchupRequestSubmittedCount(), findMatchupRequest.getMatchupCancelSubmittedCount(), !findMatchupRequest.getIsDeleted());

        } else{
            throw new CustomException("Matchup 참가 요청 취소가 불가능합니다. 이력을 참고해주세요.");
        }

        /*
         * 작성자에게 알림 보내기
         * */
        notificationService.sendNotificationWithoutMail(findMatchupRequest.getMatchupBoard().getWriter() , "[참가 요청 취소]"+findMatchupRequest.getMember().getMemberName()+"님의 참가 요청이 취소되었습니다.", "/matchup/request/board?"+"board-id="+findMatchupRequest.getMatchupBoard().getId());
    }



    /*
    * 재요청이 가능한지 검사 후 기존 요청 정보 반환
    * */
    @Transactional(readOnly = true)
    public ResMatchupRequestDto checkRetryMatchupRequest(Long boardId, Long requestId, CustomUser user) {

        MatchupRequest findMatchupRequest = matchupRequestRepository.findMatchupRequestWithMatchupBoardByRequestIdAndBoardIDAndApplicantId(requestId, boardId, user.getMember().getId()).orElseThrow(()->new CustomException("Matchup 요청을 찾을 수 없습니다."));

        //날짜
        if(findMatchupRequest.getMatchupBoard().getMatchDatetime().isBefore(LocalDateTime.now())){
            throw new CustomException("Matchup 경기 시작 시간이 지나 재요청할 수 없습니다.");
        }

        if(
            (findMatchupRequest.getMatchupStatus()==Status.PENDING && findMatchupRequest.getMatchupRequestSubmittedCount()==1 && findMatchupRequest.getMatchupCancelSubmittedCount()==0 && findMatchupRequest.getIsDeleted()) ||
            (findMatchupRequest.getMatchupStatus()==Status.DENIED && findMatchupRequest.getMatchupRequestSubmittedCount()==1 && findMatchupRequest.getMatchupCancelSubmittedCount()==0 && !findMatchupRequest.getIsDeleted())
        ) {

            return matchupRequestRepository.findResMatchupRequestDtoByRequestId(requestId).orElseThrow(()->new IllegalArgumentException("Matchup"+requestId+"번 요청은 없습니다."));

        }else{
            throw new CustomException("Matchup 현재 재요청이 불가능합니다. 요청 이력을 확인해주세요.");
        }

    }



    // 승인 취소 요청
    @Transactional
    public void matchupRequestCancelAfterApproval(Long boardId, Long requestId, CustomUser user) {
        MatchupRequest findMatchupRequest = matchupRequestRepository.findMatchupRequestWithMatchupBoardByRequestIdAndBoardIDAndApplicantId(requestId, boardId, user.getMember().getId()).orElseThrow(()->new CustomException("Matchup 요청을 찾을 수 없습니다."));

        //날짜
        if(findMatchupRequest.getMatchupBoard().getMatchDatetime().isBefore(LocalDateTime.now())){
            throw new CustomException("Matchup 경기 시작 시간이 지나 승인 취소 요청할 수 없습니다.");
        }

        //상태
        if(findMatchupRequest.getMatchupStatus()==Status.APPROVED && findMatchupRequest.getMatchupRequestSubmittedCount()==2 && findMatchupRequest.getMatchupCancelSubmittedCount()==0 && !findMatchupRequest.getIsDeleted()){

            findMatchupRequest.updateRequestMangementInfo(Status.CANCELREQUESTED, findMatchupRequest.getMatchupRequestSubmittedCount(), findMatchupRequest.getMatchupCancelSubmittedCount()+1, findMatchupRequest.getIsDeleted());

        }else if(findMatchupRequest.getMatchupStatus()==Status.APPROVED && findMatchupRequest.getMatchupRequestSubmittedCount()==1 && findMatchupRequest.getMatchupCancelSubmittedCount()==0 && !findMatchupRequest.getIsDeleted()){

            findMatchupRequest.updateRequestMangementInfo(Status.CANCELREQUESTED, findMatchupRequest.getMatchupRequestSubmittedCount(), findMatchupRequest.getMatchupCancelSubmittedCount()+1, findMatchupRequest.getIsDeleted());

        }else{
            throw new CustomException("Matchup 승인 취소 요청이 불가능합니다. 이력을 참고해주세요.");
        }

        /*
         * 작성자에게 알림 보내기
         * */
        notificationService.sendNotificationWithoutMail(findMatchupRequest.getMatchupBoard().getWriter() , "[승인 취소 요청]"+findMatchupRequest.getMember().getMemberName()+"님이 승인 취소 요청 했습니다.", "/matchup/request/board?"+"board-id="+findMatchupRequest.getMatchupBoard().getId());

    }

    // 참가 요청 승인, 승인 취소 요청 승인
    @Transactional
    public void approveMatchupRequest(Long boardId, Long requestId, CustomUser user) {
        MatchupRequest findMatchupRequest = matchupRequestRepository.findMatchupRequestWithMatchupBoardByRequestIdAndBoardIDAndWriterId(requestId, boardId, user.getMember().getId()).orElseThrow(()->new CustomException("Matchup 요청을 찾을 수 없습니다."));

        //날짜
        if(findMatchupRequest.getMatchupBoard().getMatchDatetime().isBefore(LocalDateTime.now())){
            throw new CustomException("Matchup 경기 시작 시간이 지나 승인할 수 없습니다.");
        }

        // 참가 요청 승인: 상태 확인 → 인원 수 체크 → 업데이트

        // 재요청에 대한 승인
        if(findMatchupRequest.getMatchupStatus()==Status.PENDING && findMatchupRequest.getMatchupRequestSubmittedCount()==2 && findMatchupRequest.getMatchupCancelSubmittedCount()==0 && !findMatchupRequest.getIsDeleted()){


            if(findMatchupRequest.getMatchupBoard().getCurrentParticipantCount()+findMatchupRequest.getParticipantCount()>findMatchupRequest.getMatchupBoard().getMaxParticipants()){

                throw new CustomException("Matchup 모집 인원이 초과됩니다.");
            }else{

                findMatchupRequest.updateRequestMangementInfo(Status.APPROVED, findMatchupRequest.getMatchupRequestSubmittedCount(), findMatchupRequest.getMatchupCancelSubmittedCount(), findMatchupRequest.getIsDeleted());

                findMatchupRequest.getMatchupBoard().increaseCurrentParticipantCount(findMatchupRequest.getParticipantCount());

                chatService.addParticipantToGroupChat(findMatchupRequest.getMatchupBoard().getChatRoom(), findMatchupRequest.getMember());

                /*
                 * 신청자에게 알림 보내기
                 * */
                notificationService.sendNotificationWithoutMail(findMatchupRequest.getMember() , "[참가 요청 승인]"+findMatchupRequest.getMatchupBoard().getWriter().getMemberName()+"님이 참가 요청을 승인했습니다. 단체 채팅방에 초대되었습니다. 지금 바로 확인해보세요!", "/chat/group/room?"+"roomId="+findMatchupRequest.getMatchupBoard().getChatRoom().getId());

            }

        //최초 요청에 대한 승인
        }else if(findMatchupRequest.getMatchupStatus()==Status.PENDING && findMatchupRequest.getMatchupRequestSubmittedCount()==1 && findMatchupRequest.getMatchupCancelSubmittedCount()==0 && !findMatchupRequest.getIsDeleted()){

            if(findMatchupRequest.getMatchupBoard().getCurrentParticipantCount()+findMatchupRequest.getParticipantCount()>findMatchupRequest.getMatchupBoard().getMaxParticipants()){
                throw new CustomException("Matchup 모집 인원이 초과됩니다.");

            }else{
                findMatchupRequest.updateRequestMangementInfo(Status.APPROVED, findMatchupRequest.getMatchupRequestSubmittedCount(), findMatchupRequest.getMatchupCancelSubmittedCount(), findMatchupRequest.getIsDeleted());

                findMatchupRequest.getMatchupBoard().increaseCurrentParticipantCount(findMatchupRequest.getParticipantCount());

                chatService.addParticipantToGroupChat(findMatchupRequest.getMatchupBoard().getChatRoom(), findMatchupRequest.getMember());

                /*
                 * 신청자에게 알림 보내기
                 * */
                notificationService.sendNotificationWithoutMail(findMatchupRequest.getMember() , "[참가 요청 승인]"+findMatchupRequest.getMatchupBoard().getWriter().getMemberName()+"님이 참가 요청을 승인했습니다. 단체 채팅방에 초대되었습니다. 지금 바로 확인해보세요!", "/chat/group/room?"+"roomId="+findMatchupRequest.getMatchupBoard().getChatRoom().getId());

            }
        }

        // 승인 취소 요청에 대한 승인: 승인 상태 확인 → 인원 수 체크 → 업데이트

        // 재요청 → 승인 → 승인 취소 요청 → 승인
        else if(findMatchupRequest.getMatchupStatus()==Status.CANCELREQUESTED && findMatchupRequest.getMatchupRequestSubmittedCount()==2 && findMatchupRequest.getMatchupCancelSubmittedCount()==1 && !findMatchupRequest.getIsDeleted()){

                findMatchupRequest.updateRequestMangementInfo(Status.CANCELREQUESTED, findMatchupRequest.getMatchupRequestSubmittedCount(), findMatchupRequest.getMatchupCancelSubmittedCount(), !findMatchupRequest.getIsDeleted());

                findMatchupRequest.getMatchupBoard().decreaseCurrentParticipantCount(findMatchupRequest.getParticipantCount());

                chatService.removeParticipantToGroupChat(findMatchupRequest.getMatchupBoard().getChatRoom(), findMatchupRequest.getMember());

                /*
                 * 신청자에게 알림 보내기
                 * */
                notificationService.sendNotificationWithoutMail(findMatchupRequest.getMember() , "[취소 요청 승인]"+findMatchupRequest.getMatchupBoard().getWriter().getMemberName()+"님이 취소 요청 승인을 했습니다.", "/matchup/request/detail?"+"request-id="+findMatchupRequest.getId());


//                messageTemplate.convertAndSendToUser(findMatchupRequest.getMember().getMemberEmail(),"/queue/errors","Chat 더 이상 그룹 채팅할 수 없습니다.");
            ResChatErrorDto resChatErrorDto = ResChatErrorDto.builder()
                    .errorMessage("Chat 더 이상 그룹 채팅할 수 없습니다.")
                    .receiverEmail(findMatchupRequest.getMember().getMemberEmail())
                    .build();
            redisPubSubService.publish("error",resChatErrorDto);


        // 최초 요청 → 승인 → 승인 취소 요청 → 승인
        } else if(findMatchupRequest.getMatchupStatus()==Status.CANCELREQUESTED && findMatchupRequest.getMatchupRequestSubmittedCount()==1 && findMatchupRequest.getMatchupCancelSubmittedCount()==1 && !findMatchupRequest.getIsDeleted()){

            findMatchupRequest.updateRequestMangementInfo(Status.CANCELREQUESTED, findMatchupRequest.getMatchupRequestSubmittedCount(), findMatchupRequest.getMatchupCancelSubmittedCount(), !findMatchupRequest.getIsDeleted());

            findMatchupRequest.getMatchupBoard().decreaseCurrentParticipantCount(findMatchupRequest.getParticipantCount());

            chatService.removeParticipantToGroupChat(findMatchupRequest.getMatchupBoard().getChatRoom(), findMatchupRequest.getMember());

            /*
             * 신청자에게 알림 보내기
             * */
            notificationService.sendNotificationWithoutMail(findMatchupRequest.getMember() , "[취소 요청 승인]"+findMatchupRequest.getMatchupBoard().getWriter().getMemberName()+"님이 취소 요청 승인을 했습니다.", "/matchup/request/detail?"+"request-id="+findMatchupRequest.getId());

//            messageTemplate.convertAndSendToUser(findMatchupRequest.getMember().getMemberEmail(),"/queue/errors","Chat 더 이상 그룹 채팅할 수 없습니다.");
            ResChatErrorDto resChatErrorDto = ResChatErrorDto.builder()
                    .errorMessage("Chat 더 이상 그룹 채팅할 수 없습니다.")
                    .receiverEmail(findMatchupRequest.getMember().getMemberEmail())
                    .build();
            redisPubSubService.publish("error",resChatErrorDto);

        } else{
            throw new CustomException("Matchup 현재 승인이 불가능합니다. 요청 목록을 참고해주세요.");
        }
    }

    // 참가 요청 반려, 승인 취소 요청 반려
    @Transactional
    public void denyMatchupRequest(Long boardId, Long requestId, CustomUser user) {
        MatchupRequest findMatchupRequest = matchupRequestRepository.findMatchupRequestWithMatchupBoardByRequestIdAndBoardIDAndWriterId(requestId, boardId, user.getMember().getId()).orElseThrow(()->new CustomException("Matchup 요청을 찾을 수 없습니다."));

        //날짜
        if(findMatchupRequest.getMatchupBoard().getMatchDatetime().isBefore(LocalDateTime.now())){
            throw new CustomException("Matchup 경기 시작 시간이 지나 반려할 수 없습니다.");
        }

        //인원수
        //상태

        // 참가 요청 반려: 상태 확인 → 업데이트

        // 재요청에 대한 반려
        if(findMatchupRequest.getMatchupStatus()==Status.PENDING && findMatchupRequest.getMatchupRequestSubmittedCount()==2 && findMatchupRequest.getMatchupCancelSubmittedCount()==0 && !findMatchupRequest.getIsDeleted()){

            findMatchupRequest.updateRequestMangementInfo(Status.DENIED, findMatchupRequest.getMatchupRequestSubmittedCount(), findMatchupRequest.getMatchupCancelSubmittedCount(), findMatchupRequest.getIsDeleted());

            /*
             * 신청자에게 알림 보내기
             * */
            notificationService.sendNotificationWithoutMail(findMatchupRequest.getMember() , "[참가 요청 반려]"+findMatchupRequest.getMatchupBoard().getWriter().getMemberName()+"님이 참가 요청 반려 했습니다.", "/matchup/request/detail?"+"request-id="+findMatchupRequest.getId());

        // 최초 요청에 대한 반려
        }else if(findMatchupRequest.getMatchupStatus()==Status.PENDING && findMatchupRequest.getMatchupRequestSubmittedCount()==1 && findMatchupRequest.getMatchupCancelSubmittedCount()==0 && !findMatchupRequest.getIsDeleted()){

            findMatchupRequest.updateRequestMangementInfo(Status.DENIED, findMatchupRequest.getMatchupRequestSubmittedCount(), findMatchupRequest.getMatchupCancelSubmittedCount(), findMatchupRequest.getIsDeleted());

            /*
             * 신청자에게 알림 보내기
             * */
            notificationService.sendNotificationWithoutMail(findMatchupRequest.getMember() , "[참가 요청 반려]"+findMatchupRequest.getMatchupBoard().getWriter().getMemberName()+"님이 참가 요청 반려 했습니다.", "/matchup/request/detail?"+"request-id="+findMatchupRequest.getId());

        }

        // 승인 취소 요청에 대한 반려: 승인 상태 확인 →  업데이트

        // 재요청 → 승인 → 승인 취소 요청 → 반려
        else if(findMatchupRequest.getMatchupStatus()==Status.CANCELREQUESTED && findMatchupRequest.getMatchupRequestSubmittedCount()==2 && findMatchupRequest.getMatchupCancelSubmittedCount()==1 && !findMatchupRequest.getIsDeleted()){

            findMatchupRequest.updateRequestMangementInfo(Status.APPROVED, findMatchupRequest.getMatchupRequestSubmittedCount(), findMatchupRequest.getMatchupCancelSubmittedCount(), findMatchupRequest.getIsDeleted());

            /*
             * 신청자에게 알림 보내기
             * */
            notificationService.sendNotificationWithoutMail(findMatchupRequest.getMember() , "[취소 요청 반려]"+findMatchupRequest.getMatchupBoard().getWriter().getMemberName()+"님이 취소 요청 반려 했습니다.", "/matchup/request/detail?"+"request-id="+findMatchupRequest.getId());


        // 최초 요청 → 승인 → 승인 취소 요청 → 반려
        } else if(findMatchupRequest.getMatchupStatus()==Status.CANCELREQUESTED && findMatchupRequest.getMatchupRequestSubmittedCount()==1 && findMatchupRequest.getMatchupCancelSubmittedCount()==1 && !findMatchupRequest.getIsDeleted()){

            findMatchupRequest.updateRequestMangementInfo(Status.APPROVED, findMatchupRequest.getMatchupRequestSubmittedCount(), findMatchupRequest.getMatchupCancelSubmittedCount(), findMatchupRequest.getIsDeleted());

            /*
             * 신청자에게 알림 보내기
             * */
            notificationService.sendNotificationWithoutMail(findMatchupRequest.getMember() , "[취소 요청 반려]"+findMatchupRequest.getMatchupBoard().getWriter().getMemberName()+"님이 취소 요청 반려 했습니다.", "/matchup/request/detail?"+"request-id="+findMatchupRequest.getId());

        } else{
            throw new CustomException("Matchup 현재 반려가 불가능합니다. 요청 목록을 참고해주세요.");

        }
    }
    // 삭제
}

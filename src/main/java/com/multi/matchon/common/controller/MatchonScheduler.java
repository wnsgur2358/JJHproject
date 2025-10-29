package com.multi.matchon.common.controller;


import com.multi.matchon.chat.service.ChatService;
import com.multi.matchon.matchup.service.MatchupBoardService;
import com.multi.matchon.matchup.service.MatchupRatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;



@Component
@Slf4j
@RequiredArgsConstructor
public class MatchonScheduler {
    private final MatchupBoardService matchupBoardService;
    private final MatchupRatingService matchupRatingService;
    private final ChatService chatService;




    /*
    * 경기 종료 후 작성자와 참가자들이 서로서로에 대해서 매너온도 평가를 할 수 있도록 자동 세팅
    * */
    @Scheduled(cron="0 * * * * *")
    public void setMannerTemperatureAutoSetting(){

        Integer result = matchupRatingService.setMannerTemperatureAutoSetting();
        log.info("자동 평가 세팅 갯수: {}", result);
    }


    /*
    * 경기 3시간 전에 작성자와 참가자들에게 자동 알림
    * */
    @Scheduled(cron="0 * * * * *")
    public void setMatchNotificationBeforeStart(){
        Long result = matchupBoardService.notifyAllParticipantsBeforeStart();
        log.info("경기 시작 전 자동 알림 갯수: {}",result);
    }

    /*
    * 매 시간 마다  그룹 채팅과 참여자들을 is_deleted처리
    * */
    //@Scheduled(cron="0 0 * * * *")
    @Scheduled(cron="0 0 * * * *")
    public void removeGroupChatsAfterThreeDaysOfMatch(){
        Integer result = chatService.removeGroupChatsAfterThreeDaysOfMatch();
        log.info("삭제한 Matchup 그룹 참여자 수: {}",result);
    }
}

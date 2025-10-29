package com.multi.matchon.chat.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.multi.matchon.chat.config.StompHandler;
import com.multi.matchon.chat.dto.req.ReqChatDto;
import com.multi.matchon.chat.dto.res.ResChatDto;
import com.multi.matchon.chat.exception.custom.ChatBlockException;
import com.multi.matchon.chat.exception.custom.NotChatParticipantException;
import com.multi.matchon.chat.service.ChatService;
import com.multi.matchon.chat.service.RedisPubSubService;
import com.multi.matchon.common.auth.dto.CustomUser;
import com.multi.matchon.common.exception.custom.CustomException;
import com.multi.matchon.member.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@Slf4j
@RequiredArgsConstructor
public class StompController {

    private final SimpMessageSendingOperations messageTemplate;
    private final ChatService chatService;

    // redis 적용
    private final RedisPubSubService redisPubSubService;



    @MessageMapping("/{roomId}")
    public void sendMessage(@DestinationVariable("roomId") Long roomId, ReqChatDto reqChatDto, Principal principal){
        String senderEmail = "none";
        String senderName = "none";

        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) principal;

        CustomUser user = (CustomUser) authentication.getPrincipal();
        Member sender = user.getMember();
        senderEmail = sender.getMemberEmail();
        senderName = sender.getMemberName();

//        if (principal instanceof UsernamePasswordAuthenticationToken authentication) {
//            Object principalObj = authentication.getPrincipal();
//            if (principalObj instanceof CustomUser customUser) {
//                Member sender = customUser.getMember();
//                senderEmail = sender.getMemberEmail(); // 예시
//                senderName = sender.getMemberName();
//
//            }
//        }

        log.info("message: {}",reqChatDto.getContent());
        ResChatDto resChatDto = ResChatDto.builder()
                .senderEmail(senderEmail)
                .senderName(senderName)
                .content(reqChatDto.getContent())
                .createdDate(LocalDateTime.now())
                .build();
        StompHandler.authContext.set((Authentication) principal);

        chatService.checkBlock(roomId);

        chatService.checkRoomParticipant(user, roomId);

        chatService.saveMessage(roomId, resChatDto);

        //messageTemplate.convertAndSend("/topic/"+roomId,resChatDto);

        // redis 변경

        //try {
            resChatDto.setRoomId(roomId);

//            ObjectMapper objectMapper = new ObjectMapper();
//
//            String message = objectMapper.writeValueAsString(resChatDto);
            redisPubSubService.publish("chat",resChatDto);

//        } catch (JsonProcessingException e) {
//            throw new CustomException(e.getMessage());
//        }


    }





//    @MessageExceptionHandler
//    public ResChatDto test(Exception e){
//        ResChatDto resChatDto = ResChatDto.builder()
//                .senderEmail("system@matchon.com")
//                .senderName("system")
//                .content(e.getMessage())
//                .build();
//
//        log.info("error: {}",e.getMessage());
//        return resChatDto;
//    }


    @MessageExceptionHandler
    public ResChatDto handleChatBlockException(ChatBlockException e){
        ResChatDto resChatDto = ResChatDto.builder()
                .content(e.getMessage())
                .exceptionName(e.getClass().getSimpleName())
                .createdDate(LocalDateTime.now())
                .build();

        log.info("error: {}",e.getMessage());
        return resChatDto;
    }


    @MessageExceptionHandler
    public void handleChatBlockException(NotChatParticipantException e, Principal principal){
        ResChatDto resChatDto = ResChatDto.builder()
                .content(e.getMessage())
                .exceptionName(e.getClass().getSimpleName())
                .createdDate(LocalDateTime.now())
                .build();

        log.info("error: {}",e.getMessage());
        //return resChatDto;

        messageTemplate.convertAndSendToUser(principal.getName(),"/queue/errors",resChatDto);

//        resChatDto.setReceiverEmail(principal.getName());
//        redisPubSubService.publish("chat-error", resChatDto);
    }


}

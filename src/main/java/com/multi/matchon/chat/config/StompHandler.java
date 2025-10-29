package com.multi.matchon.chat.config;

import com.multi.matchon.chat.service.ChatService;
import com.multi.matchon.common.auth.dto.CustomUser;
import com.multi.matchon.common.auth.service.CustomUserDetailsService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.file.AccessDeniedException;
import java.security.Principal;

@Component
@Slf4j
public class StompHandler implements ChannelInterceptor {


    private final SecretKey secretKey;
    private final CustomUserDetailsService customUserDetailsService;
    public static final ThreadLocal<Authentication> authContext = new ThreadLocal<>();
    private final ChatService chatService;

    public StompHandler(@Value("${jwt.secret}") String secret, CustomUserDetailsService customUserDetailsService, ChatService chatService) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.customUserDetailsService = customUserDetailsService;
        this.chatService = chatService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);


        if(StompCommand.CONNECT == accessor.getCommand()){

            log.info("connect 토큰 검증 시작");

            String accessToken = accessor.getFirstNativeHeader("Authorization");
            String token = accessToken.substring(7);

//            String email = null;
//            try{
//
//            }catch (Exception e){
//                throw new CustomException("CONNECT"+e.getMessage());
//            }
            String email = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();


            CustomUser userDetails = (CustomUser) customUserDetailsService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            accessor.setUser(authentication);

            log.info("connect 토큰 검증 완료");

            //return null;

        }else if(StompCommand.SUBSCRIBE == accessor.getCommand()){

            log.info("subscribe 시작");

//            String accessToken = accessor.getFirstNativeHeader("Authorization");
//            String token = accessToken.substring(7);
//
//            String email = Jwts.parserBuilder()
//                    .setSigningKey(secretKey)
//                    .build()
//                    .parseClaimsJws(token)
//                    .getBody()
//                    .getSubject();
//
//            CustomUser userDetails = (CustomUser) customUserDetailsService.loadUserByUsername(email);
//
//            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                    userDetails, null, userDetails.getAuthorities());
//            Long roomId = Long.parseLong(accessor.getDestination().split("/")[2]);
//
//            accessor.setUser(authentication);

            //log.info("subscribe 완료");

//            if(!chatService.isRoomParticipant(email, roomId)){ // error발생  채팅 탭 닫히도록 함
//                throw new CustomException("Chat 해당 채팅방에 참여자가 아닙니다.");
//            }

            //return null;

        }else if(StompCommand.SEND == accessor.getCommand()){
            log.info("SEND Stage");
//            String destination = accessor.getDestination();
//            if(destination!=null && destination.startsWith("/publish"))
//                throw new MessagingException("blockException"); // 프론트 측에 보낼 수 있다.

//            if(3==3)
//                throw new MessagingException("blockException"); // 프론트 측에 보낼 수 있다.

              //return null;

        }else if(StompCommand.UNSUBSCRIBE == accessor.getCommand()){
            log.info("UNSUBSCRIBE Stage");
            //return null;

        }else if(StompCommand.DISCONNECT == accessor.getCommand()){
            log.info("DISCONNECT Stage");
            authContext.remove();
            //return null;

        }
        //return null;

        return message;
    }
}

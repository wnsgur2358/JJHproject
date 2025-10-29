package com.multi.matchon.chat.exception;


import com.multi.matchon.common.exception.custom.CustomException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import java.nio.charset.StandardCharsets;


@Slf4j
@RequiredArgsConstructor
@Configuration
public class CustomStompErrorHandler extends StompSubProtocolErrorHandler {

    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
        Throwable exCause = ex.getCause() != null ? ex.getCause() : ex;

        log.error("STOMP 예외 발생: {}", exCause.getMessage());

        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);

        // 예외 메시지 설정
        if (exCause instanceof JwtException) {
            accessor.setMessage("Chat Jwt 인증 오류 발생");
        } else if (exCause instanceof UsernameNotFoundException) {
            accessor.setMessage("Chat 이메일이 정확하지 않습니다.");
        } else if (exCause instanceof CustomException) {
            accessor.setMessage(exCause.getMessage());
        } else {
            accessor.setMessage("알 수 없는 오류: " + exCause.getMessage());
        }

        // 커스텀 헤더 추가 (프론트에서 구분 용도)
        accessor.setNativeHeader("error-source", resolveErrorSource(clientMessage));

        accessor.setLeaveMutable(true);

        return MessageBuilder.createMessage(
                accessor.getMessage().getBytes(StandardCharsets.UTF_8), // body와 메시지 동일하게
                accessor.getMessageHeaders()
        );
    }

    private String resolveErrorSource(Message<byte[]> clientMessage) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(clientMessage);
        if (accessor != null && accessor.getCommand() != null) {
            return accessor.getCommand().name(); // CONNECT, SUBSCRIBE 등
        }
        return "UNKNOWN";
    }
}

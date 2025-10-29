package com.multi.matchon.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.multi.matchon.chat.dto.req.ReqChatDto;
import com.multi.matchon.chat.dto.res.ResChatDto;
import com.multi.matchon.chat.dto.res.ResChatErrorDto;
import com.multi.matchon.common.dto.res.ResNotificationDto;
import com.multi.matchon.common.exception.custom.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisPubSubService implements MessageListener {

    //private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessageSendingOperations messageTemplate;

    public void publish(String channel, Object message){
        redisTemplate.convertAndSend(channel, message);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {

        String payload = new String(message.getBody());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // ISO 8601 사용

        try {
            if("noti".equals(new String(pattern))){
                ResNotificationDto resNotiDto = objectMapper.readValue(payload, ResNotificationDto.class);
                messageTemplate.convertAndSendToUser(resNotiDto.getReceiverEmail(), "/notify",resNotiDto);
            }else if("error".equals(new String(pattern))) {
                ResChatErrorDto resChatErrorDto = objectMapper.readValue(payload, ResChatErrorDto.class);
                messageTemplate.convertAndSendToUser(resChatErrorDto.getReceiverEmail(),"/queue/errors",resChatErrorDto.getErrorMessage());
            } else{
                ResChatDto resChatDto = objectMapper.readValue(payload, ResChatDto.class);
                messageTemplate.convertAndSend("/topic/" + resChatDto.getRoomId(), resChatDto);
            }



        } catch (JsonProcessingException e) {
            throw new CustomException(e.getMessage());
        }
    }
}

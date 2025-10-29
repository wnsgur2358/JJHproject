package com.multi.matchon.chat.config;

import com.multi.matchon.chat.exception.CustomStompErrorHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final StompHandler stompHandler;
    private final CustomStompErrorHandler customStompErrorHandler;


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/connect")
                .withSockJS();
        registry.setErrorHandler(customStompErrorHandler);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        registry.setApplicationDestinationPrefixes("/publish");

        registry.enableSimpleBroker("/topic","/queue","/user","/notify");

        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {

        registration.interceptors(stompHandler);
    }

}

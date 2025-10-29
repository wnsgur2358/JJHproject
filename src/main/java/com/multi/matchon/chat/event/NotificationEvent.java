package com.multi.matchon.chat.event;


import com.multi.matchon.member.domain.Member;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class NotificationEvent extends ApplicationEvent {

    private final Member receiver;
    private final String message;
    private final String targetUrl;


    public NotificationEvent(Object source, Member receiver, String message, String targetUrl) {
        super(source);
        this.receiver = receiver;
        this.message = message;
        this.targetUrl = targetUrl;
    }
}

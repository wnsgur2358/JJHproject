package com.multi.matchon.chat.event;


import com.multi.matchon.common.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class NotificationEventListener {
    private final NotificationService notificationService;

    @EventListener
    public void handleNotificationEvent(NotificationEvent event){
        notificationService.sendNotification(event.getReceiver(), event.getMessage(),event.getTargetUrl());
    }
}

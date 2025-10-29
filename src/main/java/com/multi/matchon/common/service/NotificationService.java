package com.multi.matchon.common.service;

import com.multi.matchon.chat.service.RedisPubSubService;
import com.multi.matchon.common.auth.dto.CustomUser;

import com.multi.matchon.common.auth.service.MailService;
import com.multi.matchon.common.domain.Notification;
import com.multi.matchon.common.dto.res.ResNotificationDto;
import com.multi.matchon.common.dto.res.ResReadNotificationDto;
import com.multi.matchon.common.exception.custom.ApiCustomException;
import com.multi.matchon.common.repository.NotificationRepository;

import com.multi.matchon.member.domain.Member;
import com.multi.matchon.member.domain.MemberRole;
import com.multi.matchon.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;
    private final SimpMessageSendingOperations messageTemplate;
    private final MailService mailService;
    private final RedisPubSubService redisPubSubService;


    // 조회


    /*
    * 읽지 않은 정보 전달하기
    * */
    @Transactional(readOnly = true)
    public List<ResNotificationDto> findAllByMemberAndUnreadFalse(CustomUser user) {

        // 읽지 않은 알림 가져오기
       return notificationRepository.findAllByMemberAndUnreadFalse(user.getMember());


    }

    /*
    * 읽은 메시지 정보 전달하기
    * */
    @Transactional(readOnly = true)
    public List<ResReadNotificationDto> findAllByMemberAndUnreadTrue(CustomUser user) {
        return notificationRepository.findAllByMemberAndUnreadTrue(user.getMember());
    }


    /*
     * 알림 읽음 처리
     */
    @Transactional
    public String updateUnreadNotification(Long notificationId, CustomUser user) {
        Notification notification = notificationRepository
                .findTargetUrlByNotificationIdAndMember(notificationId, user.getMember())
                .orElseThrow(() -> new ApiCustomException("Notification not found"));

        notification.updateIsRead(true);

        return notification.getTargetUrl();
    }

    /*
     * 알림 전송 (저장 + 실시간 + 메일)
     */
    @Transactional
    public void sendNotification(Member receiver, String message, String targetUrl) {
        // 1. DB 저장
        Notification notification = Notification.builder()
                .receivedMember(receiver)
                .notificationMessage(message)
                .targetUrl(targetUrl)
                .isRead(false)
                .build();
        notificationRepository.save(notification);

        // 2. WebSocket 실시간 전송
        ResNotificationDto dto = ResNotificationDto.builder()
                .notificationId(notification.getId()) // DTO 필드명에 맞춤
                .notificationMessage(notification.getNotificationMessage())
                .createdDate(notification.getCreatedDate())
                .build();
//              messageTemplate.convertAndSendToUser(
//                receiver.getMemberEmail(), "/notify", dto
//        );

        // redis용
        dto.setReceiverEmail(receiver.getMemberEmail());

        redisPubSubService.publish("noti",dto);


        // 3. 이메일 수신 동의 시에만 전송
        if (Boolean.TRUE.equals(receiver.getEmailAgreement())) {
            mailService.sendNotificationEmail(
                    receiver.getMemberEmail(),
                    "[📢 알림 도착] " + message,
                    mailService.buildNotificationBody(message, targetUrl)
            );
        }
    }

    // 관리자용 알림
    @Transactional
    public void notifyAdmins(String content, String url, Member sender) {
        List<Member> admins = memberRepository.findByMemberRoleAndIsDeletedFalse(MemberRole.ADMIN);
        String senderName = sender.getMemberName();

        for (Member admin : admins) {
            sendNotificationWithoutMail(admin, content, url);

            // 메일 전송
            if (Boolean.TRUE.equals(admin.getEmailAgreement())) {
                mailService.sendAdminNotificationEmail(
                        admin.getMemberEmail(),
                        content,
                        mailService.buildAdminNotificationBody(senderName, content, url)
                );
            }
        }
    }

    @Transactional
    public void sendNotificationWithoutMail(Member receiver, String message, String targetUrl) {
        Notification notification = Notification.builder()
                .receivedMember(receiver)
                .notificationMessage(message)
                .targetUrl(targetUrl)
                .isRead(false)
                .build();
        notificationRepository.save(notification);

        ResNotificationDto dto = ResNotificationDto.builder()
                .notificationId(notification.getId())
                .notificationMessage(notification.getNotificationMessage())
                .createdDate(notification.getCreatedDate())
                .build();
//           messageTemplate.convertAndSendToUser(
//                receiver.getMemberEmail(), "/notify", dto
//        );

        // redis용
        dto.setReceiverEmail(receiver.getMemberEmail());

        redisPubSubService.publish("noti",dto);


    }


    /*
     * 이메일 본문 생성
     */
    private String buildEmailBody(String message, String targetUrl) {
        return """
            <h3>📬 새로운 알림이 도착했습니다.</h3>
            <p>%s</p>
            <br/>
            <a href="%s">👉 알림 바로가기</a>
            """.formatted(message, targetUrl);
    }


}


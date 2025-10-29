package com.multi.matchon.common.controller;


import com.multi.matchon.common.auth.dto.CustomUser;
import com.multi.matchon.common.dto.res.ApiResponse;
import com.multi.matchon.common.dto.res.ResNotificationDto;
import com.multi.matchon.common.dto.res.ResReadNotificationDto;
import com.multi.matchon.common.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationService notificationService;

    // 등록


    // 조회
    /*
    * 읽지 않은 메시지 조회
    * */
    @GetMapping("/get/unread")
    @ResponseBody
    public ResponseEntity<ApiResponse<List<ResNotificationDto>>> getUnreadNotification(@AuthenticationPrincipal CustomUser user){
        List<ResNotificationDto> resNotificationDtos = notificationService.findAllByMemberAndUnreadFalse(user);

        return ResponseEntity.ok(ApiResponse.ok(resNotificationDtos));
    }

    /*
    * 읽은 메시지 조회
    * */
    @GetMapping("/get/read")
    @ResponseBody
    public ResponseEntity<ApiResponse<List<ResReadNotificationDto>>> getReadNotification(@AuthenticationPrincipal CustomUser user){
        List<ResReadNotificationDto> resNotificationDtos = notificationService.findAllByMemberAndUnreadTrue(user);

        return ResponseEntity.ok(ApiResponse.ok(resNotificationDtos));
    }



    // 수정

    @GetMapping("/update/unread")
    @ResponseBody ResponseEntity<ApiResponse<String>> updateUnreadNotification(@RequestParam("notificationId") Long notificationId, @AuthenticationPrincipal CustomUser user){

        String targetUrl = notificationService.updateUnreadNotification(notificationId, user);

        return ResponseEntity.ok(ApiResponse.ok(targetUrl));
    }


    // 삭제
}

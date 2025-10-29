package com.multi.matchon.common.dto.res;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResReadNotificationDto {

    private Long notificationId;

    private String notificationMessage;

    private LocalDateTime createdDate;

    private String targetUrl;
}

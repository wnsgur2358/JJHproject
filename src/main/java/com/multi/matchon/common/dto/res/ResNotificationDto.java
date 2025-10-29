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
public class ResNotificationDto {

    private Long notificationId;

    private String notificationMessage;

    private LocalDateTime createdDate;

    private String receiverEmail;

    public void setReceiverEmail(String receiverEmail){

        this.receiverEmail = receiverEmail;

    }
}

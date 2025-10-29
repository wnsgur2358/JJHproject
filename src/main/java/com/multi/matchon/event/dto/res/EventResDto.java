package com.multi.matchon.event.dto.res;

import com.multi.matchon.common.domain.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventResDto {
    private Long id;
    private String eventTitle;
    private String memberName;
    private Status eventStatus;
    private LocalDateTime createdDate;


    public String getStatusLabel() {
        return switch (this.eventStatus) {
            case PENDING -> "대기중";
            case APPROVED -> "승인";
            case DENIED -> "반려";
            default -> "알 수 없음";
        };
    }
}

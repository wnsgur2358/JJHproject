package com.multi.matchon.event.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EventSummaryDto {
    private Long id;
    private String eventTitle;
    private String eventDescription;
    private String region;
    private String hostName;
    private String eventAddress;
    private String eventMethod;
    private String eventContact;
}

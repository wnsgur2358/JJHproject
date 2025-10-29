package com.multi.matchon.event.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class CalendarDayDto {
    private LocalDate date;
    private boolean currentMonth;
    private List<EventSummaryDto> events;

    public int getDayOfMonth() {
        return date.getDayOfMonth();
    }
}

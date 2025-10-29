package com.multi.matchon.common.dto.res;


import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageResponseDto<T> {
    private List<T> items; // 현재 페이지에 전달할 목록
    private PageInfoDto pageInfo; // 페이지 관련 정보를 담고 있는 DTO

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class PageInfoDto{
        private Integer page; //현재 페이지 번호
        private Integer size; // 현재 페이지 크기, getNumberOfElements()
        private Long totalElements; // 전체 데이터 수, getTotalElements()
        private Integer totalPages; // 전체 페이지 수, getTotalPages()
        private Boolean isFirst; // 첫 페이지 여부, isFirstPage()
        private Boolean isLast; // 마지막 페이지 여부, isLastPage()

    }

}

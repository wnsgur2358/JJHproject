package com.multi.matchon.common.dto.res;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
public class SportsTypeDto {
    private Long id;
    private String sportsTypeName;

}

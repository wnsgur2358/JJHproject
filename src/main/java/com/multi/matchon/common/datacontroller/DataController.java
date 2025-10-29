package com.multi.matchon.common.datacontroller;

import com.multi.matchon.common.domain.Positions;
import com.multi.matchon.common.domain.SportsType;
import com.multi.matchon.common.dto.res.PositionsDto;
import com.multi.matchon.common.dto.res.SportsTypeDto;
import com.multi.matchon.common.repository.PositionsRepository;
import com.multi.matchon.common.repository.SportsTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/common/datacontroller")
public class DataController {
    private final SportsTypeRepository sportsTypeRepository;
    private final PositionsRepository positionsRepository;

    @GetMapping("/sports-types")
    public List<SportsTypeDto> getSportsTypes() {
        return sportsTypeRepository.findAll().stream()
                .map(s -> new SportsTypeDto(s.getId(), s.getSportsTypeName().name()))
                .collect(Collectors.toList());
    }

//    @GetMapping("/positions")
//    public List<PositionsDto> getPositionsBySportsType(@RequestParam Long sportsTypeId) {
//        return positionsRepository.findBySportsTypeId(sportsTypeId).stream()
//                .map(p -> new PositionsDto(p.getId(), p.getPositionName().toString()))
//                .collect(Collectors.toList());
//    }

}

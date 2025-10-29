package com.multi.matchon.common.service;

import com.multi.matchon.common.domain.SportsType;
import com.multi.matchon.common.dto.res.SportsTypeDto;
import com.multi.matchon.common.repository.SportsTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.internal.resource.S3Resource;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CommonService{

    private final SportsTypeRepository sportsTypeRepository;

    public List<SportsTypeDto> findAllSportsType() {
        List<SportsType> sportsTypes = sportsTypeRepository.findAll();
        if(sportsTypes.isEmpty())
            throw new IllegalArgumentException("현재 등록된 스포츠 종목이 없습니다.");

        List<SportsTypeDto> sportsTypeDtos = new ArrayList<>();
        for(SportsType sportsType: sportsTypes){
            SportsTypeDto sportsTypeDto = new SportsTypeDto();
            sportsTypeDto.setId(sportsType.getId());
            sportsTypeDto.setSportsTypeName(sportsType.getSportsTypeName().toString());
            sportsTypeDtos.add(sportsTypeDto);
        }
        return sportsTypeDtos;
    }

}

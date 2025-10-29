package com.multi.matchon.stadium.service;

import com.multi.matchon.stadium.domain.Stadium;
import com.multi.matchon.stadium.repository.StadiumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StadiumService {

    private final StadiumRepository stadiumRepository;

    public List<Stadium> getAllStadiums() {
        return stadiumRepository.findAll();
    }
}

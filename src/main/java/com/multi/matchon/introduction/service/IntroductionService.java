package com.multi.matchon.introduction.service;

import com.multi.matchon.introduction.domain.Introduction;
import com.multi.matchon.introduction.repository.IntroductionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IntroductionService {

    private final IntroductionRepository introductionRepository;

    public List<List<Introduction>> getGroupedIntroductions(int groupSize) {
        List<Introduction> all = introductionRepository.findAll();
        List<List<Introduction>> grouped = new ArrayList<>();

        for (int i = 0; i < all.size(); i += groupSize) {
            grouped.add(all.subList(i, Math.min(i + groupSize, all.size())));
        }

        return grouped;
    }
}

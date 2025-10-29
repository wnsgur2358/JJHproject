package com.multi.matchon.stadium.controller;

import com.multi.matchon.stadium.domain.Stadium;
import com.multi.matchon.stadium.repository.StadiumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stadiums")
@RequiredArgsConstructor
public class StadiumApiController {

    private final StadiumRepository stadiumRepository;

    @GetMapping
    public List<Stadium> getAllStadiums() {
        return stadiumRepository.findAll();
    }

    @GetMapping("/nearby")
    public List<Stadium> getNearbyStadiums(@RequestParam double lat, @RequestParam double lng) {
        List<Stadium> all = stadiumRepository.findAll();

        return all.stream()
                .filter(s -> s.getLatitude() != null && s.getLongitude() != null)
                .peek(s -> s.setDistance(haversine(lat, lng, s.getLatitude(), s.getLongitude())))
                .sorted(Comparator.comparingDouble(Stadium::getDistance))
                .limit(5)
                .collect(Collectors.toList());
    }

    // 하버사인 공식
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // 지구 반지름(km)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    @GetMapping("/search")
    public List<Stadium> searchStadiums(@RequestParam String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        return stadiumRepository.findByStadiumAddressContainingIgnoreCase(keyword)
                .stream()
                .limit(10)
                .collect(Collectors.toList());
    }
}

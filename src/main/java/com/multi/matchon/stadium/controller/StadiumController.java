package com.multi.matchon.stadium.controller;

import com.multi.matchon.stadium.domain.Stadium;
import com.multi.matchon.stadium.service.StadiumService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class StadiumController {

    private final StadiumService stadiumService;

    @GetMapping("/stadium")
    public String showStadiumMap(Model model) {
        // 10개만 불러오고 싶다면 이걸로 대체 가능:
        // List<Stadium> stadiumList = stadiumService.getTop10Stadiums();
        List<Stadium> stadiumList = stadiumService.getAllStadiums();

        model.addAttribute("stadiumList", stadiumList);
        return "stadium/stadium";
    }

}

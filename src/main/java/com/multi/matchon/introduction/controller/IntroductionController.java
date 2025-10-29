package com.multi.matchon.introduction.controller;

import com.multi.matchon.introduction.service.IntroductionService;
import com.multi.matchon.introduction.domain.Introduction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class IntroductionController {

    private final IntroductionService introductionService;

    @GetMapping("/introduction")
    public String introductionPage(Model model) {
        List<List<Introduction>> introductionGroup = introductionService.getGroupedIntroductions(3);
        model.addAttribute("introductionGroup", introductionGroup);
        return "introduction/intbase";
    }
}

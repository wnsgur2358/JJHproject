package com.multi.matchon.common.controller;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;

@Controller
public class ViewController {

    @GetMapping({"/","/main"})
    public String mainPage(){
        return "main/main";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "redirectUrl", required = false) String redirectUrl,
                            Model model) {
        if (redirectUrl != null && !redirectUrl.trim().isEmpty() && redirectUrl.startsWith("/")) {
            model.addAttribute("redirectUrl", redirectUrl);
        } else {
            model.addAttribute("redirectUrl", "");  // 기본 빈 값
        }
        return "login/login";
    }


    @GetMapping("/signup")
    public String userSignup() {
        return "signup/signup";
    }

    @PostMapping("/error/chat")
    public ModelAndView showErrorPage(@RequestParam("error") String error, ModelAndView mv){
        mv.setViewName("common/error");
        mv.addObject("errorMessage", error);

        return mv;
    }

    @GetMapping("/error/authorities")
    public ModelAndView showErrorPageByAuthorities(ModelAndView mv){
        mv.setViewName("common/error");
        mv.addObject("errorMessage", "403 접근할 권한이 없습니다.");

        return mv;
    }

    @GetMapping("/redirect")
    public String redirectHandler(@RequestParam("url") String url, HttpServletRequest request, Model model) {
        // 로그인 여부 확인
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean notLoggedIn = (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken);

        // URL이 null, 비어 있을 경우 대비
        if (url == null || url.trim().isEmpty() || "null".equalsIgnoreCase(url)) {
            url = "/main";
        }

        // 외부 경로 차단: /로 시작하는 내부 경로만 허용
        if (!url.startsWith("/")) {
            url = "/main";
        }

        // 로그인 안되어 있으면 login으로
        if (notLoggedIn) {
            model.addAttribute("redirectUrl", url);
            return "login/login";
        }

        // 로그인된 경우 정상 redirect
        return "redirect:" + url;
    }

//    @GetMapping("/error/authentication")
//    public ModelAndView showErrorPageByAuthentication(ModelAndView mv){
//        mv.setViewName("common/error");
//        mv.addObject("errorMessage", "401 로그인이 필요한 서비스입니다.");
//
//        return mv;
//    }





}

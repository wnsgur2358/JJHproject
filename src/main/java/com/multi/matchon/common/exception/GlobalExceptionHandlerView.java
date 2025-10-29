package com.multi.matchon.common.exception;

import com.multi.matchon.common.exception.custom.CustomException;
import com.multi.matchon.common.exception.custom.hasCanceledMatchRequestMoreThanOnceException;
import com.multi.matchon.common.exception.custom.MatchupRequestLimitExceededException;
import com.sun.jdi.request.DuplicateRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice(basePackages = {"com.multi.matchon"})
@Slf4j
public class GlobalExceptionHandlerView {

    @ExceptionHandler({DuplicateRequestException.class})
    public ModelAndView exceptionHandler(DuplicateRequestException ex){
        ModelAndView mv = new ModelAndView();
        mv.setViewName("common/error");
        mv.addObject("errorMessage",ex.getMessage());
        log.info("error message:{} ",ex.getMessage());
        ex.printStackTrace(); //나중에 제거
        return mv;
    }

    @ExceptionHandler({hasCanceledMatchRequestMoreThanOnceException.class})
    public ModelAndView exceptionHandler(hasCanceledMatchRequestMoreThanOnceException ex){
        ModelAndView mv = new ModelAndView();
        mv.setViewName("common/error");
        mv.addObject("errorMessage",ex.getMessage());
        log.info("error message: {}",ex.getMessage());
        ex.printStackTrace();
        return mv;
    }

    @ExceptionHandler({MatchupRequestLimitExceededException.class})
    public ModelAndView exceptionHandler(MatchupRequestLimitExceededException ex){
        ModelAndView mv = new ModelAndView();
        mv.setViewName("common/error");
        mv.addObject("errorMessage",ex.getMessage());
        log.info("error message: {}",ex.getMessage());
        ex.printStackTrace();
        return mv;
    }

    @ExceptionHandler({CustomException.class})
    public ModelAndView exceptionHandler(CustomException ex){
        ModelAndView mv = new ModelAndView();
        mv.setViewName("common/error");
        mv.addObject("errorMessage",ex.getMessage());
        log.info("error message: {}",ex.getMessage());
        ex.printStackTrace();
        return mv;
    }

    @ExceptionHandler({AuthorizationDeniedException.class})
    public ModelAndView exceptionHandler(AuthorizationDeniedException ex){
        ModelAndView mv = new ModelAndView();
        mv.setViewName("common/error");
        mv.addObject("errorMessage","Unauthorization 사용자만 접근할 수 있는 페이지입니다.");
        log.info("error message: {}",ex.getMessage());
        ex.printStackTrace();
        return mv;
    }

    @ExceptionHandler({Exception.class})
    public ModelAndView exceptionHandler(Exception ex){
        ModelAndView mv = new ModelAndView();
        mv.setViewName("common/error");
        mv.addObject("errorMessage",ex.getMessage());
        log.info("error message:{} ",ex.getMessage());
        ex.printStackTrace(); //나중에 제거
        return mv;
    }

}

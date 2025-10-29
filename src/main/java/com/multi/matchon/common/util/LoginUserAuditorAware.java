package com.multi.matchon.common.util;


import com.multi.matchon.chat.config.StompHandler;
import com.multi.matchon.common.auth.dto.CustomUser;
import com.multi.matchon.member.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoginUserAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = null;
        authentication = SecurityContextHolder.getContextHolderStrategy().getContext().getAuthentication();

        if(authentication==null){
            authentication = StompHandler.authContext.get();
        }
        if(authentication ==null || !authentication.isAuthenticated())
            return Optional.empty();
        return Optional.of(((CustomUser)authentication.getPrincipal()).getUsername());
//        return Optional.of(((CustomUser)SecurityContextHolder.getContextHolderStrategy().getContext().getAuthentication().getPrincipal()).getUsername());
    }
}

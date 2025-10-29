package com.multi.matchon.common.auth.dto;

import com.multi.matchon.member.domain.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class CustomUser implements UserDetails {

    private final Member member;

    public CustomUser(Member member) {
        this.member = member;
    }

    public Member getMember() {
        return this.member;
    }

    public boolean isTemporaryPassword() {
        return member.getIsTemporaryPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(() -> "ROLE_" + member.getMemberRole().name());
    }

    @Override
    public String getPassword() {
        return member.getMemberPassword();
    }

    @Override
    public String getUsername() {
        return member.getMemberEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

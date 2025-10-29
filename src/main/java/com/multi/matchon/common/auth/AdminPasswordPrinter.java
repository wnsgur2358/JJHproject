package com.multi.matchon.common.auth;

import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminPasswordPrinter {

    // 관리자용 데이터 넣을떄 이메일 : matchon2025@gmail.com , 비밀번호 : Matchon2025!! 암호화 결과 데이터 넣기 -> 한번 출력하면 클래스 삭제 예정
    @PostConstruct
    public void encodeAdminPassword() {
        String rawPassword = "Matchon2025!!"; // 암호화할 비밀번호
        String encoded = new BCryptPasswordEncoder().encode(rawPassword);
        System.out.println("관리자 비밀번호 암호화 결과: " + encoded);
    }

}

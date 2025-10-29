package com.multi.matchon.member.config;//package com.multi.matchon.member.config;
//
//import com.multi.matchon.member.domain.Member;
//import com.multi.matchon.member.domain.MemberRole;
//import com.multi.matchon.member.repository.MemberRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.springframework.security.crypto.password.PasswordEncoder;
////임시 클래스 생성
//@Configuration
//@Profile("local")  // ✅ local 프로필에서만 이 클래스가 작동함
//@RequiredArgsConstructor
//public class AdminInitConfig {
//
//    private final MemberRepository memberRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @Bean
//    public CommandLineRunner initAdminAccount() {
//        return args -> {
//            if (!memberRepository.existsByMemberEmail("admin@wnsgur23.com")) {
//                Member admin = Member.builder()
//                        .memberEmail("admin@wnsgur23.com")
//                        .memberPassword(passwordEncoder.encode("admin2358"))
//                        .memberName("admin")
//                        .memberRole(MemberRole.valueOf("ADMIN"))
//                        .build();
//                memberRepository.save(admin);
//                System.out.println("✅ 관리자 계정(admin@wnsgur23.com)이 생성되었습니다.");
//            } else {
//                System.out.println("ℹ️ 관리자 계정이 이미 존재합니다.");
//            }
//        };
//    }
//}
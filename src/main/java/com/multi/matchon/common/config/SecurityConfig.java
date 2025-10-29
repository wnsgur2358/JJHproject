package com.multi.matchon.common.config;

import com.multi.matchon.common.jwt.service.JwtAccessDeniedHandler;
import com.multi.matchon.common.jwt.service.JwtAuthenticationEntryPoint;
import com.multi.matchon.common.jwt.service.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final JwtAuthenticationEntryPoint entryPoint;
    private final JwtAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults()) // CORS 허용 추가
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(entryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/", "/main", "/signup", "/signup/**", "/login", "/auth/**", "/api/common/datacontroller/**", "/css/**", "/img/**", "/js/**", "/favicon.ico","/connect/**", "/error", "/aichat", "/api/aichat", "/redirect","/introduction/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN") // 관리자 전용 경로
                        .requestMatchers("/community/**").authenticated()//커뮤니티 작성 인증
                        .requestMatchers("/inquiry", "/inquiry/**").authenticated()
                        .requestMatchers("/member/**").authenticated()//임시추가
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers // 배포시 chat-icon 커스텀 변경 가능, localhost 환경에서는 custom X
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives("default-src *; script-src * 'unsafe-inline' 'unsafe-eval'; style-src * 'unsafe-inline'; img-src * data: blob:;")
                        )
                        .frameOptions(frame -> frame.sameOrigin())
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

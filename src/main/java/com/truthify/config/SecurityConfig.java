package com.truthify.config;

import com.truthify.config.auth.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // BCryptPasswordEncoder 임포트 추가
import org.springframework.security.crypto.password.PasswordEncoder; // PasswordEncoder 임포트 추가
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security의 핵심 설정 파일입니다.
 * 소셜 로그인(OAuth2) 기능을 활성화하고 권한별 접근 제어, CORS 설정을 담당하며,
 * 비밀번호 암호화를 위한 PasswordEncoder Bean을 등록합니다.
 */
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    /**
     * 비밀번호 인코딩을 위한 BCryptPasswordEncoder를 Bean으로 등록
     * 이 Bean은 UserService 등에서 비밀번호를 안전하게 해시하는 데 사용
     * @return BCryptPasswordEncoder 인스턴스
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt 알고리즘은 현재 가장 널리 사용되고 권장되는 비밀번호 해시 방식입니다.
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(AbstractHttpConfigurer::disable)

            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/", "/css/**", "/images/**", "/js/**").permitAll()
                .requestMatchers("/oauth2/**", "/login/**").permitAll()
                .requestMatchers("/usr/user/**").permitAll()                
                .requestMatchers("/api/v1/guest/**").hasRole("GUEST")
                .requestMatchers("/api/email/**").permitAll()
                .anyRequest().authenticated()
            )

            .logout(logout -> logout
                .logoutSuccessUrl("http://localhost:3000/")
                .permitAll()
            )
            .formLogin(form -> form
            		.loginProcessingUrl("/usr/user/login")
            		.usernameParameter("loginId")
            		.passwordParameter("loginPw")
            		.successHandler((req, res, auth) -> {
            			res.setStatus(200);
            			res.setContentType("application/json");
            			res.getWriter().write("{\"resultCode\":\"S-1\", \"msg\":\"로그인 성공\"}");
            		})
            		.failureHandler((req, res, ex) -> {
            			res.setStatus(401);
            			res.setContentType("application/json");
            			res.getWriter().write("{\"resultCode\":\"F-1\", \"msg\":\"로그인 실패\"}");
            		})
            		.permitAll()
            		)
            .oauth2Login(oauth2 -> oauth2
                .defaultSuccessUrl("http://localhost:3000/", true)
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService)
                )
            );

        return http.build();
    }
}
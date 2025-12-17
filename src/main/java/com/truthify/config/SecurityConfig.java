package com.truthify.config;

import com.truthify.config.auth.CustomLoginSuccessHandler;
import com.truthify.config.auth.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomLoginSuccessHandler customLoginSuccessHandler;
    private static final String FRONT_URL = "http://localhost:3000";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)

            .authorizeHttpRequests(auth -> auth

                // ✅ Preflight
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // ✅ 정적 리소스
                .requestMatchers("/", "/css/**", "/images/**", "/js/**").permitAll()

                // ✅ 인증 / 회원
                .requestMatchers(
                    "/oauth2/**",
                    "/login/**",
                    "/api/user/login",
                    "/api/user/join",
                    "/api/user/find-login-id",
                    "/api/user/find-password",
                    "/api/user/loginIdDupChk",
                    "/api/user/nicknameDupChk",
                    "/api/user/logout",
                    "/api/v1/auth/user-info",
                    "/api/email/**"
                ).permitAll()

                // ✅ 광고 분석 (쓰기)
                .requestMatchers(HttpMethod.POST, "/api/ad/analyze").authenticated()

                // ✅ 피드백 (쓰기)
                .requestMatchers(HttpMethod.POST, "/api/ad/*/feedback").authenticated()

                // ✅ 분석 결과 조회 (로그인 필요)
                .requestMatchers(HttpMethod.GET, "/api/ad/*/analysis").authenticated()

                // ✅ 피드백 통계 (공개)
                .requestMatchers(HttpMethod.GET, "/api/ad/*/feedback/stats").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/ad/*/feedback/me").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/ad/public").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/ad/*/visibility").authenticated()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
             // ✅ 분석 상세 조회: 공개글은 비로그인도 접근 가능해야 함 (서비스에서 isPublic/owner로 판단)
                .requestMatchers(HttpMethod.GET, "/api/ad/*/detail").permitAll()
                // ❌ 나머지는 차단
                .anyRequest().authenticated()
            ).exceptionHandling(e -> e
                    .authenticationEntryPoint((req, res, ex) -> {
                        res.setStatus(401);
                        res.setContentType("application/json;charset=UTF-8");
                        res.getWriter().write("""
                        {
                          "resultCode": "F-3",
                          "msg": "로그인이 필요합니다"
                        }
                        """);
                    })
                )
            .formLogin(form -> form
                .loginProcessingUrl("/api/user/login")
                .usernameParameter("loginId")
                .passwordParameter("loginPw")
                .successHandler(customLoginSuccessHandler)
                .failureHandler((req, res, ex) -> {
                    res.setStatus(401);
                    res.setContentType("application/json;charset=UTF-8");
                    res.getWriter().write(
                        "{\"resultCode\":\"F-1\", \"msg\":\"로그인 실패\"}"
                    );
                })
                .permitAll()
            )

            .oauth2Login(oauth -> oauth
                .defaultSuccessUrl(FRONT_URL, true)
                .userInfoEndpoint(info -> info.userService(customOAuth2UserService))
            )

            .logout(logout -> logout
                .logoutUrl("/api/user/logout")
                .logoutSuccessHandler((req, res, auth) -> {
                    res.setStatus(200);
                    res.setContentType("application/json;charset=UTF-8");
                    res.getWriter().write(
                        "{\"resultCode\":\"S-1\", \"msg\":\"로그아웃 성공\"}"
                    );
                })
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(FRONT_URL));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}

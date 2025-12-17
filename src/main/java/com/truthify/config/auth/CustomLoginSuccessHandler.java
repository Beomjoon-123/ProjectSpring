package com.truthify.config.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.truthify.config.auth.dto.SessionUser;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * UserService 의존성을 제거한 SuccessHandler.
 * - request.changeSessionId()로 세션 재발급 처리 (session fixation 방어)
 * - 다양한 principal 타입을 안전하게 처리
 * - JSON 응답을 ObjectMapper로 직렬화하여 반환
 */
@Component
@RequiredArgsConstructor
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper = new ObjectMapper(); // 스프링에서 ObjectMapper 빈이 있으면 주입 가능

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        // 세션 고정 공격 방어: 로그인 성공 시 세션ID 재발급
        try { request.changeSessionId(); } catch (Exception ignored) {}

        String username = authentication.getName(); // 일반적으로 loginId
        Long userId = null;
        String nickname = null;
        String name = null;
        String phoneNumber = null;

        Object principal = authentication.getPrincipal();

        // 1) UserDetails
        if (principal instanceof UserDetails ud) {
            username = ud.getUsername();

            try { userId = (Long) ud.getClass().getMethod("getId").invoke(ud); } catch (Exception ignore) {}
            try { nickname = String.valueOf(ud.getClass().getMethod("getNickname").invoke(ud)); } catch (Exception ignore) {}
            try { name = String.valueOf(ud.getClass().getMethod("getName").invoke(ud)); } catch (Exception ignore) {}
            try { phoneNumber = String.valueOf(ud.getClass().getMethod("getPhoneNumber").invoke(ud)); } catch (Exception ignore) {}
        }
        // 2) OAuth2 로그인
        else if (principal instanceof DefaultOAuth2User oauth2User) {
            Map<String, Object> attrs = oauth2User.getAttributes();
            if (attrs.containsKey("nickname")) nickname = String.valueOf(attrs.get("nickname"));
            if (attrs.containsKey("name") && nickname == null) nickname = String.valueOf(attrs.get("name"));
            if (attrs.containsKey("email") && username == null) username = String.valueOf(attrs.get("email"));
            if (attrs.containsKey("id")) {
                try { userId = Long.valueOf(String.valueOf(attrs.get("id"))); } catch (Exception ignore) {}
            }
            if (attrs.containsKey("phoneNumber")) phoneNumber = String.valueOf(attrs.get("phoneNumber"));
        }
        // 3) SessionUser
        else if (principal instanceof SessionUser sessionUser) {
            userId = sessionUser.getId();
            username = sessionUser.getEmail() != null ? sessionUser.getEmail() : sessionUser.getNickname();
            nickname = sessionUser.getNickname();
            name = sessionUser.getName();
            phoneNumber = sessionUser.getPhone();
        }

        // JSON 응답 빌드
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", userId);
        userMap.put("loginId", username);
        userMap.put("nickname", nickname);
        userMap.put("name", name);
        userMap.put("phoneNumber", phoneNumber);

        Map<String, Object> body = new HashMap<>();
        body.put("resultCode", "S-1");
        body.put("msg", "로그인 성공");
        body.put("user", userMap);

        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}

package com.truthify.config.auth.dto;

import com.truthify.domain.user.Member;
import com.truthify.domain.user.Role;
import lombok.Builder;
import lombok.Getter;
import java.util.Map;

@Getter
@Builder
public class OAuthAttributes {
    private Map<String, Object> attributes; // OAuth2User 전체 attributes
    private String nameAttributeKey;        // PK 역할
    private String name;
    private String email;
    private String provider;
    private String providerId;

    public static OAuthAttributes of(String registrationId, String userNameAttributeName,
                                     Map<String, Object> attributes) {
        if ("kakao".equals(registrationId)) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            if (kakaoAccount == null) kakaoAccount = Map.of();

            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            if (profile == null) profile = Map.of();

            String nickname = (String) profile.getOrDefault("nickname", "NoName");
            String email = (String) kakaoAccount.get("email"); // null 가능

            return OAuthAttributes.builder()
                    .name(nickname)
                    .email(email)
                    .provider("kakao")
                    .providerId(String.valueOf(attributes.get("id")))
                    .attributes(attributes)
                    .nameAttributeKey(userNameAttributeName)
                    .build();

        } else if ("google".equals(registrationId)) {
            return OAuthAttributes.builder()
                    .name((String) attributes.get("name"))
                    .email((String) attributes.get("email"))
                    .provider("google")
                    .providerId((String) attributes.get("sub"))
                    .attributes(attributes)
                    .nameAttributeKey(userNameAttributeName)
                    .build();
        }
        throw new IllegalArgumentException("지원하지 않는 OAuth2 provider: " + registrationId);
    }

    public Member toEntity() {
        // 이메일이 null일 경우 임시 값 생성
        String safeEmail = email != null ? email : provider + "_" + providerId + "@noemail.com";

        return Member.builder()
                .nickname(name)
                .email(safeEmail)
                .provider(provider)
                .providerId(providerId)
                .role(Role.GUEST)
                .build();
    }
}

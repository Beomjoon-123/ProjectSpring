package com.truthify.user.dto;

import com.truthify.domain.user.Member; // 💡 Member 엔티티 import
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
    
    // 일반 로그인/회원가입에서 필요한 응답 필드
    private Long id;
    private String loginId;
    private String email;
    private String nickname;
    private String role;
    private String provider; // 소셜 로그인 사용자 구분을 위해 추가

    /**
     * Member 엔티티로부터 UserResponse DTO를 생성합니다.
     */
    public static UserResponse fromEntity(Member member) { // 💡 Member 엔티티를 인수로 받음
        if (member == null) {
            return null;
        }
        return UserResponse.builder()
                .id(member.getId())
                .loginId(member.getLoginId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .role(member.getRole() != null ? member.getRole().getKey() : null)
                .provider(member.getProvider())
                .build();
    }
}
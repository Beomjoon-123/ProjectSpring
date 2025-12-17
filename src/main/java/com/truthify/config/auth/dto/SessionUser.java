package com.truthify.config.auth.dto;

import com.truthify.domain.user.Member;
import lombok.Getter;
import java.io.Serializable;

@Getter
public class SessionUser implements Serializable {

    private static final long serialVersionUID = 1L; // 명시적 serialVersionUID 추가 (중요)

    private Long id;
    private String nickname;
    private String email;
    private String provider;
    private String roleKey;
    private String name;
    private String phone;

    public SessionUser(Member member) {
        this.id = member.getId();
        this.nickname = member.getNickname();
        this.email = member.getEmail();
        this.provider = member.getProvider();
        this.roleKey = member.getRole() != null ? member.getRole().getKey() : null;
        this.name = member.getName();
        this.phone = member.getPhone();
    }

    // 추가: loginId만 있을 때 안전하게 세션에 넣을 수 있는 생성자
    public SessionUser(String loginId) {
        this.id = null;
        this.nickname = loginId; // 임시 닉네임으로 loginId 사용
        this.email = null;
        this.provider = null;
        this.roleKey = null;
        this.name = null;
        this.phone = null;
    }
}

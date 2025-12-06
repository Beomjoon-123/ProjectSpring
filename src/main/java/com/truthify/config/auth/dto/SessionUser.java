package com.truthify.config.auth.dto;

import com.truthify.domain.user.Member;
import lombok.Getter;
import java.io.Serializable;

/**
 * 인증된 사용자 정보를 세션에 저장하기 위한 DTO 클래스 Member 엔티티를 직접 세션에 저장하면 직렬화 오류가 발생할 수 있으므로,
 * 세션 저장용 DTO를 따로 정의하고 Serializable을 구현합니다.
 */
@Getter
public class SessionUser implements Serializable { // 세션에 저장하기 위해 직렬화(Serializable) 구현

	private Long id;
	private String nickname;
	private String email;
	private String picture;
	private String provider;
	private String roleKey; // 권한 키 (예: ROLE_USER)

	/**
	 * Member 엔티티를 기반으로 SessionUser를 생성하는 생성자
	 */
	public SessionUser(Member member) {
		this.id = member.getId();
		this.nickname = member.getNickname();
		this.email = member.getEmail();
		this.picture = member.getPicture();
		this.provider = member.getProvider();
		this.roleKey = member.getRoleKey();
	}
}
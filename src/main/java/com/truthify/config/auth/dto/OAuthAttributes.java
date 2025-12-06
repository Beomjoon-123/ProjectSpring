package com.truthify.config.auth.dto;

import com.truthify.domain.user.Member;
import com.truthify.domain.user.Role;
import lombok.Builder;
import lombok.Getter;
import java.util.Map;

/**
 * 소셜 로그인 서비스별로 제공되는 사용자 정보를 공통된 포맷으로 변환하는 DTO 클래스
 */
@Getter
public class OAuthAttributes {

	private Map<String, Object> attributes; // OAuth2User의 getAttributes()
	private String nameAttributeKey; // 사용자 이름, 이메일 등의 키
	private String name; // ✅ 사용자 이름/닉네임 필드
	private String email;
	private String picture;
	private String provider; // 구글, 카카오 등
	private String providerId; // 소셜 서비스 내에서 사용자 고유 ID

	@Builder
	public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email,
			String picture, String provider, String providerId) {
		this.attributes = attributes;
		this.nameAttributeKey = nameAttributeKey;
		this.name = name;
		this.email = email;
		this.picture = picture;
		this.provider = provider;
		this.providerId = providerId;
	}

	/**
	 * 소셜 서비스별로 사용자 정보 추출 로직을 분기
	 */
	public static OAuthAttributes of(String registrationId, String userNameAttributeName,
			Map<String, Object> attributes) {
		if ("kakao".equals(registrationId)) {
			return ofKakao(userNameAttributeName, attributes);
		}
		return ofGoogle(userNameAttributeName, attributes);
	}

	private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
		return OAuthAttributes.builder().name((String) attributes.get("name")).email((String) attributes.get("email"))
				.picture((String) attributes.get("picture")).attributes(attributes)
				.nameAttributeKey(userNameAttributeName).provider("GOOGLE")
				.providerId((String) attributes.get(userNameAttributeName)) // 'sub' 값을 사용
				.build();
	}

	/**
	 * Kakao에서 받은 사용자 정보를 OAuthAttributes로 변환 카카오는 정보가 nested 되어 있으므로 별도 처리가 필요
	 */
	private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
		// 카카오 계정 정보는 'kakao_account' 내부에
		Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

		// 프로필 정보는 'profile' 내부에
		Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");

		String email = (String) kakaoAccount.get("email");
		String picture = (String) kakaoProfile.get("profile_image_url");
		String name = (String) kakaoProfile.get("nickname");
		String providerId = String.valueOf(attributes.get(userNameAttributeName));

		return OAuthAttributes.builder().name((String) kakaoProfile.get("nickname"))
				.email((String) kakaoAccount.get("email")).picture((String) kakaoProfile.get("profile_image_url"))
				.attributes(attributes).nameAttributeKey(userNameAttributeName) // 'id' (Kakao 기본 키)
				.provider("KAKAO").providerId(String.valueOf(attributes.get(userNameAttributeName))) // 'id' 값을 String으로
																										// 변환
				.build();
	}

	/**
	 * OAuthAttributes에서 Member 엔티티를 생성 최초 회원가입 시 사용
	 */
	public Member toEntity() {
		return Member.builder().nickname(name).email(email).picture(picture).provider("KAKAO").providerId(providerId)
				.role(Role.GUEST).build();
	}
}
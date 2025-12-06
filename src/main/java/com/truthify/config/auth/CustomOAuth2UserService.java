package com.truthify.config.auth;

import com.truthify.config.auth.dto.OAuthAttributes;
import com.truthify.config.auth.dto.SessionUser; // 💡 SessionUser DTO import
import com.truthify.domain.user.Member;
import com.truthify.domain.user.MemberMapper;
import com.truthify.domain.user.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.Optional;

/**
 * OAuth2 로그인 성공 후 사용자 정보를 처리하는 서비스입니다. OAuth2User 정보를 기반으로 DB에 저장하거나 업데이트하고,
 * 세션에 저장합니다.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	private final MemberMapper memberMapper;
	private final jakarta.servlet.http.HttpSession httpSession; // 💡 HttpSession 주입

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

		OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
		OAuth2User oauth2User = delegate.loadUser(userRequest);

		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint()
				.getUserNameAttributeName();

		OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName,
				oauth2User.getAttributes());

		Member member = saveOrUpdate(attributes);

		// 💡 로그인 성공 시, 사용자 정보를 세션에 저장
		httpSession.setAttribute("user", new SessionUser(member));

		// 6. SecurityContext에 저장할 DefaultOAuth2User 객체 생성 및 반환
		return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(member.getRoleKey())), // 권한 설정
				attributes.getAttributes(), // 사용자 속성 Map
				attributes.getNameAttributeKey() // 속성 키 (Primary Key)
		);
	}

	/**
	 * DB에 사용자 정보가 있으면 업데이트하고, 없으면 저장합니다.
	 */
	private Member saveOrUpdate(OAuthAttributes attributes) {
		// 이메일을 통해 기존 사용자 찾기
		Optional<Member> memberOptional = memberMapper.findByEmail(attributes.getEmail());

		Member member;

		if (memberOptional.isPresent()) {
			// 💡 기존 사용자: 닉네임, 프로필 사진만 업데이트
			member = memberOptional.get();
			member.update(attributes.getName(), attributes.getPicture());
			memberMapper.update(member); // DB 업데이트
			log.info("Existing member updated: {}", member.getEmail());

		} else {
			// 💡 신규 사용자: Member 엔티티를 생성하여 DB에 저장
			member = attributes.toEntity();
			// toEntity에서 Role이 GUEST로 설정되었을 경우 USER로 변경합니다. (소셜 로그인은 일반 사용자)
			if (member.getRole() == Role.GUEST) {
				member.setRole(Role.USER);
			}
			memberMapper.save(member); // DB 저장
			log.info("New member saved: {}", member.getEmail());
		}

		return member;
	}
}
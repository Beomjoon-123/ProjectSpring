package com.truthify.config.auth;

import com.truthify.config.auth.dto.OAuthAttributes;
import com.truthify.config.auth.dto.SessionUser;
import com.truthify.domain.user.Member;
import com.truthify.domain.user.MemberMapper;
import com.truthify.domain.user.Role;
import jakarta.servlet.http.HttpSession;
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

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberMapper memberMapper;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oauth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName,
                oauth2User.getAttributes());

        Member member = saveOrUpdate(attributes);

        httpSession.setAttribute("user", new SessionUser(member));

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(member.getRoleKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey()
        );
    }

    private Member saveOrUpdate(OAuthAttributes attributes) {
        Optional<Member> memberOptional;

        // 이메일이 존재하면 이메일 기준, 없으면 provider+providerId 기준
        if (attributes.getEmail() != null && !attributes.getEmail().isEmpty()) {
            memberOptional = memberMapper.findByEmail(attributes.getEmail());
        } else {
            memberOptional = memberMapper.findByProviderAndProviderId(attributes.getProvider(), attributes.getProviderId());
        }

        Member member;

        if (memberOptional.isPresent()) {
            member = memberOptional.get();
            member.update(attributes.getName());
            memberMapper.update(member);
            log.info("Existing member updated: {}", member.getNickname());
        } else {
            member = attributes.toEntity();

            if (member.getRole() == Role.GUEST) {
                member.setRole(Role.USER);
            }

            // 이메일 없으면 임시 이메일 생성
            if (member.getEmail() == null || member.getEmail().isEmpty()) {
                member.setEmail(member.getProvider() + "_" + member.getProviderId() + "@noemail.com");
            }

            memberMapper.save(member);
            log.info("New member saved: {}", member.getNickname());
        }

        return member;
    }
}

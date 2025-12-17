package com.truthify.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.truthify.domain.user.Member;
import com.truthify.domain.user.MemberMapper;
import com.truthify.user.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
	private final MemberMapper memberMapper;
	
	@Override
	public UserDetails loadUserByUsername(String loginId) {
		Member member = memberMapper.findByLoginId(loginId)
				.orElseThrow(() -> new UsernameNotFoundException("회원없음"));
		return new CustomUserDetails(member);
	}
}

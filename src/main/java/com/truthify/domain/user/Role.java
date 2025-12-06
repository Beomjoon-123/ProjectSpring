package com.truthify.domain.user;

import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
	GUEST("ROLE_GUEST", "손님"), USER("ROLE_USER", "일반 사용자"), ADMIN("ROLE_ADMIN", "관리자");

	private final String key;
	private final String title;

	public static Role ofKey(String key) {
		return Arrays.stream(Role.values()).filter(r -> r.getKey().equals(key)).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Invalid Role key: " + key));
	}
}

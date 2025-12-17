package com.truthify.domain.user;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {
	private Long id;
	private String loginId;
	private String loginPw;
	private String nickname;
	private String email;
	private String provider;
	private String providerId;
	private String phone;
	private String name;
	private Role role;
	private LocalDateTime regDate;
	private LocalDateTime updateDate;

	public void update(String nickname) {
		this.nickname = nickname;
		this.updateDate = LocalDateTime.now();
	}

	public String getRoleKey() {
		return this.role.getKey();
	}
}
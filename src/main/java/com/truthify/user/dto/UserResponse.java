package com.truthify.user.dto;

import java.time.LocalDateTime;
import com.truthify.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
	private Integer id;
	private String loginId;
	private String email;
	private String nickname;
	private String role;
	private LocalDateTime regDate;
	private LocalDateTime updateDate;
	
	public static UserResponse fromEntity(User user) {
		return UserResponse.builder().id(user.getId()).loginId(user.getLoginId()).email(user.getEmail()).nickname(user.getNickname()).role(user.getRole())
				.regDate(user.getRegDate()).updateDate(user.getUpdateDate()).build();
	}
}

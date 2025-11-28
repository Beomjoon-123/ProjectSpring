package com.truthify.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
	private String loginId;
	private String loginPw;
	private String email;
	private String nickname;
}

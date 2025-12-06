package com.truthify.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.truthify.config.auth.dto.SessionUser; // ✅ SessionUser DTO 임포트
import com.truthify.domain.User;
import com.truthify.service.UserService;
import com.truthify.user.dto.ResultData;
import com.truthify.user.dto.UserRequest;
import com.truthify.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("/usr/user")
public class UserController {
	private final UserService userService;

	@PostMapping("/join")
	public ResultData<UserResponse> joinMember(@RequestBody UserRequest request) {
		try {
			UserResponse user = userService.joinMember(request);
			return ResultData.of("S-1", String.format("%s님 환영합니다 :)", user.getNickname()), user);
		} catch (IllegalArgumentException e) {
			return ResultData.of("F-1", e.getMessage());
		} catch (Exception e) {
			return ResultData.of("F-2", "Server Error");
		}
	}

	@GetMapping("/me")
	public ResultData<UserResponse> getLoginedUserInfo(@AuthenticationPrincipal SessionUser principal) {

		if (principal == null) {
			return ResultData.of("F-3", "로그인이 필요한 서비스입니다");
		}

		String identifier = principal.getEmail();

		try {
			if (principal instanceof SessionUser) {
				identifier = ((SessionUser) principal).getEmail();
				return ResultData.of("F-6", "OAuth2 사용자는 현재 ID/PW 조회 경로에서 지원하지 않습니다. (임시 메시지)");
			}

			String loginIdFromPrincipal = null;
			try {

				loginIdFromPrincipal = ((org.springframework.security.core.userdetails.UserDetails) principal)
						.getUsername();
			} catch (ClassCastException e) {
				if (principal instanceof SessionUser) {
					return ResultData.of("F-6", "OAuth2 사용자는 현재 ID/PW 조회 경로에서 지원하지 않습니다. (임시 메시지)");
				}
				return ResultData.of("F-7", "인증 주체 타입 오류");
			}

			UserResponse user = userService.getMemberByLoginId(loginIdFromPrincipal);

			if (user == null) {
				return ResultData.of("F-4", "사용자 정보를 찾을 수 없습니다");
			}
			return ResultData.of("S-1", "사용자 정보 조회 성공", user);
		} catch (Exception e) {
			return ResultData.of("F-5", "오류가 발생하여 사용자 정보를 불러올 수 없습니다");
		}
	}

	@GetMapping("/loginIdDupChk")
	@ResponseBody
	public ResultData<?> loginIdDupChk(@RequestParam String loginId) {
		UserResponse user = this.userService.getMemberByLoginId(loginId);

		if (user != null) {
			return ResultData.of("F-1", "이미 사용중인 아이디입니다");
		}

		return ResultData.of("S-1", "사용가능한 아이디입니다");
	}

	@GetMapping("/nicknameDupChk")
	public ResultData<?> nicknameDupChk(@RequestParam String nickname) {
		boolean isDup = userService.checkNicknameDuplication(nickname);

		if (isDup) {
			return ResultData.of("F-1", "이미 사용중인 닉네임입니다");
		}
		return ResultData.of("S-1", "사용가능한 닉네임입니다");
	}
}
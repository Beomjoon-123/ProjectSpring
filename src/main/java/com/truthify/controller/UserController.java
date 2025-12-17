package com.truthify.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.truthify.service.UserService;
import com.truthify.user.dto.ResultData;
import com.truthify.user.dto.UserRequest;
import com.truthify.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;

//@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
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
			e.printStackTrace();
			return ResultData.of("F-2", "Server Error");
		}
	}

	@GetMapping("/me")
	public ResultData<?> getLoginedUserInfo(@AuthenticationPrincipal Object principal) {

		if (principal == null || principal.equals("anonymousUser")) {
			return ResultData.of("F-3", "로그인이 필요한 서비스입니다");
		}
		
		if (principal instanceof OAuth2User oAuth2User) {
			String email = oAuth2User.getAttribute("email");
			
			if (email == null) {
				return ResultData.of("F-6", "OAuth 사용자 이메일 없음");
			}
			
			UserResponse user = userService.getMemberByEmail(email);
			return ResultData.of("S-1", "OAuth 사용자", user);
		}
		
		if (principal instanceof UserDetails userdetails) {
			UserResponse user = userService.getMemberByLoginId(userdetails.getUsername());
			return ResultData.of("S-1", "일반 사용자", user);
		}
		return ResultData.of("F-7", "알 수 없는 로그인 타입");
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

	@PostMapping("/find-login-id")
	public Map<String, Object> findLoginId(@RequestParam String email) {
		Map<String, Object> result = new HashMap<>();

		String loginId = userService.findLoginByEmail(email);
		if (loginId == null) {
			result.put("resultCode", "F-1");
			result.put("msg", " 해당 이메일로 가입된 계정이 없습니다");
		} else {
			result.put("resultCode", "S-1");
			result.put("loginId", loginId);
		}
		return result;
	}

	@PostMapping("/find-password")
	public ResultData<?> findPassword(@RequestBody Map<String, String> request) {
		Map<String, Object> result = new HashMap<>();

		String loginId = request.get("loginId");
		String email = request.get("email");

		if (loginId == null || email == null) {
			return ResultData.of("F-1", "아이디와 이메일을 모두 입력해주세요");
		}
		
		String tempPassword = userService.sendTempPassword(loginId, email);
		
		if (tempPassword != null) {
			return ResultData.of("S-1", "임시 비밀번호가 생성되었습니다", "임시 비밀번호를 전송했습니다");
		} else {
			return ResultData.of("F-2", "입력하신 아이디와 이메일 정보가 일치하는 사용자를 찾을 수 없습니다");
		}
	}
}
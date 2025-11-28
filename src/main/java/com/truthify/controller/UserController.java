package com.truthify.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.truthify.service.UserService;
import com.truthify.user.dto.ResultData;
import com.truthify.user.dto.UserRequest;
import com.truthify.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;

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
		} catch(IllegalArgumentException e) {
			return ResultData.of("F-1", e.getMessage());
		} catch (Exception e) {
			return ResultData.of("F-2", "Server Error");
		}
	}
	
	@GetMapping("loginIdDupChk")
	@ResponseBody
	public ResultData<UserResponse> loginIdDupChk(String loginId) {
		UserResponse user = this.userService.getMemberByLoginId(loginId);
		
		if(user != null) {
			return ResultData.of("F-1", "이미 사용중인 아이디입니다");
		}
		
		return ResultData.of("S-1", "사용가능한 아이디입니다");
	}
}

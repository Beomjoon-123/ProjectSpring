package com.truthify.email;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {
	private final EmailService emailService;

	@PostMapping("/send")
	public Map<String, Object> send(@RequestBody Map<String, String> request) {

		String email = request.get("email");
		
		if (email == null || email.isEmpty()) {
			return Map.of (
					"success", false,
					"message", "이메일을 입력해주세요");
		}
		emailService.sendVerificationCode(email);
		
		return Map.of(
				"success", true,
				"message", "인증코드가 전송되었습니다");
	}

	@PostMapping("/verify")
	public Map<String, Object> verify(@RequestBody Map<String, String> request) {

		String email = request.get("email");
		String code = request.get("code");
		boolean verified = emailService.verifyCode(email, code);

		if (verified) {
			return Map.of("success", true, "message", "인증 성공");
		}
		return Map.of("success", false, "message", "인증 실패");
	}
}

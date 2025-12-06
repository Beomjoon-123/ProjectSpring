package com.truthify.controller;

import com.truthify.config.auth.dto.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증(Authentication) 관련 API를 처리하는 컨트롤러
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

	private final HttpSession httpSession;

	/**
	 * 현재 로그인된 사용자의 정보를 조회합니다. 프론트엔드가 페이지 로드 시 로그인 상태를 확인하는 데 사용됩니다. * URL: GET
	 * /api/v1/auth/user-info 권한: permitAll (로그인 여부와 관계없이 접근 허용)
	 */
	@GetMapping("/user-info")
	public ResponseEntity<SessionUser> getUserInfo() {

		// HttpSession에서 "user"라는 이름으로 저장된 SessionUser 객체를 가져옵니다.
		SessionUser user = (SessionUser) httpSession.getAttribute("user");

		if (user == null) {
			// 로그인되어 있지 않은 경우 (user가 세션에 없는 경우)
			log.debug("No user session found.");
			// 204 No Content를 반환하거나 null을 포함한 200 OK를 반환할 수 있습니다.
			return ResponseEntity.ok(null);
		}

		log.info("User session found: {}", user.getNickname());
		return ResponseEntity.ok(user); // SessionUser 객체를 JSON으로 반환
	}
}
package com.truthify.email;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.truthify.user.dto.ResultData;

import java.util.Map;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    /**
     * 인증 코드 발송
     */
    @PostMapping("/send")
    public Map<String, Object> send(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isEmpty()) {
            return Map.of("success", false, "message", "이메일을 입력해주세요");
        }

        emailService.sendVerificationCode(email);
        return Map.of("success", true, "message", "인증코드가 전송되었습니다");
    }

    /**
     * 인증 코드 검증
     */
    @PostMapping("/verify")
    public ResultData<?> verifyEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");

        if (email == null || code == null) {
            return ResultData.of("F-1", "코드를 입력해주세요");
        }

        boolean verified = emailService.verifyCode(email, code);
        if (verified) {
            return ResultData.of("S-1", "인증 완료");
        } else {
            return ResultData.of("F-2", "인증번호가 틀립니다");
        }
    }
}

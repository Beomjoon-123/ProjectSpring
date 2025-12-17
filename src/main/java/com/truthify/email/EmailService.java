package com.truthify.email;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final VerificationStore verificationStore;
    private final Logger log = LoggerFactory.getLogger(EmailService.class);

    // 인증 코드 TTL (초)
    private final long CODE_TTL_SECONDS = 180;

    /**
     * 이메일로 인증 코드 전송
     */
    public void sendVerificationCode(String email) {
        String code = generateCode6();
        // 서버 저장
        verificationStore.put(email, code, CODE_TTL_SECONDS);

        // 메일 전송
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom("dbsdnrwns@gmail.com"); // 실제 발신계정
        message.setSubject("[Truthify] 이메일 인증 코드");
        message.setText(String.format("안녕하세요.\n\n인증 코드: %s\n\n이 코드는 %d초 동안 유효합니다.", code, CODE_TTL_SECONDS));

        mailSender.send(message);
        log.info("Sent verification code to {} (code hidden)", email);
    }

    /**
     * 인증 코드 검증
     */
    public boolean verifyCode(String email, String code) {
        return verificationStore.verify(email, code);
    }

    /**
     * 6자리 인증 코드 생성
     */
    private String generateCode6() {
        SecureRandom random = new SecureRandom();
        int n = random.nextInt(900000) + 100000; // 100000 ~ 999999
        return String.valueOf(n);
    }
}

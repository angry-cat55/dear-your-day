package com.example.dearyourdayserver.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    // 이메일 발송 인터페이스
    private final JavaMailSender javaMailSender;

    // 인증번호와 발송 시간을 저장하는 메모리 (Key: 이메일, Value: 인증번호)
    private final Map<String, String> verificationCodes = new ConcurrentHashMap<>();

    @Value("${spring.mail.username}")
    private String senderEmail;

    // 1. 인증번호 메일 발송
    public void sendEmail(String toEmail) {
        if (verificationCodes.containsKey(toEmail)) {
            verificationCodes.remove(toEmail); // 기존 코드가 있으면 삭제
        }

        String authCode = createCode(); // 랜덤 인증번호 생성
        MimeMessage message = createEmailForm(toEmail, authCode); // 메일 내용 작성

        javaMailSender.send(message); // 메일 발송

        // 메모리에 저장 (이메일 <-> 인증번호)
        verificationCodes.put(toEmail, authCode);
        log.info("인증번호 발송 완료: email={}, code={}", toEmail, authCode);
    }

    // 2. 인증번호 검증
    public boolean verifyEmailCode(String email, String code) {
        String savedCode = verificationCodes.get(email);

        if (savedCode != null && savedCode.equals(code)) {
            verificationCodes.remove(email); // 인증 성공하면 메모리에서 삭제
            return true;
        }
        return false;
    }

    // 메일 폼 생성
    private MimeMessage createEmailForm(String email, String authCode) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(senderEmail);
            helper.setTo(email);
            helper.setSubject("[너의 하루에게] 회원가입 인증번호입니다.");

            String logoUrl = "https://github.com/angry-cat55/dear-your-day/blob/develop/dearyourdayserver/src/main/resources/images/main_logo_transparent.png?raw=true";
            String body = """
                            <div style="font-family: 'Apple SD Gothic Neo', 'Malgun Gothic', sans-serif; background-color: #f5f5f5; padding: 40px 0;">
                                    <div style="max-width: 400px; margin: 0 auto; background-color: #ffffff; padding: 30px; border-radius: 12px; box-shadow: 0 4px 10px rgba(0,0,0,0.05);">
                    
                                        <div style="text-align: center; margin-bottom: 30px;">
                                            <img src="%s" alt="너의 하루에게 로고" style="width: 360px; height: auto; display: block; margin: 0 auto;">
                                            <p style="color: #888; font-size: 14px; margin-top: 10px;">AI 감성 다이어리 파트너</p>
                                        </div>
                    
                                        <div style="text-align: center;">
                                <p style="font-size: 16px; color: #333; line-height: 1.6; margin-bottom: 20px;">
                                    안녕하세요!<br>
                                    회원가입을 위한 인증번호를 보내드립니다.<br>
                                    아래 번호를 앱에 입력해 주세요.
                                </p>
                    
                                <div style="background-color: #F3F4F6; padding: 15px; border-radius: 8px; margin: 20px 0;">
                                    <span style="font-size: 32px; font-weight: bold; letter-spacing: 5px; color: #6A5AE0;">%s</span>
                                </div>
                    
                                <p style="font-size: 13px; color: #999; margin-top: 30px;">
                                    본 메일은 발신 전용입니다.<br>
                                    인증번호 유효시간은 3분입니다.
                                </p>
                            </div>
                    
                        </div>
                    
                        <div style="text-align: center; margin-top: 20px;">
                            <p style="font-size: 12px; color: #aaa;">© 2026 Dear Your Day. All rights reserved.</p>
                        </div>
                    </div>
                    """.formatted(logoUrl, authCode); // %s 자리에 authCode가 들어갑니다.

            helper.setText(body, true);
            return message;
        } catch (MessagingException e) {
            throw new RuntimeException("이메일 형식을 생성하지 못했습니다.");
        }
    }

    // 6자리 랜덤 숫자 생성
    private String createCode() {
        Random random = new Random();
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            key.append(random.nextInt(10));
        }
        return key.toString();
    }
}
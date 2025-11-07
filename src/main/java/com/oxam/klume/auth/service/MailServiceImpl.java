package com.oxam.klume.auth.service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;
    private final RedisService redisService;

    private static final long TTL_MINUTES = 3;  // 인증 코드 유효시간 (3분)
    private static final long VERIFIED_TTL_MINUTES = 10;  // 인증 완료 후 회원가입까지 10분
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");


    @Override
    public void sendVerificationCode(String email) {
        String code = generateCode();

        // Redis에 인증코드 저장 (3분 TTL)
        redisService.setDataExpire(email, code, TTL_MINUTES, TimeUnit.MINUTES);

        // 만료 예정 시각 계산
        String expireTime = LocalDateTime.now().plusMinutes(TTL_MINUTES).format(FORMATTER);

        // 이메일 전송
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("[KLÜME] 이메일 인증코드 안내");

            // 이메일 발송자를 KLÜME 으로 출력
            try {
                helper.setFrom("no-reply@klume.io", "KLÜME");
            } catch (MessagingException | UnsupportedEncodingException e) {
                throw new IllegalStateException("이메일 발신자 설정 중 오류 발생", e);
            }

            // 답신 방지 헤더 설정
            message.addHeader("Reply-To", "no-reply@klume.io");
            message.addHeader("X-Auto-Response-Suppress", "All");
            message.addHeader("Auto-Submitted", "auto-generated");

            // 이메일 본문 (로고 + 코드 + 만료시각)
            String htmlContent = buildEmailContent(code, expireTime);
            helper.setText(htmlContent, true);

            // inline 로고 이미지 첨부
            FileSystemResource logo = new FileSystemResource(
                    new File("src/main/resources/static/images/klume_logo.png")
            );
            helper.addInline("klumeLogo", logo);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new IllegalStateException("이메일 전송에 실패했습니다.", e);
        }
    }



    private String buildEmailContent(String code, String expireTime) {
        return """
            <div style="font-family:'Noto Sans KR', sans-serif; background-color:#f8f9fa; padding:24px; border-radius:10px;">
                <div style="text-align:center; margin-bottom:16px;">
                    <img src="cid:klumeLogo" width="120" alt="KLÜME 로고"/>
                </div>
                <h2 style="text-align:center; color:#222;">이메일 인증</h2>
                <p style="text-align:center; color:#333;">안녕하세요 😊<br>
                아래 인증코드를 입력하여 이메일 인증을 완료해주세요.</p>
                <div style="font-size:28px; font-weight:bold; text-align:center; color:#0055ff; margin:16px 0;">
                    %s
                </div>
                <p style="text-align:center; font-size:14px; color:#555;">
                    ⏰ 인증코드는 <b>3분</b>간 유효합니다.<br>
                    (만료 예정 시각: <b>%s</b>)
                </p>
                <hr style="border:none; border-top:1px solid #ddd; margin:24px 0;">
                <p style="font-size:12px; color:#999; text-align:center;">
                    본 메일은 발신 전용입니다. 문의는 KLÜME 관리자에게 연락해주세요.
                </p>
            </div>
        """.formatted(code, expireTime);
    }

    private String generateCode() {
        Random random = new Random();
        return String.valueOf(100000 + random.nextInt(900000));
    }

    @Override
    public boolean verifyCode(String email, String code) {
        String storedCode = redisService.getData(email);
        if (storedCode != null && storedCode.equals(code)) {
            // 인증 성공 시 코드 삭제하고 인증 완료 상태 저장 (10분 유효)
            redisService.deleteData(email);
            redisService.setDataExpire(email + ":verified", "true", VERIFIED_TTL_MINUTES, TimeUnit.MINUTES);
            return true;
        }
        return false;
    }

    public boolean isEmailVerified(String email) {
        String verified = redisService.getData(email + ":verified");
        return "true".equals(verified);
    }

    public void clearVerification(String email) {
        redisService.deleteData(email + ":verified");
    }
}

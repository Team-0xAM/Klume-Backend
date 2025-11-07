package com.oxam.klume.member.service;

public interface MailService {

    void sendVerificationCode(String email);

    boolean verifyCode(String email, String code);

    boolean isEmailVerified(String email);

    void clearVerification(String email);

}

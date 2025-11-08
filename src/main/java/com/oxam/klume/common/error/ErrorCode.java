package com.oxam.klume.common.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    /* COMMON */
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON001", "Entity not found"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON002", "Internal server error"),

    /* AUTH - 인증 */
    EMAIL_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "AUTH001", "이메일 인증이 완료되지 않았습니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "AUTH002", "이미 가입된 이메일입니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH003", "이메일 또는 비밀번호가 일치하지 않습니다."),
    SOCIAL_LOGIN_REQUIRED(HttpStatus.BAD_REQUEST, "AUTH004", "소셜 로그인 회원은 해당 소셜 계정으로 로그인해주세요."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH005", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH006", "만료된 토큰입니다."),

    /* MEMBER - 회원 */
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER001", "존재하지 않는 회원입니다."),
    MEMBER_DELETED(HttpStatus.FORBIDDEN, "MEMBER002", "탈퇴한 회원입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(final HttpStatus status, final String code, final String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
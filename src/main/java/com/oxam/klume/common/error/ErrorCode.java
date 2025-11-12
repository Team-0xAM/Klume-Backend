package com.oxam.klume.common.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    /* COMMON */
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON001", "Entity not found"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON002", "Internal server error"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON003", "Bad request"),

    /* File */
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "FILE001", "File not found"),
    FILE_INVALID_EXTENSION(HttpStatus.BAD_REQUEST, "FILE002", "Invalid file extension"),

    /* Member */
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER001", "Member not found"),
    MEMBER_SYSTEM_ROLE_NOT_ADMIN(HttpStatus.FORBIDDEN, "MEMBER002", "Member is not admin"),
    MEMBER_DELETED(HttpStatus.FORBIDDEN, "MEMBER003", "탈퇴한 회원입니다."),

    /* AUTH - 인증 */
    EMAIL_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "AUTH001", "이메일 인증이 완료되지 않았습니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "AUTH002", "이미 가입된 이메일입니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH003", "이메일 또는 비밀번호가 일치하지 않습니다."),
    SOCIAL_LOGIN_REQUIRED(HttpStatus.BAD_REQUEST, "AUTH004", "소셜 로그인 회원은 해당 소셜 계정으로 로그인해주세요."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH005", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH006", "만료된 토큰입니다."),
    VERIFICATION_CODE_NOT_FOUND(HttpStatus.BAD_REQUEST, "AUTH007", "인증 코드가 존재하지 않거나 만료되었습니다."),
    VERIFICATION_CODE_MISMATCH(HttpStatus.BAD_REQUEST, "AUTH008", "인증 코드가 일치하지 않습니다."),

    /* Organization */
    ORGANIZATION_NOT_FOUND(HttpStatus.NOT_FOUND, "ORGANIZATION001", "Organization not found"),
    ORGANIZATION_NOT_ADMIN(HttpStatus.FORBIDDEN, "ORGANIZATION002", "Organization not admin"),
    ORGANIZATION_MEMBER_ACCESS_DENIED(HttpStatus.FORBIDDEN, "ORGANIZATION003", "Not a member of the organization"),
    ORGANIZATION_MEMBER_ALREADY_EXISTS(HttpStatus.CONFLICT, "ORGANIZATION004", "Organization member already exists"),
    ORGANIZATION_INVITATION_CODE_INVALID(HttpStatus.BAD_REQUEST, "ORGANIZATION005", "Organization invitation code is expired or invalid"),
    ORGANIZATION_GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "ORGANIZATION006", "Organization group not found"),
    ORGANIZATION_GROUP_NAME_DUPLICATED(HttpStatus.CONFLICT, "ORGANIZATION007", "Organization group name duplicated"),
    ORGANIZATION_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "ORGANIZATION008", "Organization Member not found"),
    ORGANIZATION_ADMIN_MINIMUM_REQUIRED(HttpStatus.BAD_REQUEST, "ORGANIZATION009", "Organization admin minimum required"),
    ORGANIZATION_MISMATCH(HttpStatus.FORBIDDEN, "ORGANIZATION010", "Organization mismatch"),

    /* Organization Notice */
    ORGANIZATION_NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTICE001", "Organization notice not found"),

    /* Room */
    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "ROOM001","Room not found"),

    /* Available_Time */
    AVAILABLE_TIME_NOT_FOUND(HttpStatus.NOT_FOUND, "AVAILABLE_TIME001","Available time not found"),
    AVAILABLE_TIME_OVERLAP(HttpStatus.CONFLICT, "AVAILABLE_TIME002", "Available time overlap"),

    /* Daily_Available_Time */
    DAILY_AVAILABLE_TIME_NOT_FOUND(HttpStatus.NOT_FOUND, "DAILY_AVAILABLE_TIME001","Daily available time not found"),

    /* Reservation */
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "RESERVATION001","Reservation not found"),
    RESERVATION_ALREADY_EXISTS(HttpStatus.CONFLICT, "RESERVATION002", "Reservation exists"),
    RESERVATION_ALREADY_STARTED(HttpStatus.CONFLICT, "RESERVATION003", "Reservation time has already started"),

    /* FAQ */
    FAQ_NOT_FOUND(HttpStatus.NOT_FOUND, "FAQ001", "FAQ not found"),

    /* S3 */
    S3_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S3001", "S3 upload failed"),
    S3_DELETE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S3002", "S3 Delete failed"),
    S3_URL_INVALID(HttpStatus.BAD_REQUEST, "S3003", "Invalid S3 URL"),

    /* Organization Member */
    ORGANIZATION_MEMBER_NOT_FOUNT(HttpStatus.NOT_FOUND, "ORGANIZATIONMEMBER001", "Organization member not found");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(final HttpStatus status, final String code, final String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
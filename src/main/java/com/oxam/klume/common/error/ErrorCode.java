package com.oxam.klume.common.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    /* COMMON */
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON001", "Entity not found"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON002", "Internal server error"),

    /* Member */
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER001", "Member not found"),
    MEMBER_SYSTEM_ROLE_NOT_ADMIN(HttpStatus.FORBIDDEN, "MEMBER002", "Member is not admin"),

    /* Organization */
    ORGANIZATION_NOT_FOUND(HttpStatus.NOT_FOUND, "ORGANIZATION001", "Organization not found"),
    ORGANIZATION_NOT_ADMIN(HttpStatus.FORBIDDEN, "ORGANIZATION002", "Organization not admin"),

    /* Organization Notice */
    ORGANIZATION_NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTICE001","Organization notice not found"),

    /* FAQ */
    FAQ_NOT_FOUND(HttpStatus.NOT_FOUND, "FAQ001", "FAQ not found");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(final HttpStatus status, final String code, final String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
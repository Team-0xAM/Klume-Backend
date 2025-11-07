package com.oxam.klume.common.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    /* COMMON */
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON001", "Entity not found"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON002", "Internal server error"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON003", "Bad request"),

    /* Member */
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER001", "Member not found"),

    /* File */
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "FILE001", "File not found"),
    FILE_INVALID_EXTENSION(HttpStatus.BAD_REQUEST, "FILE002", "Invalid file extension"),

    /* Organization */
    ORGANIZATION_NOT_FOUND(HttpStatus.NOT_FOUND, "ORGANIZATION001", "Organization not found"),
    ORGANIZATION_NOT_ADMIN(HttpStatus.FORBIDDEN, "ORGANIZATION002", "Organization not admin");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(final HttpStatus status, final String code, final String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
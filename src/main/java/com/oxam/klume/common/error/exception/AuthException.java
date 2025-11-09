package com.oxam.klume.common.error.exception;

import com.oxam.klume.common.error.ErrorCode;

public class AuthException extends BusinessException {
    public AuthException(final ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthException(final ErrorCode errorCode, final String message) {
        super(errorCode, message);
    }
}

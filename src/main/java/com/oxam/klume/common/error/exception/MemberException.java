package com.oxam.klume.common.error.exception;

import com.oxam.klume.common.error.ErrorCode;

public class MemberException extends BusinessException {
    public MemberException(final ErrorCode errorCode) {
        super(errorCode);
    }

    public MemberException(final ErrorCode errorCode, final String message) {
        super(errorCode, message);
    }
}

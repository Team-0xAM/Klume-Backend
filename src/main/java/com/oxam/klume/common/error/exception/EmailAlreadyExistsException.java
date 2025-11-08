package com.oxam.klume.common.error.exception;

import com.oxam.klume.common.error.ErrorCode;

public class EmailAlreadyExistsException extends BusinessException {
    public EmailAlreadyExistsException() {
        super(ErrorCode.EMAIL_ALREADY_EXISTS);
    }
}

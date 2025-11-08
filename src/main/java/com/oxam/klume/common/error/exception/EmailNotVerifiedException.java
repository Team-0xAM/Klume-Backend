package com.oxam.klume.common.error.exception;

import com.oxam.klume.common.error.ErrorCode;

public class EmailNotVerifiedException extends BusinessException {
    public EmailNotVerifiedException() {
        super(ErrorCode.EMAIL_NOT_VERIFIED);
    }
}

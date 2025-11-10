package com.oxam.klume.common.error.exception;

import com.oxam.klume.common.error.ErrorCode;

public class VerificationCodeNotFoundException extends BusinessException {
    public VerificationCodeNotFoundException() {
        super(ErrorCode.VERIFICATION_CODE_NOT_FOUND);
    }
}

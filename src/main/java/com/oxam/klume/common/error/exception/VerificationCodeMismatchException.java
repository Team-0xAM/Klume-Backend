package com.oxam.klume.common.error.exception;

import com.oxam.klume.common.error.ErrorCode;

public class VerificationCodeMismatchException extends BusinessException {
    public VerificationCodeMismatchException() {
        super(ErrorCode.VERIFICATION_CODE_MISMATCH);
    }
}

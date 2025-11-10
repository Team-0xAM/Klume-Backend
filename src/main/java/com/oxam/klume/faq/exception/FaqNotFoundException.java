package com.oxam.klume.faq.exception;

import com.oxam.klume.common.error.ErrorCode;
import com.oxam.klume.common.error.exception.BusinessException;

public class FaqNotFoundException extends BusinessException {
    public FaqNotFoundException() {
        super(ErrorCode.FAQ_NOT_FOUND);
    }

    public FaqNotFoundException(final String message) {
        super(ErrorCode.FAQ_NOT_FOUND, message);
    }
}
package com.oxam.klume.room.exception;

import com.oxam.klume.common.error.ErrorCode;
import com.oxam.klume.common.error.exception.BusinessException;

public class AvailableTimeNotFoundException extends BusinessException {
    public AvailableTimeNotFoundException() {
        super(ErrorCode.AVAILABLE_TIME_NOT_FOUND);
    }

    public AvailableTimeNotFoundException(final String message) {
        super(ErrorCode.AVAILABLE_TIME_NOT_FOUND, message);
    }
}

package com.oxam.klume.room.exception;

import com.oxam.klume.common.error.ErrorCode;
import com.oxam.klume.common.error.exception.BusinessException;

public class DailyAvailableTimeNotFoundException extends BusinessException {
    public DailyAvailableTimeNotFoundException() {
        super(ErrorCode.DAILY_AVAILABLE_TIME_NOT_FOUND);
    }

    public DailyAvailableTimeNotFoundException(final String message) {
        super(ErrorCode.DAILY_AVAILABLE_TIME_NOT_FOUND, message);
    }
}

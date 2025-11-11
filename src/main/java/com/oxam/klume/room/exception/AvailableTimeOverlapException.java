package com.oxam.klume.room.exception;

import com.oxam.klume.common.error.ErrorCode;
import com.oxam.klume.common.error.exception.BusinessException;

public class AvailableTimeOverlapException extends BusinessException {
    public AvailableTimeOverlapException() {
        super(ErrorCode.AVAILABLE_TIME_OVERLAP);
    }
}


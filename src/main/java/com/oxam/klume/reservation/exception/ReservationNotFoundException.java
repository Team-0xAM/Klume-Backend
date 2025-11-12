package com.oxam.klume.reservation.exception;

import com.oxam.klume.common.error.ErrorCode;
import com.oxam.klume.common.error.exception.BusinessException;

public class ReservationNotFoundException extends BusinessException {
    public ReservationNotFoundException(final String message) {
        super(ErrorCode.RESERVATION_NOT_FOUND, message);
    }

}

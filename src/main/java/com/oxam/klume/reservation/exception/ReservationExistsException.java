package com.oxam.klume.reservation.exception;

import com.oxam.klume.common.error.ErrorCode;
import com.oxam.klume.common.error.exception.BusinessException;

public class ReservationExistsException extends BusinessException {
    public ReservationExistsException() {
        super(ErrorCode.RESERVATION_ALREADY_EXISTS);
    }
}

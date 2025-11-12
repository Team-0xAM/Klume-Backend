package com.oxam.klume.reservation.exception;

import com.oxam.klume.common.error.ErrorCode;
import com.oxam.klume.common.error.exception.BusinessException;

public class ReservationAlreadyStartedException extends BusinessException {
    public ReservationAlreadyStartedException() {
        super(ErrorCode.RESERVATION_ALREADY_STARTED, "회의실 이용시간이 이미 시작되었습니다.");
    }
}

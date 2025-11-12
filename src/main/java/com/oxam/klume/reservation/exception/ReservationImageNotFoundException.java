package com.oxam.klume.reservation.exception;

import com.oxam.klume.common.error.ErrorCode;
import com.oxam.klume.common.error.exception.BusinessException;

public class ReservationImageNotFoundException extends BusinessException {
    public ReservationImageNotFoundException() {
        super(ErrorCode.RESERVATION_IMAGE_NOT_FOUND, "회의실 이용 인증 이미지가 존재하지 않습니다.");
    }
}

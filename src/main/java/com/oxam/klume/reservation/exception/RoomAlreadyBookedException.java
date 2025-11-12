package com.oxam.klume.reservation.exception;

import com.oxam.klume.common.error.ErrorCode;
import com.oxam.klume.common.error.exception.BusinessException;

public class RoomAlreadyBookedException extends BusinessException {
    public RoomAlreadyBookedException() {
        super(ErrorCode.ROOM_ALREADY_BOOKED);
    }
}
package com.oxam.klume.room.exception;

import com.oxam.klume.common.error.ErrorCode;
import com.oxam.klume.common.error.exception.BusinessException;

public class RoomNotFoundException extends BusinessException {
    public RoomNotFoundException() {
        super(ErrorCode.ROOM_NOT_FOUND);
    }

    public RoomNotFoundException(final String message) {
        super(ErrorCode.ROOM_NOT_FOUND, message);
    }
}

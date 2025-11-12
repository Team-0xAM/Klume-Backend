package com.oxam.klume.room.exception;

public class RoomCapacityInvalidException extends RoomException {
    public RoomCapacityInvalidException() {
        super("수용 인원은 1명 이상이어야 합니다.");
    }
}
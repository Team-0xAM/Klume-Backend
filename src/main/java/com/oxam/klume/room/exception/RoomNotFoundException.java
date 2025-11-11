package com.oxam.klume.room.exception;

public class RoomNotFoundException extends RoomException {
    public RoomNotFoundException() {
        super("회의실을 찾을 수 없습니다.");
    }
}
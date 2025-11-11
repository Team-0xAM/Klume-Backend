package com.oxam.klume.room.exception;

public class RoomNameDuplicationException extends RuntimeException {

    public RoomNameDuplicationException() {
        super("이미 동일한 이름의 회의실이 존재합니다.");
    }

    public RoomNameDuplicationException(String message) {
        super(message);
    }
}
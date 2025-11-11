package com.oxam.klume.room.exception;

public abstract class RoomException extends RuntimeException {
    public RoomException(String message) {
        super(message);
    }
}
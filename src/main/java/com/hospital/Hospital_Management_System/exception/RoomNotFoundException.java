package com.hospital.Hospital_Management_System.exception;

public class RoomNotFoundException extends RuntimeException {
    public RoomNotFoundException(String roomNotFound) {
        super(roomNotFound);
    }
}

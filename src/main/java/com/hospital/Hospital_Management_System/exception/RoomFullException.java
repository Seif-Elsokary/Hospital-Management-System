package com.hospital.Hospital_Management_System.exception;

public class RoomFullException extends RuntimeException {
    public RoomFullException(String s) {
        super(s);
    }
}

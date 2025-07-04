package com.hospital.Hospital_Management_System.exception;

public class AppointmentNotFoundException extends RuntimeException{
    public AppointmentNotFoundException(String s) {
        super(s);
    }
}

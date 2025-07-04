package com.hospital.Hospital_Management_System.exception;

public class DoctorNotFoundException extends RuntimeException {
    public DoctorNotFoundException(String s) {
        super(s);
    }
}

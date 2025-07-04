package com.hospital.Hospital_Management_System.exception;

public class PrescriptionNotFoundException extends RuntimeException{
    public PrescriptionNotFoundException(String message){
        super(message);
    }
}

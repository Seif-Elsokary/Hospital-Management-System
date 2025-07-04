package com.hospital.Hospital_Management_System.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<Object> handlePatientNotFound(PatientNotFoundException ex) {
        return new ResponseEntity<>(buildErrorMap(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MedicalRecordNotFoundException.class)
    public ResponseEntity<Object> MedicalRecordNotFound(MedicalRecordNotFoundException ex) {
        return new ResponseEntity<>(buildErrorMap(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PrescriptionNotFoundException.class)
    public ResponseEntity<Object> PrescriptionNotFound(PrescriptionNotFoundException ex) {
        return new ResponseEntity<>(buildErrorMap(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RoomFullException.class)
    public ResponseEntity<Object>RoomFull(RoomFullException ex) {
        return new ResponseEntity<>(buildErrorMap(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RoomNotAvailableException.class)
    public ResponseEntity<Object> RoomNotAvailable(RoomNotAvailableException ex) {
        return new ResponseEntity<>(buildErrorMap(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DoctorNotFoundException.class)
    public ResponseEntity<Object> handleDoctorNotFound(DoctorNotFoundException ex) {
        return new ResponseEntity<>(buildErrorMap(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AppointmentNotFoundException.class)
    public ResponseEntity<Object> handleAppointmentNotFound(AppointmentNotFoundException ex) {
        return new ResponseEntity<>(buildErrorMap(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<Object> handleAlreadyExists(AlreadyExistsException ex) {
        return new ResponseEntity<>(buildErrorMap(ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex) {
        return new ResponseEntity<>(buildErrorMap(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleOtherExceptions(Exception ex) {
        return new ResponseEntity<>(buildErrorMap("Something went wrong: " + ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Map<String, String> buildErrorMap(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}

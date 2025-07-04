package com.hospital.Hospital_Management_System.exception;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class AlreadyExistsException extends RuntimeException {
    public AlreadyExistsException(@NotBlank(message = "Phone is required") @Pattern(regexp = "^(01)[0-9]{9}$",
            message = "Phone number must be a valid Egyptian number") String message) {
        super(message);
    }
}

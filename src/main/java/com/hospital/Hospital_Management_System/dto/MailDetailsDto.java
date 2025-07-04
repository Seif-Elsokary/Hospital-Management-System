package com.hospital.Hospital_Management_System.dto;

import lombok.*;

@Data
@Builder
public class MailDetailsDto {
    private String toMail;
    private String message;
    private String subject;
    private String contentType;
}

package com.hospital.Hospital_Management_System.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class PrescriptionDto {

    private Long id;
    private String medicineDescription;
    private Date issueDate;
    private Long doctorId;
    private String doctorName;
    private String doctorSpecialty;
    private Long patientId;
    private String patientName;
    private String patientDisease;
}

package com.hospital.Hospital_Management_System.dto;

import com.hospital.Hospital_Management_System.enums.Disease;
import jakarta.persistence.Column;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class MedicalRecordDto {
    private Long id;
    private String diagnosis;
    private String treatment;
    private Date visitDate;
    private String notes;
    private Long patientId;
    private String patientName;
    private Long doctorId;
    private String doctorName;
}

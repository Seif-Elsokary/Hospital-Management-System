package com.hospital.Hospital_Management_System.request.MedicalRecordRequest;

import com.hospital.Hospital_Management_System.enums.Disease;
import jakarta.persistence.Column;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class CreateMedicalRecordRequest {

    @Column(nullable = false)
    private String diagnosis;

    @Column(nullable = false)
    private String treatment;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date visitDate;

    @Column(length = 1000)
    private String notes;

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;
}

package com.hospital.Hospital_Management_System.request.MedicalReportRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class CreateMedicalReportRequest {

    @NotBlank(message = "Report title is required")
    private String reportTitle;

    @NotNull(message = "Report date is required")
    private Date reportDate;

    @NotBlank(message = "Report content is required")
    private String content;

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    private Long medicalRecordId; // Optional
}

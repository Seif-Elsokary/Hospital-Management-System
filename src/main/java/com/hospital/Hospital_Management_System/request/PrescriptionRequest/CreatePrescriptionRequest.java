package com.hospital.Hospital_Management_System.request.PrescriptionRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class CreatePrescriptionRequest {

    @NotBlank(message = "Description is required")
    private String medicineDescription;

    @NotNull(message = "Issue date is required")
    private Date issueDate;

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    private Long medicalRecordId;
}

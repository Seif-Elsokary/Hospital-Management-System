package com.hospital.Hospital_Management_System.request.PrescriptionRequest;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class UpdatePrescriptionRequest {

    @NotNull(message = "Prescription ID is required")
    private Long id;

    private String medicineDescription;

    private Date issueDate;

    private Long patientId;

    private Long doctorId;

    private Long medicalRecordId;
}

package com.hospital.Hospital_Management_System.request.MedicalReportRequest;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class UpdateMedicalReportRequest {

    private String reportTitle;

    private Date reportDate;

    private String content;

    private Long patientId;

    private Long doctorId;

    private Long medicalRecordId;
}

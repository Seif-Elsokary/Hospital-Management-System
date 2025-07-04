package com.hospital.Hospital_Management_System.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class MedicalReportDto {

    private Long id;
    private String reportTitle;

    private Date reportDate;

    private String content;

    private Long patientId;
    private String PatientName;

    private Long doctorId;
    private String doctorName;

}

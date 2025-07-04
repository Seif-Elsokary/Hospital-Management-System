package com.hospital.Hospital_Management_System.dto;

import com.hospital.Hospital_Management_System.enums.AppointmentStatus;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class DoctorAppointmentDto {

    private Long id;

    private Date date;

    private String reason;

    private AppointmentStatus status;

    private Long patientId;

    private String patientName;

    private String disease;

}

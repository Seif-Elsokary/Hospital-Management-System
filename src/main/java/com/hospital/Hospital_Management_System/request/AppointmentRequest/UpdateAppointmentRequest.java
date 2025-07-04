package com.hospital.Hospital_Management_System.request.AppointmentRequest;

import com.hospital.Hospital_Management_System.enums.AppointmentStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class UpdateAppointmentRequest {

    @NotNull(message = "Date is required")
    @Future(message = "Appointment date must be in the future")
    private Date date;

    @NotBlank(message = "Reason is required")
    private String reason;

    @NotNull(message = "Status is required")
    private AppointmentStatus status;

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;
}

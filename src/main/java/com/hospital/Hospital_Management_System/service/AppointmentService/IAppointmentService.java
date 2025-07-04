package com.hospital.Hospital_Management_System.service.AppointmentService;

import com.hospital.Hospital_Management_System.dto.AppointmentDto;
import com.hospital.Hospital_Management_System.request.AppointmentRequest.CreateAppointmentRequest;
import com.hospital.Hospital_Management_System.request.AppointmentRequest.UpdateAppointmentRequest;

import java.util.List;

public interface IAppointmentService {

    AppointmentDto createAppointment(CreateAppointmentRequest request);

    AppointmentDto updateAppointment(Long id, UpdateAppointmentRequest request);

    void deleteAppointment(Long id);

    AppointmentDto getAppointmentById(Long id);

    List<AppointmentDto> getAllAppointments();

    List<AppointmentDto> getAppointmentsByDoctorId(Long doctorId);

    List<AppointmentDto> getAppointmentsByPatientId(Long patientId);
}

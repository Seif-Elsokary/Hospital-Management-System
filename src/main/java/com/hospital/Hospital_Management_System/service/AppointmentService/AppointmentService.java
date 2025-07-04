package com.hospital.Hospital_Management_System.service.AppointmentService;

import com.hospital.Hospital_Management_System.dto.AppointmentDto;
import com.hospital.Hospital_Management_System.dto.DoctorAppointmentDto;
import com.hospital.Hospital_Management_System.dto.MailDetailsDto;
import com.hospital.Hospital_Management_System.entity.Appointment;
import com.hospital.Hospital_Management_System.entity.Doctor;
import com.hospital.Hospital_Management_System.entity.Patient;
import com.hospital.Hospital_Management_System.enums.DiseaseSpecialtyMapper;
import com.hospital.Hospital_Management_System.enums.Specialty;
import com.hospital.Hospital_Management_System.exception.*;
import com.hospital.Hospital_Management_System.repository.AppointmentRepository;
import com.hospital.Hospital_Management_System.repository.DoctorRepository;
import com.hospital.Hospital_Management_System.repository.PatientRepository;
import com.hospital.Hospital_Management_System.request.AppointmentRequest.CreateAppointmentRequest;
import com.hospital.Hospital_Management_System.request.AppointmentRequest.UpdateAppointmentRequest;
import com.hospital.Hospital_Management_System.service.MailService.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService implements IAppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final MailService mailService;

    @Override
    public AppointmentDto createAppointment(CreateAppointmentRequest request) {
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new DoctorNotFoundException("Doctor with ID: " + request.getDoctorId() + " not found"));

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new PatientNotFoundException("Patient with ID: " + request.getPatientId() + " not found"));

        if (!isDoctorSpecialtyMatchingPatient(doctor, patient)) {
            List<Doctor> alternativeDoctors = getAlternativeDoctorsForPatient(patient);
            if (alternativeDoctors.isEmpty()) {
                throw new RuntimeException("This patient's condition requires a specialty that is currently unavailable.");
            } else {
                throw new RuntimeException("Selected doctor does not match the required specialty. Please choose one of the following available doctors: " +
                        alternativeDoctors.stream()
                                .map(d -> d.getName() + " (ID: " + d.getId() + ")")
                                .toList());
            }
        }

        Appointment appointment = createNewAppointment(request, doctor, patient);
        appointment = appointmentRepository.save(appointment);

        sendAppointmentEmails(patient, doctor, appointment);

        return convertToAppointmentDto(appointment);
    }

    @Override
    public AppointmentDto updateAppointment(Long id, UpdateAppointmentRequest request) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment with ID: " + id + " not found"));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new DoctorNotFoundException("Doctor with ID: " + request.getDoctorId() + " not found"));

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new PatientNotFoundException("Patient with ID: " + request.getPatientId() + " not found"));

        if (!isDoctorSpecialtyMatchingPatient(doctor, patient)) {
            List<Doctor> alternativeDoctors = getAlternativeDoctorsForPatient(patient);
            if (alternativeDoctors.isEmpty()) {
                throw new RuntimeException("This patient's condition requires a specialty that is currently unavailable.");
            } else {
                throw new RuntimeException("Selected doctor does not match the required specialty. Please choose one of the following available doctors: " +
                        alternativeDoctors.stream()
                                .map(d -> d.getName() + " (ID: " + d.getId() + ")")
                                .toList());
            }
        }

        appointment = updateExistingAppointment(request, appointment, doctor, patient);
        appointment = appointmentRepository.save(appointment);

        return convertToAppointmentDto(appointment);
    }

    @Override
    public void deleteAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment With ID: " + id + " Not Found!"));

        appointmentRepository.delete(appointment);

        // Send cancellation emails (optional)
        sendAppointmentEmails(appointment.getPatient(), appointment.getDoctor(), appointment);
    }

    @Override
    public AppointmentDto getAppointmentById(Long id) {
        return convertToAppointmentDto(appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment With ID: " + id + " Not Found!")));
    }

    @Override
    public List<AppointmentDto> getAllAppointments() {
        return convertToAppointmentDtoList(appointmentRepository.findAll());
    }

    @Override
    public List<AppointmentDto> getAppointmentsByDoctorId(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor with ID: " + doctorId + " Not Found!"));

        return convertToAppointmentDtoList(appointmentRepository.findByDoctorId(doctor));
    }

    @Override
    public List<AppointmentDto> getAppointmentsByPatientId(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException("Patient with ID: " + patientId + " Not Found!"));

        return convertToAppointmentDtoList(appointmentRepository.findByPatientId(patient));
    }

    // ============ Helper Methods ============

    private AppointmentDto convertToAppointmentDto(Appointment appointment) {
        return AppointmentDto.builder()
                .id(appointment.getId())
                .reason(appointment.getReason())
                .date(appointment.getDate())
                .status(appointment.getStatus())
                .doctorId(appointment.getDoctor().getId())
                .doctorName(appointment.getDoctor().getName())
                .patientId(appointment.getPatient().getId())
                .patientName(appointment.getPatient().getName())
                .disease(String.valueOf(appointment.getPatient().getDisease()))
                .specialty(String.valueOf(appointment.getDoctor().getSpecialty()))
                .build();
    }

    private List<AppointmentDto> convertToAppointmentDtoList(List<Appointment> appointments) {
        return appointments.stream().map(this::convertToAppointmentDto).toList();
    }

    private Appointment createNewAppointment(CreateAppointmentRequest request, Doctor doctor, Patient patient) {
        return Appointment.builder()
                .date(request.getDate())
                .reason(request.getReason())
                .status(request.getStatus())
                .doctor(doctor)
                .patient(patient)
                .build();
    }

    private Appointment updateExistingAppointment(UpdateAppointmentRequest request, Appointment appointment,
                                                  Doctor doctor, Patient patient) {
        appointment.setDate(request.getDate());
        appointment.setReason(request.getReason());
        appointment.setStatus(request.getStatus());
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        return appointment;
    }

    public boolean isDoctorSpecialtyMatchingPatient(Doctor doctor, Patient patient) {
        Specialty expectedSpecialty = DiseaseSpecialtyMapper.getSpecialtyByDisease(patient.getDisease());
        return expectedSpecialty != null && expectedSpecialty.equals(doctor.getSpecialty());
    }

    public List<Doctor> getAlternativeDoctorsForPatient(Patient patient) {
        Specialty expectedSpecialty = DiseaseSpecialtyMapper.getSpecialtyByDisease(patient.getDisease());
        return doctorRepository.findBySpecialty(expectedSpecialty);
    }

    private void sendAppointmentEmails(Patient patient, Doctor doctor, Appointment appointment) {

        String patientHtml = """
        <html>
        <body style="font-family: Arial, sans-serif;">
            <h2 style="color: #2E86C1;">Appointment Confirmation</h2>
            <p>Dear <strong>%s</strong>,</p>
            <p>Your appointment with Dr. <strong>%s</strong> is confirmed.</p>
            <p><strong>Date:</strong> %s</p>
            <p><strong>Reason:</strong> %s</p>
            <p style="color: red;">If you can't attend, please reschedule.</p>
        </body>
        </html>
        """.formatted(patient.getName(), doctor.getName(), appointment.getDate(), appointment.getReason());

        mailService.sendMail(MailDetailsDto.builder()
                .toMail(patient.getEmail())
                .subject("✅ Appointment Confirmation")
                .message(patientHtml)
                .contentType("html")
                .build());

        // Email to doctor
        String doctorHtml = """
        <html>
        <body style="font-family: Arial, sans-serif;">
            <h2 style="color: #27AE60;">📥 New Appointment Booked</h2>
            <p>Dear Dr. <strong>%s</strong>,</p>
            <p>A new appointment has been booked:</p>
            <ul>
                <li><strong>Patient:</strong> %s</li>
                <li><strong>Email:</strong> %s</li>
                <li><strong>Phone:</strong> %s</li>
                <li><strong>Disease:</strong> %s</li>
                <li><strong>Date:</strong> %s</li>
                <li><strong>Reason:</strong> %s</li>
            </ul>
        </body>
        </html>
        """.formatted(doctor.getName(), patient.getName(), patient.getEmail(), patient.getPhone(),
                patient.getDisease(), appointment.getDate(), appointment.getReason());

        mailService.sendMail(MailDetailsDto.builder()
                .toMail(doctor.getEmail())
                .subject("📥 New Appointment Booked")
                .message(doctorHtml)
                .contentType("html")
                .build());
    }

    // ============ Scheduler Support ============

    public void sendUpcomingReminders() {
        List<Appointment> appointments = appointmentRepository.findAll();

        LocalDate targetDate = LocalDate.now().plusDays(2);

        appointments.stream()
                .filter(app -> {
                    LocalDate appDate = app.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    return appDate.equals(targetDate);
                })
                .forEach(app -> {
                    String html = """
                        <html>
                        <body style='font-family:Arial,sans-serif'>
                          <h2 style='color:#f39c12'>⏰ Reminder: Appointment in 2 days</h2>
                          <p>Dear %s,</p>
                          <p>This is a reminder that your appointment with Dr. %s is scheduled in 2 days.</p>
                          <p><strong>Date:</strong> %s</p>
                          <p>Please make sure to attend or reschedule if needed.</p>
                        </body>
                        </html>
                        """.formatted(app.getPatient().getName(), app.getDoctor().getName(), app.getDate());

                    mailService.sendMail(MailDetailsDto.builder()
                            .toMail(app.getPatient().getEmail())
                            .subject("⏰ Appointment Reminder")
                            .message(html)
                            .contentType("html")
                            .build());
                });
    }
}

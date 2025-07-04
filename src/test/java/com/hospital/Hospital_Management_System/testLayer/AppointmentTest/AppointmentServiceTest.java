package com.hospital.Hospital_Management_System.testLayer.AppointmentTest;

import com.hospital.Hospital_Management_System.dto.AppointmentDto;
import com.hospital.Hospital_Management_System.entity.Appointment;
import com.hospital.Hospital_Management_System.entity.Doctor;
import com.hospital.Hospital_Management_System.entity.Patient;
import com.hospital.Hospital_Management_System.enums.*;
import com.hospital.Hospital_Management_System.repository.AppointmentRepository;
import com.hospital.Hospital_Management_System.repository.DoctorRepository;
import com.hospital.Hospital_Management_System.repository.PatientRepository;
import com.hospital.Hospital_Management_System.request.AppointmentRequest.CreateAppointmentRequest;
import com.hospital.Hospital_Management_System.request.AppointmentRequest.UpdateAppointmentRequest;
import com.hospital.Hospital_Management_System.service.AppointmentService.AppointmentService;
import com.hospital.Hospital_Management_System.service.MailService.MailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppointmentServiceTest {

    @InjectMocks
    private AppointmentService appointmentService;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private MailService mailService;

    private Doctor doctor;
    private Patient patient;
    private CreateAppointmentRequest createRequest;
    private UpdateAppointmentRequest updateRequest;
    private Appointment savedAppointment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        doctor = Doctor.builder()
                .id(1L)
                .name("Dr. Ahmed")
                .email("dr@example.com")
                .specialty(Specialty.CARDIOLOGY)
                .build();

        patient = Patient.builder()
                .id(1L)
                .name("Ali")
                .email("ali@example.com")
                .gender(Gender.MALE)
                .disease(Disease.HEART_DISEASE)
                .build();

        createRequest = CreateAppointmentRequest.builder()
                .doctorId(1L)
                .patientId(1L)
                .reason("Chest Pain")
                .status(AppointmentStatus.PENDING)
                .date(new Date(System.currentTimeMillis() + 86400000))
                .build();

        updateRequest = UpdateAppointmentRequest.builder()
                .doctorId(1L)
                .patientId(1L)
                .reason("Updated Reason")
                .status(AppointmentStatus.CANCELED)
                .date(new Date(System.currentTimeMillis() + 172800000))
                .build();

        savedAppointment = Appointment.builder()
                .id(1L)
                .doctor(doctor)
                .patient(patient)
                .reason(createRequest.getReason())
                .status(createRequest.getStatus())
                .date(createRequest.getDate())
                .build();
    }

    @Test
    void testCreateAppointmentSuccess() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(appointmentRepository.save(any())).thenReturn(savedAppointment);

        AppointmentDto result = appointmentService.createAppointment(createRequest);

        assertNotNull(result);
        assertEquals("Ali", result.getPatientName());
        assertEquals("Dr. Ahmed", result.getDoctorName());
        verify(mailService, times(2)).sendMail(any());
    }

    @Test
    void testCreateAppointmentWithMismatchedSpecialty() {
        doctor.setSpecialty(Specialty.DERMATOLOGY);
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findBySpecialty(Specialty.CARDIOLOGY)).thenReturn(new ArrayList<>());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            appointmentService.createAppointment(createRequest);
        });

        assertTrue(exception.getMessage().contains("currently unavailable"));
    }

    @Test
    void testUpdateAppointment() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(savedAppointment));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(appointmentRepository.save(any())).thenReturn(savedAppointment);

        AppointmentDto updated = appointmentService.updateAppointment(1L, updateRequest);

        assertEquals("Updated Reason", updated.getReason());
    }

    @Test
    void testDeleteAppointment() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(savedAppointment));
        appointmentService.deleteAppointment(1L);
        verify(appointmentRepository).delete(savedAppointment);
        verify(mailService, times(2)).sendMail(any());
    }

    @Test
    void testGetAppointmentById() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(savedAppointment));
        AppointmentDto result = appointmentService.getAppointmentById(1L);
        assertEquals("Ali", result.getPatientName());
    }

    @Test
    void testGetAllAppointments() {
        when(appointmentRepository.findAll()).thenReturn(List.of(savedAppointment));
        List<AppointmentDto> result = appointmentService.getAllAppointments();
        assertEquals(1, result.size());
    }

    @Test
    void testGetAppointmentsByDoctorId() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findByDoctorId(doctor)).thenReturn(List.of(savedAppointment));
        List<AppointmentDto> result = appointmentService.getAppointmentsByDoctorId(1L);
        assertEquals(1, result.size());
    }

    @Test
    void testGetAppointmentsByPatientId() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(appointmentRepository.findByPatientId(patient)).thenReturn(List.of(savedAppointment));
        List<AppointmentDto> result = appointmentService.getAppointmentsByPatientId(1L);
        assertEquals(1, result.size());
    }
}

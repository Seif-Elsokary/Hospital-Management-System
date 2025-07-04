package com.hospital.Hospital_Management_System.testLayer.DoctorTest;

import com.hospital.Hospital_Management_System.dto.DoctorDto;
import com.hospital.Hospital_Management_System.entity.Doctor;
import com.hospital.Hospital_Management_System.enums.Gender;
import com.hospital.Hospital_Management_System.enums.Specialty;
import com.hospital.Hospital_Management_System.exception.AlreadyExistsException;
import com.hospital.Hospital_Management_System.exception.DoctorNotFoundException;
import com.hospital.Hospital_Management_System.repository.DoctorRepository;
import com.hospital.Hospital_Management_System.request.DoctorRequest.CreateDoctorRequest;
import com.hospital.Hospital_Management_System.request.DoctorRequest.UpdateDoctorRequest;
import com.hospital.Hospital_Management_System.service.DoctorService.DoctorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DoctorServiceTest {

    @InjectMocks
    private DoctorService doctorService;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AutoCloseable closeable;

    private CreateDoctorRequest createDoctorRequest;
    private UpdateDoctorRequest updateDoctorRequest;
    private Doctor doctor;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");

        createDoctorRequest = CreateDoctorRequest.builder()
                .name("saif")
                .email("seif@example.com")
                .specialty(Specialty.ENT)
                .age(22)
                .yearOfExperience(4)
                .gender(Gender.MALE)
                .phone("01020304050")
                .address("Mansoura")
                .build();

        updateDoctorRequest = UpdateDoctorRequest.builder()
                .name("Updated Saif")
                .email("seif@example.com")
                .specialty(Specialty.ENT)
                .age(30)
                .yearOfExperience(6)
                .gender(Gender.MALE)
                .phone("01020304050")
                .address("Cairo")
                .build();

        doctor = Doctor.builder()
                .id(1L)
                .name("saif")
                .email("seif@example.com")
                .specialty(Specialty.ENT)
                .age(22)
                .yearOfExperience(4)
                .gender(Gender.MALE)
                .phone("01020304050")
                .address("Mansoura")
                .build();
    }

    @Test
    void testCreateDoctor_Success() {
        when(doctorRepository.existsByEmail(createDoctorRequest.getEmail())).thenReturn(false);
        when(doctorRepository.existsByPhone(createDoctorRequest.getPhone())).thenReturn(false);
        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor);

        DoctorDto result = doctorService.createDoctor(createDoctorRequest);

        assertNotNull(result);
        assertEquals("saif", result.getName());
        verify(doctorRepository, times(1)).save(any(Doctor.class));
    }

    @Test
    void testCreateDoctor_EmailOrPhoneAlreadyExists() {
        when(doctorRepository.existsByEmail(createDoctorRequest.getEmail())).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> {
            doctorService.createDoctor(createDoctorRequest);
        });
    }

    @Test
    void testUpdateDoctor_Success() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor);

        DoctorDto result = doctorService.updateDoctor(updateDoctorRequest, 1L);

        assertNotNull(result);
        assertEquals("Updated Saif", result.getName());
        verify(doctorRepository, times(1)).save(any(Doctor.class));
    }

    @Test
    void testGetDoctorById_Success() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        DoctorDto result = doctorService.getDoctorById(1L);

        assertNotNull(result);
        assertEquals("saif", result.getName());
    }

    @Test
    void testGetDoctorById_NotFound() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DoctorNotFoundException.class, () -> doctorService.getDoctorById(1L));
    }

    @Test
    void testDeleteDoctorById_Success() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        assertDoesNotThrow(() -> doctorService.deleteDoctorById(1L));
        verify(doctorRepository, times(1)).delete(any(Doctor.class));
    }

    @Test
    void testDeleteDoctorById_NotFound() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DoctorNotFoundException.class, () -> doctorService.deleteDoctorById(1L));
    }
}

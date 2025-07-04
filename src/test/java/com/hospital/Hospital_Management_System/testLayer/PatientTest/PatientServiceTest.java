package com.hospital.Hospital_Management_System.testLayer.PatientTest;

import com.hospital.Hospital_Management_System.dto.PatientDto;
import com.hospital.Hospital_Management_System.entity.Patient;
import com.hospital.Hospital_Management_System.enums.Disease;
import com.hospital.Hospital_Management_System.enums.Gender;
import com.hospital.Hospital_Management_System.exception.PatientNotFoundException;
import com.hospital.Hospital_Management_System.repository.PatientRepository;
import com.hospital.Hospital_Management_System.request.PatientRequest.CreatePatientRequest;
import com.hospital.Hospital_Management_System.request.PatientRequest.UpdatePatientRequest;
import com.hospital.Hospital_Management_System.service.PatientService.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PatientServiceTest {

    @InjectMocks
    private PatientService patientService;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
    }

    @Test
    void testCreatePatient_Success() {
        CreatePatientRequest request = CreatePatientRequest.builder()
                .name("Ali")
                .email("ali@example.com")
                .phone("01012345678")
                .address("Cairo")
                .age(30)
                .bloodType("A+")
                .disease(Disease.FLU)
                .gender(Gender.MALE)
                .dateOfRegistration(new Date())
                .build();

        when(patientRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(patientRepository.existsByPhone(request.getPhone())).thenReturn(false);

        Patient savedPatient = Patient.builder()
                .id(1L)
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .age(request.getAge())
                .bloodType(request.getBloodType())
                .disease(request.getDisease())
                .gender(request.getGender())
                .dateOfRegistration(request.getDateOfRegistration())
                .build();

        when(patientRepository.save(any(Patient.class))).thenReturn(savedPatient);

        PatientDto result = patientService.createPatient(request);

        assertNotNull(result);
        assertEquals("Ali", result.getName());
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    void testGetPatientById_Success() {
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setName("Ali");

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        PatientDto dto = patientService.getPatientById(1L);

        assertEquals("Ali", dto.getName());
    }

    @Test
    void testGetPatientById_NotFound() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PatientNotFoundException.class, () -> patientService.getPatientById(1L));
    }

    @Test
    void testUpdatePatient_Success() {
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setName("Old Name");

        UpdatePatientRequest update = new UpdatePatientRequest();
        update.setName("New Name");

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        PatientDto result = patientService.updatePatient(update, 1L);

        assertEquals("New Name", result.getName());
    }

    @Test
    void testDeletePatientById_Success() {
        Patient patient = new Patient();
        patient.setId(1L);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        doNothing().when(patientRepository).delete(patient);

        assertDoesNotThrow(() -> patientService.deletePatientById(1L));
        verify(patientRepository, times(1)).delete(patient);
    }

    @Test
    void testGetAllPatients_ReturnsList() {
        List<Patient> patients = List.of(
                Patient.builder().id(1L).name("Ali").age(30).phone("01012345678").email("ali@example.com")
                        .address("Cairo").disease(Disease.FLU).bloodType("A+").dateOfRegistration(new Date())
                        .gender(Gender.MALE).build(),

                Patient.builder().id(2L).name("Sara").age(25).phone("01098765432").email("sara@example.com")
                        .address("Giza").disease(Disease.HEART_DISEASE).bloodType("B-").dateOfRegistration(new Date())
                        .gender(Gender.FEMALE).build()
        );

        when(patientRepository.findAll()).thenReturn(patients);

        List<PatientDto> result = patientService.getAllPatients();

        assertEquals(2, result.size());
    }

    @Test
    void testGetPatientsByName_ReturnsList() {
        List<Patient> patients = List.of(
                Patient.builder().id(1L).name("Ali").age(30).phone("01012345678").email("ali@example.com")
                        .address("Cairo").disease(Disease.FLU).bloodType("A+").dateOfRegistration(new Date())
                        .gender(Gender.MALE).build()
        );

        when(patientRepository.findByNameContainingIgnoreCase("Ali")).thenReturn(patients);

        List<PatientDto> result = patientService.getPatientsByName("Ali");

        assertEquals(1, result.size());
    }

    @Test
    void testGetPatientsByDisease_ReturnsList() {
        List<Patient> patients = List.of(
                Patient.builder().id(1L).name("Ali").age(30).phone("01012345678").email("ali@example.com")
                        .address("Cairo").disease(Disease.FLU).bloodType("A+").dateOfRegistration(new Date())
                        .gender(Gender.MALE).build()
        );

        when(patientRepository.findByDisease(Disease.FLU)).thenReturn(patients);

        List<PatientDto> result = patientService.getPatientsByDisease(Disease.FLU);

        assertEquals(1, result.size());
        assertEquals(Disease.FLU, result.get(0).getDisease());
    }

    @Test
    void testExistsByEmail_ReturnsTrue() {
        when(patientRepository.existsByEmail("ali@example.com")).thenReturn(true);
        assertTrue(patientService.existsByEmail("ali@example.com"));
    }

    @Test
    void testExistsByPhone_ReturnsFalse() {
        when(patientRepository.existsByPhone("01012345678")).thenReturn(false);
        assertFalse(patientService.existsByPhone("01012345678"));
    }

    @Test
    void testGetPatientByDateOfRegistration_ReturnsList() {
        Date date = new Date();
        List<Patient> patients = List.of(
                Patient.builder().id(1L).name("Ali").age(30).phone("01012345678").email("ali@example.com")
                        .address("Cairo").disease(Disease.FLU).bloodType("A+").dateOfRegistration(date)
                        .gender(Gender.MALE).build()
        );

        when(patientRepository.findByDateOfRegistration(date)).thenReturn(patients);

        List<PatientDto> result = patientService.getPatientByDateOfRegistration(date);
        assertEquals(1, result.size());
    }

    @Test
    void testSearchPatients_ReturnList() {
        List<Patient> patients = List.of(
                Patient.builder().id(1L).name("Ali").age(30).phone("01012345678").email("ali@example.com")
                        .address("Cairo").disease(Disease.FLU).bloodType("A+").dateOfRegistration(new Date())
                        .gender(Gender.MALE).build()
        );

        when(patientRepository.findAll(any(Specification.class))).thenReturn(patients);

        List<PatientDto> result = patientService.searchPatients("Ali", Disease.FLU, Gender.MALE, "A+", new Date());

        assertFalse(result.isEmpty());
        assertEquals("Ali", result.get(0).getName());
    }
}

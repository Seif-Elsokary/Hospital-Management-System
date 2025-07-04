package com.hospital.Hospital_Management_System.testLayer.MedicalRecordTest;

import com.hospital.Hospital_Management_System.dto.MedicalRecordDto;
import com.hospital.Hospital_Management_System.entity.Doctor;
import com.hospital.Hospital_Management_System.entity.MedicalRecord;
import com.hospital.Hospital_Management_System.entity.Patient;
import com.hospital.Hospital_Management_System.exception.MedicalRecordNotFoundException;
import com.hospital.Hospital_Management_System.repository.DoctorRepository;
import com.hospital.Hospital_Management_System.repository.MedicalRecordRepository;
import com.hospital.Hospital_Management_System.repository.PatientRepository;
import com.hospital.Hospital_Management_System.request.MedicalRecordRequest.CreateMedicalRecordRequest;
import com.hospital.Hospital_Management_System.request.MedicalRecordRequest.UpdateMedicalRecordRequest;
import com.hospital.Hospital_Management_System.service.MailService.MailService;
import com.hospital.Hospital_Management_System.service.MedicalRecordService.MedicalRecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MedicalRecordServiceTest {

    @InjectMocks
    private MedicalRecordService medicalRecordService;

    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private MailService mailService;

    private Doctor doctor;
    private Patient patient;
    private MedicalRecord record;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        doctor = Doctor.builder().id(1L).name("Dr. John").build();
        patient = Patient.builder().id(2L).name("Ali").email("ali@example.com").build();

        record = MedicalRecord.builder()
                .id(10L).diagnosis("Flu")
                .treatment("Rest").notes("note")
                .visitDate(new Date()).doctor(doctor).patient(patient)
                .build();
    }

    @Test
    void testCreateMedicalRecord() {
        CreateMedicalRecordRequest request = new CreateMedicalRecordRequest();
        request.setDiagnosis("Flu");
        request.setTreatment("Rest");
        request.setVisitDate(new Date());
        request.setNotes("note");
        request.setDoctorId(1L);
        request.setPatientId(2L);

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(2L)).thenReturn(Optional.of(patient));
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenAnswer(i -> i.getArgument(0));

        MedicalRecordDto dto = medicalRecordService.createMedicalRecord(request);

        assertEquals("Flu", dto.getDiagnosis());
        assertEquals("Ali", dto.getPatientName());
        verify(mailService, times(1)).sendMail(any());
    }

    @Test
    void testUpdateMedicalRecord() {
        UpdateMedicalRecordRequest request = new UpdateMedicalRecordRequest();
        request.setDiagnosis("Updated Flu");
        request.setTreatment("Updated Treatment");
        request.setVisitDate(new Date());
        request.setNotes("Updated Notes");
        request.setDoctorId(1L);
        request.setPatientId(2L);

        when(medicalRecordRepository.findById(10L)).thenReturn(Optional.of(record));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(2L)).thenReturn(Optional.of(patient));
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenAnswer(i -> i.getArgument(0));

        MedicalRecordDto dto = medicalRecordService.updateMedicalRecord(10L, request);

        assertEquals("Updated Flu", dto.getDiagnosis());
        assertEquals("Ali", dto.getPatientName());
        verify(mailService, times(1)).sendMail(any());
    }

    @Test
    void testDeleteMedicalRecord_Success() {
        when(medicalRecordRepository.findById(10L)).thenReturn(Optional.of(record));
        doNothing().when(medicalRecordRepository).delete(record);

        assertDoesNotThrow(() -> medicalRecordService.deleteMedicalRecord(10L));
        verify(medicalRecordRepository, times(1)).delete(record);
    }

    @Test
    void testDeleteMedicalRecord_NotFound() {
        when(medicalRecordRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(MedicalRecordNotFoundException.class, () -> medicalRecordService.deleteMedicalRecord(99L));
    }

    @Test
    void testGetMedicalRecordById() {
        when(medicalRecordRepository.findById(10L)).thenReturn(Optional.of(record));

        MedicalRecordDto dto = medicalRecordService.getMedicalRecordById(10L);
        assertEquals("Flu", dto.getDiagnosis());
    }

    @Test
    void testGetAllMedicalRecords() {
        when(medicalRecordRepository.findAll()).thenReturn(List.of(record));

        List<MedicalRecordDto> result = medicalRecordService.getAllMedicalRecords();
        assertEquals(1, result.size());
    }

    @Test
    void testGetRecordsByDoctorId() {
        when(medicalRecordRepository.findByDoctorId(1L)).thenReturn(List.of(record));

        List<MedicalRecordDto> result = medicalRecordService.getRecordsByDoctorId(1L);
        assertEquals(1, result.size());
    }

    @Test
    void testGetRecordsByPatientId() {
        when(patientRepository.findById(2L)).thenReturn(Optional.of(patient));
        when(medicalRecordRepository.findByPatientId(patient)).thenReturn(List.of(record));

        List<MedicalRecordDto> result = medicalRecordService.getRecordsByPatientId(2L);
        assertEquals(1, result.size());
    }
}

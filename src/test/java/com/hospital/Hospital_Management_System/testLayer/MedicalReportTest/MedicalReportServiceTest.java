package com.hospital.Hospital_Management_System.testLayer.MedicalReportTest;

import com.hospital.Hospital_Management_System.dto.MedicalReportDto;
import com.hospital.Hospital_Management_System.entity.*;
import com.hospital.Hospital_Management_System.exception.MedicalReportNotFoundException;
import com.hospital.Hospital_Management_System.repository.*;
import com.hospital.Hospital_Management_System.request.MedicalReportRequest.CreateMedicalReportRequest;
import com.hospital.Hospital_Management_System.request.MedicalReportRequest.UpdateMedicalReportRequest;
import com.hospital.Hospital_Management_System.service.MedicalReportService.MedicalReportService;
import com.hospital.Hospital_Management_System.service.MailService.MailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MedicalReportServiceTest {

    @InjectMocks
    private MedicalReportService medicalReportService;

    @Mock private MedicalReportRepository medicalReportRepository;
    @Mock private DoctorRepository doctorRepository;
    @Mock private PatientRepository patientRepository;
    @Mock private MedicalRecordRepository medicalRecordRepository;
    @Mock private MailService mailService;                     // ← إضافة الـ Mock للـ MailService

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateMedicalReport() {
        // تجهيز الطلب
        CreateMedicalReportRequest request = CreateMedicalReportRequest.builder()
                .reportTitle("Summary")
                .reportDate(new Date())
                .content("Detailed report...")
                .doctorId(1L)
                .patientId(2L)
                .medicalRecordId(3L)
                .build();

        // كيانات وهمية
        Doctor doctor = Doctor.builder().id(1L).name("Dr. A").build();
        Patient patient = Patient.builder().id(2L).name("John").build();
        MedicalRecord record = MedicalRecord.builder().id(3L).build();

        // سلوك الموك
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(2L)).thenReturn(Optional.of(patient));
        when(medicalRecordRepository.findById(3L)).thenReturn(Optional.of(record));
        when(medicalReportRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // تنفيذ
        MedicalReportDto dto = medicalReportService.createReport(request);

        // تأكيد النتائج
        assertEquals("Summary", dto.getReportTitle());
        assertEquals("Dr. A", dto.getDoctorName());
        assertEquals("John", dto.getPatientName());

        // التأكد من إرسال الإيميل
        verify(mailService, times(1)).sendMail(any());
    }

    @Test
    void testUpdateMedicalReport() {
        Long reportId = 10L;
        // كيان موجود
        Doctor doctor = Doctor.builder().id(1L).name("Dr. A").build();
        Patient patient = Patient.builder().id(2L).name("John").build();
        MedicalReport existing = MedicalReport.builder()
                .id(reportId)
                .reportTitle("Old")
                .content("Old content")
                .doctor(doctor)
                .patient(patient)
                .reportDate(new Date())
                .build();

        UpdateMedicalReportRequest request = UpdateMedicalReportRequest.builder()
                .reportTitle("New Title")
                .content("New content")
                .doctorId(1L)
                .patientId(2L)
                .medicalRecordId(null)
                .reportDate(new Date())
                .build();

        // سلوك الموك
        when(medicalReportRepository.findById(reportId)).thenReturn(Optional.of(existing));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(2L)).thenReturn(Optional.of(patient));
        when(medicalReportRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // تنفيذ
        MedicalReportDto dto = medicalReportService.updateReport(reportId, request);

        // تأكيد التحديثات
        assertEquals("New Title", dto.getReportTitle());
        assertEquals("New content", dto.getContent());

        // تأكيد إرسال الإيميل بعد التحديث
        verify(mailService, times(1)).sendMail(any());
    }

    @Test
    void testGetReportById() {
        Long id = 5L;
        Doctor doctor = Doctor.builder().id(1L).name("Dr. X").build();
        Patient patient = Patient.builder().id(2L).name("Ali").build();
        MedicalReport report = MedicalReport.builder()
                .id(id)
                .reportTitle("Report")
                .doctor(doctor)
                .patient(patient)
                .reportDate(new Date())
                .content("C")
                .build();

        when(medicalReportRepository.findById(id)).thenReturn(Optional.of(report));

        MedicalReportDto dto = medicalReportService.getReportById(id);
        assertEquals("Report", dto.getReportTitle());
        assertEquals("Ali", dto.getPatientName());
    }

    @Test
    void testGetAllReports() {
        Doctor doctor = Doctor.builder().id(1L).name("Dr. A").build();
        Patient patient = Patient.builder().id(2L).name("J").build();
        MedicalReport r = MedicalReport.builder()
                .id(1L)
                .reportTitle("R1")
                .doctor(doctor)
                .patient(patient)
                .reportDate(new Date())
                .content("X")
                .build();

        when(medicalReportRepository.findAll()).thenReturn(List.of(r));

        List<MedicalReportDto> list = medicalReportService.getAllReports();
        assertEquals(1, list.size());
        assertEquals("R1", list.get(0).getReportTitle());
    }

    @Test
    void testDeleteMedicalReport_NotFound() {
        // إذا لم يُعثر على التقرير
        doThrow(new MedicalReportNotFoundException("Not found"))
                .when(medicalReportRepository).findById(999L);
        assertThrows(MedicalReportNotFoundException.class,
                () -> medicalReportService.deleteReport(999L));
    }

    @Test
    void testGetReportsByPatientId() {
        Patient patient = Patient.builder().id(7L).name("Bob").build();
        MedicalReport r = MedicalReport.builder()
                .id(1L)
                .reportTitle("R")
                .doctor(Doctor.builder().id(3L).name("Dr. Y").build())
                .patient(patient)
                .reportDate(new Date())
                .content("Z")
                .build();

        when(medicalReportRepository.findByPatientId(7L)).thenReturn(List.of(r));

        List<MedicalReportDto> list = medicalReportService.getReportsByPatientId(7L);
        assertEquals(1, list.size());
        assertEquals("Bob", list.get(0).getPatientName());
    }
}

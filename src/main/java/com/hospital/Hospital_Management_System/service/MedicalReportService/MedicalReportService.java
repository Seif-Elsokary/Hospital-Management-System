package com.hospital.Hospital_Management_System.service.MedicalReportService;

import com.hospital.Hospital_Management_System.dto.MailDetailsDto;
import com.hospital.Hospital_Management_System.dto.MedicalReportDto;
import com.hospital.Hospital_Management_System.entity.Doctor;
import com.hospital.Hospital_Management_System.entity.MedicalRecord;
import com.hospital.Hospital_Management_System.entity.MedicalReport;
import com.hospital.Hospital_Management_System.entity.Patient;
import com.hospital.Hospital_Management_System.exception.DoctorNotFoundException;
import com.hospital.Hospital_Management_System.exception.MedicalRecordNotFoundException;
import com.hospital.Hospital_Management_System.exception.MedicalReportNotFoundException;
import com.hospital.Hospital_Management_System.exception.PatientNotFoundException;
import com.hospital.Hospital_Management_System.repository.DoctorRepository;
import com.hospital.Hospital_Management_System.repository.MedicalRecordRepository;
import com.hospital.Hospital_Management_System.repository.MedicalReportRepository;
import com.hospital.Hospital_Management_System.repository.PatientRepository;
import com.hospital.Hospital_Management_System.request.MedicalReportRequest.CreateMedicalReportRequest;
import com.hospital.Hospital_Management_System.request.MedicalReportRequest.UpdateMedicalReportRequest;
import com.hospital.Hospital_Management_System.service.MailService.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class MedicalReportService implements IMedicalReportService {

    private final MedicalReportRepository medicalReportRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final MailService mailService;

    @Override
    public MedicalReportDto createReport(CreateMedicalReportRequest request) {
        log.info("Creating medical report...");
        MedicalReport report = addNewReport(request);
        report = medicalReportRepository.save(report);
        sendMedicalReportEmailToPatient(report);
        return convertToDto(report);
    }

    @Override
    public MedicalReportDto updateReport(Long id, UpdateMedicalReportRequest request) {
        log.info("Updating medical report with ID: {}", id);
        MedicalReport existing = medicalReportRepository.findById(id)
                .orElseThrow(() -> new MedicalReportNotFoundException("MedicalReport Not Found."));
        updateMedicalReportInfo(request, existing);
        existing = medicalReportRepository.save(existing);
        sendMedicalReportEmailToPatient(existing);
        return convertToDto(existing);
    }

    @Override
    public void deleteReport(Long id) {
        medicalReportRepository.findById(id)
                .ifPresentOrElse(medicalReportRepository::delete, () -> {
                    throw new MedicalReportNotFoundException("MedicalReport Not Found.");
                });
    }

    @Override
    public MedicalReportDto getReportById(Long id) {
        return convertToDto(medicalReportRepository.findById(id)
                .orElseThrow(() -> new MedicalReportNotFoundException("MedicalReport Not Found.")));
    }

    @Override
    public List<MedicalReportDto> getAllReports() {
        return convertToDtoList(medicalReportRepository.findAll());
    }

    @Override
    public List<MedicalReportDto> getReportsByPatientId(Long patientId) {
        return convertToDtoList(medicalReportRepository.findByPatientId(patientId));
    }

    @Override
    public List<MedicalReportDto> getReportsByDoctorId(Long doctorId) {
        return convertToDtoList(medicalReportRepository.findByDoctorId(doctorId));
    }

    @Override
    public List<MedicalReportDto> getReportsByMedicalRecordId(Long recordId) {
        return convertToDtoList(medicalReportRepository.findByMedicalRecordId(recordId));
    }

    // ======================= HELPER METHODS =======================

    private MedicalReportDto convertToDto(MedicalReport report) {
        return MedicalReportDto.builder()
                .id(report.getId())
                .reportTitle(report.getReportTitle())
                .reportDate(report.getReportDate())
                .content(report.getContent())
                .doctorId(report.getDoctor().getId())
                .doctorName(report.getDoctor().getName())
                .patientId(report.getPatient().getId())
                .PatientName(report.getPatient().getName())
                .build();
    }

    private List<MedicalReportDto> convertToDtoList(List<MedicalReport> reports) {
        return reports.stream().map(this::convertToDto).toList();
    }

    private MedicalReport addNewReport(CreateMedicalReportRequest request) {
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found"));

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new PatientNotFoundException("Patient not found"));

        MedicalRecord record = medicalRecordRepository.findById(request.getMedicalRecordId())
                .orElseThrow(() -> new MedicalRecordNotFoundException("Medical Record not found"));

        return MedicalReport.builder()
                .reportTitle(request.getReportTitle())
                .reportDate(request.getReportDate())
                .content(request.getContent())
                .doctor(doctor)
                .patient(patient)
                .medicalRecord(record)
                .build();
    }

    private void updateMedicalReportInfo(UpdateMedicalReportRequest request, MedicalReport report) {
        Optional.ofNullable(request.getReportTitle()).ifPresent(report::setReportTitle);
        Optional.ofNullable(request.getContent()).ifPresent(report::setContent);
        Optional.ofNullable(request.getReportDate()).ifPresent(report::setReportDate);

        Optional.ofNullable(request.getDoctorId())
                .filter(id -> !id.equals(report.getDoctor().getId()))
                .ifPresent(id -> {
                    Doctor doctor = doctorRepository.findById(id)
                            .orElseThrow(() -> new DoctorNotFoundException("Doctor with ID: " + id + " not found"));
                    report.setDoctor(doctor);
                });

        Optional.ofNullable(request.getPatientId())
                .filter(id -> !id.equals(report.getPatient().getId()))
                .ifPresent(id -> {
                    Patient patient = patientRepository.findById(id)
                            .orElseThrow(() -> new PatientNotFoundException("Patient with ID: " + id + " not found"));
                    report.setPatient(patient);
                });

        Optional.ofNullable(request.getMedicalRecordId())
                .filter(id -> report.getMedicalRecord() == null || !id.equals(report.getMedicalRecord().getId()))
                .ifPresent(id -> {
                    MedicalRecord record = medicalRecordRepository.findById(id)
                            .orElseThrow(() -> new MedicalRecordNotFoundException("Medical Record with ID: " + id + " not found"));
                    report.setMedicalRecord(record);
                });
    }

    private void sendMedicalReportEmailToPatient(MedicalReport report) {
        String html = """
            <html>
            <body style="font-family: Arial, sans-serif;">
                <h2 style="color: #3498db;">📄 Medical Report Notification</h2>
                <p>Dear <strong>%s</strong>,</p>
                <p>Your doctor <strong>%s</strong> has issued a medical report for you.</p>
                <ul>
                    <li><strong>Report Title:</strong> %s</li>
                    <li><strong>Report Date:</strong> %s</li>
                    <li><strong>Content:</strong> %s</li>
                </ul>
                <p>If you have any questions, please contact your doctor.</p>
            </body>
            </html>
        """.formatted(
                report.getPatient().getName(),
                report.getDoctor().getName(),
                report.getReportTitle(),
                report.getReportDate(),
                report.getContent()
        );

        mailService.sendMail(MailDetailsDto.builder()
                .toMail(report.getPatient().getEmail())
                .subject("📄 New Medical Report Issued")
                .message(html)
                .contentType("html")
                .build());

        log.info("📧 Email sent to patient {} with report from doctor {}", report.getPatient().getEmail(), report.getDoctor().getName());
    }
}

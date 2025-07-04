package com.hospital.Hospital_Management_System.service.MedicalRecordService;

import com.hospital.Hospital_Management_System.dto.MailDetailsDto;
import com.hospital.Hospital_Management_System.dto.MedicalRecordDto;
import com.hospital.Hospital_Management_System.entity.Doctor;
import com.hospital.Hospital_Management_System.entity.MedicalRecord;
import com.hospital.Hospital_Management_System.entity.Patient;
import com.hospital.Hospital_Management_System.exception.DoctorNotFoundException;
import com.hospital.Hospital_Management_System.exception.MedicalRecordNotFoundException;
import com.hospital.Hospital_Management_System.exception.PatientNotFoundException;
import com.hospital.Hospital_Management_System.repository.DoctorRepository;
import com.hospital.Hospital_Management_System.repository.MedicalRecordRepository;
import com.hospital.Hospital_Management_System.repository.PatientRepository;
import com.hospital.Hospital_Management_System.request.MedicalRecordRequest.CreateMedicalRecordRequest;
import com.hospital.Hospital_Management_System.request.MedicalRecordRequest.UpdateMedicalRecordRequest;
import com.hospital.Hospital_Management_System.service.MailService.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class MedicalRecordService implements IMedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final MailService mailService;

    @Override
    public MedicalRecordDto createMedicalRecord(CreateMedicalRecordRequest request) {

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new DoctorNotFoundException("Doctor with ID: " + request.getDoctorId() + " not found"));

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new PatientNotFoundException("Patient with ID: " + request.getPatientId() + " not found"));

        if (medicalRecordRepository.existsByPatientEmailAndVisitDate(patient.getEmail(), request.getVisitDate())) {
            log.warn("Duplicate medical record creation attempt for patient email [{}] on date [{}]",
                    patient.getEmail(), request.getVisitDate());
            throw new IllegalStateException("A medical record already exists for this patient on the same date. Please update the existing record.");
        }

        MedicalRecord record = MedicalRecord.builder()
                .visitDate(request.getVisitDate())
                .notes(request.getNotes())
                .diagnosis(request.getDiagnosis())
                .treatment(request.getTreatment())
                .doctor(doctor)
                .patient(patient)
                .build();

        record = medicalRecordRepository.save(record);
        sendMedicalRecordEmailToPatient(record);
        return convertToDto(record);
    }

    @Override
    public MedicalRecordDto updateMedicalRecord(Long id, UpdateMedicalRecordRequest request) {
        MedicalRecord existing = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new MedicalRecordNotFoundException("medicalRecord with id:: " + id + " Not Found!"));

        updateMedicalRecordInfo(request, existing);
        existing = medicalRecordRepository.save(existing);
        sendMedicalRecordUpdateEmailToPatient(existing);
        return convertToDto(existing);
    }

    @Override
    public void deleteMedicalRecord(Long id) {
        medicalRecordRepository.findById(id)
                .ifPresentOrElse(medicalRecordRepository::delete, () -> {
                    throw new MedicalRecordNotFoundException("medicalRecord with id:: " + id + " Not Found!");
                });
    }

    @Override
    public MedicalRecordDto getMedicalRecordById(Long id) {
        MedicalRecord getById = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new MedicalRecordNotFoundException("medicalRecord with id:: " + id + " Not Found!"));

        return convertToDto(getById);
    }

    @Override
    public List<MedicalRecordDto> getAllMedicalRecords() {
        return convertToDtoList(medicalRecordRepository.findAll());
    }

    @Override
    public List<MedicalRecordDto> getRecordsByPatientId(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException("Patient with ID: " + patientId + " not found"));
        return convertToDtoList(medicalRecordRepository.findByPatientId(patient));
    }

    @Override
    public List<MedicalRecordDto> getRecordsByDoctorId(Long doctorId) {
        return convertToDtoList(medicalRecordRepository.findByDoctorId(doctorId));
    }

    // ============================ HELPER METHODS ============================

    private MedicalRecordDto convertToDto(MedicalRecord record) {
        return MedicalRecordDto.builder()
                .id(record.getId())
                .doctorName(record.getDoctor().getName())
                .doctorId(record.getDoctor().getId())
                .visitDate(record.getVisitDate())
                .diagnosis(record.getDiagnosis())
                .patientName(record.getPatient().getName())
                .patientId(record.getPatient().getId())
                .notes(record.getNotes())
                .treatment(record.getTreatment())
                .build();
    }

    private List<MedicalRecordDto> convertToDtoList(List<MedicalRecord> records) {
        return records.stream()
                .map(this::convertToDto)
                .toList();
    }

    private void updateMedicalRecordInfo(UpdateMedicalRecordRequest request, MedicalRecord record) {
        if (request.getDoctorId() != null && !request.getDoctorId().equals(record.getDoctor().getId())) {
            Doctor doctor = doctorRepository.findById(request.getDoctorId())
                    .orElseThrow(() -> new DoctorNotFoundException("Doctor with ID: " + request.getDoctorId() + " not found"));
            record.setDoctor(doctor);
        }

        if (request.getPatientId() != null && !request.getPatientId().equals(record.getPatient().getId())) {
            Patient patient = patientRepository.findById(request.getPatientId())
                    .orElseThrow(() -> new PatientNotFoundException("Patient with ID: " + request.getPatientId() + " not found"));
            record.setPatient(patient);
        }

        if (request.getDiagnosis() != null) record.setDiagnosis(request.getDiagnosis());
        if (request.getNotes() != null) record.setNotes(request.getNotes());
        if (request.getVisitDate() != null) record.setVisitDate(request.getVisitDate());
        if (request.getTreatment() != null) record.setTreatment(request.getTreatment());
    }

    private void sendMedicalRecordEmailToPatient(MedicalRecord record) {
        String html = """
            <html>
            <body style="font-family: Arial, sans-serif;">
                <h2 style="color: #2E86C1;">🩺 Medical Record Created</h2>
                <p>Dear <strong>%s</strong>,</p>
                <p>Your medical record has been successfully created.</p>
                <ul>
                    <li><strong>Doctor:</strong> %s</li>
                    <li><strong>Visit Date:</strong> %s</li>
                    <li><strong>Diagnosis:</strong> %s</li>
                    <li><strong>Treatment:</strong> %s</li>
                </ul>
                <p>Stay safe and take care!</p>
            </body>
            </html>
        """.formatted(
                record.getPatient().getName(),
                record.getDoctor().getName(),
                record.getVisitDate(),
                record.getDiagnosis(),
                record.getTreatment()
        );

        mailService.sendMail(MailDetailsDto.builder()
                .toMail(record.getPatient().getEmail())
                .subject("🩺 Medical Record Created")
                .message(html)
                .contentType("html")
                .build());
    }

    private void sendMedicalRecordUpdateEmailToPatient(MedicalRecord record) {
        String html = """
        <html>
        <body style="font-family: Arial, sans-serif;">
            <h2 style="color: #f39c12;">📢 Medical Record Updated</h2>
            <p>Dear <strong>%s</strong>,</p>
            <p>Your medical record has been updated. Please find the updated details below:</p>
            <ul>
                <li><strong>Doctor:</strong> %s</li>
                <li><strong>Visit Date:</strong> %s</li>
                <li><strong>Diagnosis:</strong> %s</li>
                <li><strong>Treatment:</strong> %s</li>
                <li><strong>Notes:</strong> %s</li>
            </ul>
            <p>For any concerns, please contact your doctor.</p>
        </body>
        </html>
    """.formatted(
                record.getPatient().getName(),
                record.getDoctor().getName(),
                record.getVisitDate(),
                record.getDiagnosis(),
                record.getTreatment(),
                record.getNotes()
        );

        mailService.sendMail(MailDetailsDto.builder()
                .toMail(record.getPatient().getEmail())
                .subject("📢 Your Medical Record Has Been Updated")
                .message(html)
                .contentType("html")
                .build());
    }
}

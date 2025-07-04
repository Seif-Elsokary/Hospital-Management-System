package com.hospital.Hospital_Management_System.service.PrescriptionService;

import com.hospital.Hospital_Management_System.dto.MailDetailsDto;
import com.hospital.Hospital_Management_System.dto.PrescriptionDto;
import com.hospital.Hospital_Management_System.entity.Doctor;
import com.hospital.Hospital_Management_System.entity.MedicalRecord;
import com.hospital.Hospital_Management_System.entity.Patient;
import com.hospital.Hospital_Management_System.entity.Prescription;
import com.hospital.Hospital_Management_System.exception.DoctorNotFoundException;
import com.hospital.Hospital_Management_System.exception.MedicalRecordNotFoundException;
import com.hospital.Hospital_Management_System.exception.PatientNotFoundException;
import com.hospital.Hospital_Management_System.exception.PrescriptionNotFoundException;
import com.hospital.Hospital_Management_System.repository.DoctorRepository;
import com.hospital.Hospital_Management_System.repository.MedicalRecordRepository;
import com.hospital.Hospital_Management_System.repository.PatientRepository;
import com.hospital.Hospital_Management_System.repository.PrescriptionRepository;
import com.hospital.Hospital_Management_System.request.PrescriptionRequest.CreatePrescriptionRequest;
import com.hospital.Hospital_Management_System.request.PrescriptionRequest.UpdatePrescriptionRequest;
import com.hospital.Hospital_Management_System.service.MailService.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class PrescriptionService implements IPrescriptionService {
    private final PrescriptionRepository prescriptionRepository;
    private final MailService mailService;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    @Override
    public PrescriptionDto createPrescription(CreatePrescriptionRequest request) {
        Prescription prescription = addNewPrescription(request);

        if (request.getMedicalRecordId() != null) {
            MedicalRecord medicalRecord = medicalRecordRepository.findById(request.getMedicalRecordId())
                    .orElseThrow(() -> new MedicalRecordNotFoundException("Medical record with ID: " + request.getMedicalRecordId() + " not found"));
            prescription.setMedicalRecord(medicalRecord);
        }

        Prescription savedPrescription = prescriptionRepository.save(prescription);
        sendPrescriptionEmailToPatient(savedPrescription);
        log.info("Created new prescription with ID: {}", savedPrescription.getId());

        return convertToDto(savedPrescription);
    }

    @Override
    public PrescriptionDto updatePrescription(Long id, UpdatePrescriptionRequest request) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new PrescriptionNotFoundException("Prescription with ID: " + id + " not found"));

        updatePrescriptionInfo(request, prescription);
        Prescription updated = prescriptionRepository.save(prescription);
        log.info("Updated prescription with ID: {}", updated.getId());

        return convertToDto(updated);
    }

    @Override
    public void deletePrescription(Long id) {
        prescriptionRepository.findById(id)
                .ifPresentOrElse(prescription -> {
                    prescriptionRepository.delete(prescription);
                    log.info("Deleted prescription with ID: {}", id);
                }, () -> {
                    log.warn("Attempted to delete non-existent prescription with ID: {}", id);
                    throw new PrescriptionNotFoundException("Prescription with id: " + id + " Not Found!");
                });
    }

    @Override
    public PrescriptionDto getPrescriptionById(Long id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new PrescriptionNotFoundException("Prescription with id: " + id + " Not Found!"));
        return convertToDto(prescription);
    }

    @Override
    public List<PrescriptionDto> getAllPrescriptions() {
        return convertToDtoList(prescriptionRepository.findAll());
    }

    @Override
    public List<PrescriptionDto> getPrescriptionsByPatientId(Long patientId) {
        return convertToDtoList(prescriptionRepository.findByPatientId(patientId));
    }

    @Override
    public List<PrescriptionDto> getPrescriptionsByDoctorId(Long doctorId) {
        return convertToDtoList(prescriptionRepository.findByDoctorId(doctorId));
    }

    // =============================== [ HELPER METHODS ] ===============================

    private PrescriptionDto convertToDto(Prescription prescription) {
        return PrescriptionDto.builder()
                .id(prescription.getId())
                .medicineDescription(prescription.getMedicineDescription())
                .issueDate(prescription.getIssueDate())
                .doctorSpecialty(String.valueOf(prescription.getDoctor().getSpecialty()))
                .doctorId(prescription.getDoctor().getId())
                .doctorName(prescription.getDoctor().getName())
                .patientId(prescription.getPatient().getId())
                .patientName(prescription.getPatient().getName())
                .patientDisease(String.valueOf(prescription.getPatient().getDisease()))
                .build();
    }

    private List<PrescriptionDto> convertToDtoList(List<Prescription> prescriptions) {
        return prescriptions.stream()
                .map(this::convertToDto)
                .toList();
    }

    private Prescription addNewPrescription(CreatePrescriptionRequest request) {
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new DoctorNotFoundException("Doctor with ID: " + request.getDoctorId() + " not found"));

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new PatientNotFoundException("Patient with ID: " + request.getPatientId() + " not found"));

        return Prescription.builder()
                .medicineDescription(request.getMedicineDescription())
                .issueDate(request.getIssueDate())
                .doctor(doctor)
                .patient(patient)
                .build();
    }

    private void updatePrescriptionInfo(UpdatePrescriptionRequest request, Prescription prescription) {
        if (request.getMedicineDescription() != null) {
            prescription.setMedicineDescription(request.getMedicineDescription());
        }

        if (request.getIssueDate() != null) {
            prescription.setIssueDate(request.getIssueDate());
        }

        if (request.getDoctorId() != null &&
                (prescription.getDoctor() == null || !request.getDoctorId().equals(prescription.getDoctor().getId()))) {
            Doctor doctor = doctorRepository.findById(request.getDoctorId())
                    .orElseThrow(() -> new DoctorNotFoundException("Doctor with ID: " + request.getDoctorId() + " not found"));
            prescription.setDoctor(doctor);
        }

        if (request.getPatientId() != null &&
                (prescription.getPatient() == null || !request.getPatientId().equals(prescription.getPatient().getId()))) {
            Patient patient = patientRepository.findById(request.getPatientId())
                    .orElseThrow(() -> new PatientNotFoundException("Patient with ID: " + request.getPatientId() + " not found"));
            prescription.setPatient(patient);
        }

        if (request.getMedicalRecordId() != null &&
                (prescription.getMedicalRecord() == null || !request.getMedicalRecordId().equals(prescription.getMedicalRecord().getId()))) {
            MedicalRecord medicalRecord = medicalRecordRepository.findById(request.getMedicalRecordId())
                    .orElseThrow(() -> new MedicalRecordNotFoundException("Medical record with ID: " + request.getMedicalRecordId() + " not found"));
            prescription.setMedicalRecord(medicalRecord);
        }
    }

    private void sendPrescriptionEmailToPatient(Prescription prescription) {
        String html = """
        <html>
        <body style="font-family: Arial, sans-serif;">
            <h2 style="color: #28a745;">💊 Prescription Issued</h2>
            <p>Dear <strong>%s</strong>,</p>
            <p>Your doctor <strong>%s</strong> has issued a new prescription for you.</p>
            <ul>
                <li><strong>Medication:</strong> %s</li>
                <li><strong>Date Issued:</strong> %s</li>
                <li><strong>Medical Record Diagnosis:</strong> %s</li>
            </ul>
            <p>Wishing you a speedy recovery!</p>
        </body>
        </html>
        """.formatted(
                prescription.getPatient().getName(),
                prescription.getDoctor().getName(),
                prescription.getMedicineDescription(),
                prescription.getIssueDate(),
                prescription.getMedicalRecord() != null ? prescription.getMedicalRecord().getDiagnosis() : "N/A"
        );

        mailService.sendMail(MailDetailsDto.builder()
                .toMail(prescription.getPatient().getEmail())
                .subject("💊 New Prescription Issued")
                .message(html)
                .contentType("html")
                .build());
    }
}

package com.hospital.Hospital_Management_System.controller;

import com.hospital.Hospital_Management_System.dto.MedicalRecordDto;
import com.hospital.Hospital_Management_System.request.MedicalRecordRequest.CreateMedicalRecordRequest;
import com.hospital.Hospital_Management_System.request.MedicalRecordRequest.UpdateMedicalRecordRequest;
import com.hospital.Hospital_Management_System.service.MedicalRecordService.IMedicalRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medical-records")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final IMedicalRecordService medicalRecordService;

    @PostMapping
    public ResponseEntity<MedicalRecordDto> createRecord(@RequestBody CreateMedicalRecordRequest request) {
        MedicalRecordDto record = medicalRecordService.createMedicalRecord(request);
        return ResponseEntity.ok(record);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicalRecordDto> updateRecord(@PathVariable Long id,
                                                         @RequestBody UpdateMedicalRecordRequest request) {
        MedicalRecordDto updated = medicalRecordService.updateMedicalRecord(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecord(@PathVariable Long id) {
        medicalRecordService.deleteMedicalRecord(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecordDto> getRecord(@PathVariable Long id) {
        return ResponseEntity.ok(medicalRecordService.getMedicalRecordById(id));
    }

    @GetMapping
    public ResponseEntity<List<MedicalRecordDto>> getAllRecords() {
        return ResponseEntity.ok(medicalRecordService.getAllMedicalRecords());
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<MedicalRecordDto>> getRecordsByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(medicalRecordService.getRecordsByPatientId(patientId));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<MedicalRecordDto>> getRecordsByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(medicalRecordService.getRecordsByDoctorId(doctorId));
    }
}

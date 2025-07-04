package com.hospital.Hospital_Management_System.controller;

import com.hospital.Hospital_Management_System.dto.MedicalReportDto;
import com.hospital.Hospital_Management_System.request.MedicalReportRequest.CreateMedicalReportRequest;
import com.hospital.Hospital_Management_System.request.MedicalReportRequest.UpdateMedicalReportRequest;
import com.hospital.Hospital_Management_System.service.MedicalReportService.IMedicalReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medical-reports")
@RequiredArgsConstructor
public class MedicalReportController {

    private final IMedicalReportService medicalReportService;

    @PostMapping
    public ResponseEntity<MedicalReportDto> createReport(@RequestBody CreateMedicalReportRequest request) {
        MedicalReportDto report = medicalReportService.createReport(request);
        return ResponseEntity.ok(report);
    }
    @GetMapping("/record/{recordId}")
    public ResponseEntity<List<MedicalReportDto>> getReportsByMedicalRecord(@PathVariable Long recordId) {
        List<MedicalReportDto> reports = medicalReportService.getReportsByMedicalRecordId(recordId);
        return ResponseEntity.ok(reports);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicalReportDto> updateReport(
            @PathVariable Long id,
            @RequestBody UpdateMedicalReportRequest request) {
        MedicalReportDto updated = medicalReportService.updateReport(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        medicalReportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicalReportDto> getReportById(@PathVariable Long id) {
        return ResponseEntity.ok(medicalReportService.getReportById(id));
    }

    @GetMapping
    public ResponseEntity<List<MedicalReportDto>> getAllReports() {
        return ResponseEntity.ok(medicalReportService.getAllReports());
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<MedicalReportDto>> getReportsByPatientId(@PathVariable Long patientId) {
        return ResponseEntity.ok(medicalReportService.getReportsByPatientId(patientId));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<MedicalReportDto>> getReportsByDoctorId(@PathVariable Long doctorId) {
        return ResponseEntity.ok(medicalReportService.getReportsByDoctorId(doctorId));
    }

    @GetMapping("/medical-record/{recordId}")
    public ResponseEntity<List<MedicalReportDto>> getReportsByMedicalRecordId(@PathVariable Long recordId) {
        return ResponseEntity.ok(medicalReportService.getReportsByMedicalRecordId(recordId));
    }
}

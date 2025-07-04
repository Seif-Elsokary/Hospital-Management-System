package com.hospital.Hospital_Management_System.controller;

import com.hospital.Hospital_Management_System.dto.PrescriptionDto;
import com.hospital.Hospital_Management_System.request.PrescriptionRequest.CreatePrescriptionRequest;
import com.hospital.Hospital_Management_System.request.PrescriptionRequest.UpdatePrescriptionRequest;
import com.hospital.Hospital_Management_System.service.PrescriptionService.IPrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final IPrescriptionService prescriptionService;

    @PostMapping
    public ResponseEntity<PrescriptionDto> createPrescription(@RequestBody CreatePrescriptionRequest request) {
        PrescriptionDto created = prescriptionService.createPrescription(request);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PrescriptionDto> updatePrescription(@PathVariable Long id,
                                                              @RequestBody UpdatePrescriptionRequest request) {
        PrescriptionDto updated = prescriptionService.updatePrescription(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrescription(@PathVariable Long id) {
        prescriptionService.deletePrescription(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrescriptionDto> getPrescriptionById(@PathVariable Long id) {
        PrescriptionDto dto = prescriptionService.getPrescriptionById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<PrescriptionDto>> getAllPrescriptions() {
        return ResponseEntity.ok(prescriptionService.getAllPrescriptions());
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<PrescriptionDto>> getPrescriptionsByPatientId(@PathVariable Long patientId) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionsByPatientId(patientId));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<PrescriptionDto>> getPrescriptionsByDoctorId(@PathVariable Long doctorId) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionsByDoctorId(doctorId));
    }
}

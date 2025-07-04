package com.hospital.Hospital_Management_System.controller;

import com.hospital.Hospital_Management_System.dto.PatientDto;
import com.hospital.Hospital_Management_System.enums.Disease;
import com.hospital.Hospital_Management_System.enums.Gender;
import com.hospital.Hospital_Management_System.request.PatientRequest.CreatePatientRequest;
import com.hospital.Hospital_Management_System.request.PatientRequest.UpdatePatientRequest;
import com.hospital.Hospital_Management_System.service.PatientService.IPatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final IPatientService patientService;

    @PostMapping
    public ResponseEntity<PatientDto> createPatient(@RequestBody CreatePatientRequest request) {
        return ResponseEntity.ok(patientService.createPatient(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<PatientDto> updatePatient(@PathVariable Long id,
                                                    @RequestBody UpdatePatientRequest request) {
        return ResponseEntity.ok(patientService.updatePatient(request, id));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<PatientDto> getPatientById(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> deletePatient(@PathVariable Long id) {
        patientService.deletePatientById(id);
        return ResponseEntity.ok("Patient deleted successfully");
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<PatientDto>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<List<PatientDto>> searchPatients(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Disease disease,
            @RequestParam(required = false) Gender gender,
            @RequestParam(required = false) String bloodType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date dateOfRegistration) {
        return ResponseEntity.ok(
                patientService.searchPatients(name, disease, gender, bloodType, dateOfRegistration)
        );
    }

    @GetMapping("/by-name")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<List<PatientDto>> getByName(@RequestParam String name) {
        return ResponseEntity.ok(patientService.getPatientsByName(name));
    }

    @GetMapping("/by-disease")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<List<PatientDto>> getByDisease(@RequestParam Disease disease) {
        return ResponseEntity.ok(patientService.getPatientsByDisease(disease));
    }

    @GetMapping("/by-name-and-disease")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<List<PatientDto>> getByNameAndDisease(@RequestParam String name , @RequestParam Disease disease){
        return ResponseEntity.ok(patientService.getPatientByNameAndDisease(name , disease));
    }

    @GetMapping("/by-date")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<PatientDto>> getByDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date) {
        return ResponseEntity.ok(patientService.getPatientByDateOfRegistration(date));
    }

    @GetMapping("by-disease-and-gender")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<List<PatientDto>> getByDiseaseAndGender(@RequestParam Disease disease , @RequestParam Gender gender){
        return ResponseEntity.ok(patientService.getPatientsByDiseaseAndGender(disease , gender));
    }

    @GetMapping("/by-disease-and-gender-and-bloodType")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<PatientDto>> getByDiseaseAndGenderAndBloodType(@RequestParam Disease disease , @RequestParam Gender gender , @RequestParam String bloodType){
        return ResponseEntity.ok(patientService.getPatientsByDiseaseAndGenderAndBloodType(disease , gender , bloodType));
    }
}

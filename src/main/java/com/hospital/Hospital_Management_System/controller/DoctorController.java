package com.hospital.Hospital_Management_System.controller;

import com.hospital.Hospital_Management_System.dto.DoctorDto;
import com.hospital.Hospital_Management_System.enums.Gender;
import com.hospital.Hospital_Management_System.enums.Specialty;
import com.hospital.Hospital_Management_System.request.DoctorRequest.CreateDoctorRequest;
import com.hospital.Hospital_Management_System.request.DoctorRequest.UpdateDoctorRequest;
import com.hospital.Hospital_Management_System.service.DoctorService.IDoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final IDoctorService doctorService;

    @PostMapping
    public ResponseEntity<DoctorDto> createDoctor(@RequestBody CreateDoctorRequest request) {
        return ResponseEntity.ok(doctorService.createDoctor(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<DoctorDto> updateDoctor(@PathVariable Long id,
                                                  @RequestBody UpdateDoctorRequest request) {
        return ResponseEntity.ok(doctorService.updateDoctor(request, id));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<DoctorDto> getDoctorById(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> deleteDoctorById(@PathVariable Long id) {
        doctorService.deleteDoctorById(id);
        return ResponseEntity.ok("Doctor deleted successfully");
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<List<DoctorDto>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    @GetMapping("/by-name")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<List<DoctorDto>> getDoctorsByName(@RequestParam String name) {
        return ResponseEntity.ok(doctorService.getDoctorsByName(name));
    }

    @GetMapping("/by-specialty")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<List<DoctorDto>> getDoctorsBySpecialty(@RequestParam Specialty specialty) {
        return ResponseEntity.ok(doctorService.getDoctorsBySpecialty(specialty));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<List<DoctorDto>> searchDoctors(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Specialty specialty,
            @RequestParam(required = false) Gender gender,
            @RequestParam(required = false) Integer minExperience
    ) {
        return ResponseEntity.ok(doctorService.searchDoctors(name, specialty, gender, minExperience));
    }
}

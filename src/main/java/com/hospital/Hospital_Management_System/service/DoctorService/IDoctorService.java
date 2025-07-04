package com.hospital.Hospital_Management_System.service.DoctorService;

import com.hospital.Hospital_Management_System.dto.DoctorDto;
import com.hospital.Hospital_Management_System.enums.Gender;
import com.hospital.Hospital_Management_System.enums.Specialty;
import com.hospital.Hospital_Management_System.request.DoctorRequest.CreateDoctorRequest;
import com.hospital.Hospital_Management_System.request.DoctorRequest.UpdateDoctorRequest;

import java.util.List;

public interface IDoctorService {

    DoctorDto createDoctor(CreateDoctorRequest request);

    DoctorDto updateDoctor(UpdateDoctorRequest request, Long id);

    DoctorDto getDoctorById(Long id);

    void deleteDoctorById(Long id);

    List<DoctorDto> getAllDoctors();

    List<DoctorDto> getDoctorsByName(String name);

    List<DoctorDto> getDoctorsBySpecialty(Specialty specialty);



    List<DoctorDto> searchDoctors(String name, Specialty specialty, Gender gender, Integer minExperience);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);
}

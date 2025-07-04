package com.hospital.Hospital_Management_System.service.PatientService;

import com.hospital.Hospital_Management_System.dto.PatientDto;
import com.hospital.Hospital_Management_System.enums.Disease;
import com.hospital.Hospital_Management_System.enums.Gender;
import com.hospital.Hospital_Management_System.request.PatientRequest.CreatePatientRequest;
import com.hospital.Hospital_Management_System.request.PatientRequest.UpdatePatientRequest;
import lombok.Data;

import java.util.Date;
import java.util.List;
public interface IPatientService {

    PatientDto getPatientById(Long patientId);

    PatientDto createPatient(CreatePatientRequest request);

    PatientDto updatePatient(UpdatePatientRequest request , Long patientId);

    void deletePatientById(Long patientId);

    List<PatientDto> getAllPatients();

    List<PatientDto> getPatientsByName(String name);

    List<PatientDto> getPatientByNameAndDisease(String name, Disease disease);

    List<PatientDto> getPatientsByDisease(Disease disease);

    List<PatientDto> getPatientsByDiseaseAndGender(Disease disease, Gender gender);

    List<PatientDto> getPatientsByDiseaseAndGenderAndBloodType(Disease disease, Gender gender, String bloodType);

    List<PatientDto> getPatientByDateOfRegistration(Date dateOfRegistration);

    List<PatientDto> getPatientByDateOfRegistrationAndDisease(Date dateOfRegistration, Disease disease);

    List<PatientDto> searchPatients(String name, Disease disease, Gender gender, String bloodType, Date dateOfRegistration);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);
}

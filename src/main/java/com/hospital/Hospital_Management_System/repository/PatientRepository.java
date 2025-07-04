package com.hospital.Hospital_Management_System.repository;

import com.hospital.Hospital_Management_System.entity.Patient;
import com.hospital.Hospital_Management_System.enums.Disease;
import com.hospital.Hospital_Management_System.enums.Gender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long>, JpaSpecificationExecutor<Patient> {

    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

    List<Patient> findByNameContainingIgnoreCase(String name);
    List<Patient> findByDateOfRegistration(Date date);
    List<Patient> findByDateOfRegistrationAndDisease(Date date, Disease disease);

    List<Patient> findByDiseaseAndGenderAndBloodType(Disease disease, Gender gender, String bloodType);

    List<Patient> findByDisease(Disease disease);

    List<Patient> findByDiseaseAndGender(Disease disease, Gender gender);

    List<Patient> findByNameContainingIgnoreCaseAndDisease(String name, Disease disease);

    Optional<Patient> findByEmail(String email);
}

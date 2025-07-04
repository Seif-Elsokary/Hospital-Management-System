package com.hospital.Hospital_Management_System.repository;

import com.hospital.Hospital_Management_System.enums.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import com.hospital.Hospital_Management_System.entity.Doctor;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;


public interface DoctorRepository extends JpaRepository<Doctor, Long>, JpaSpecificationExecutor<Doctor> {

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    List<Doctor> findByNameEqualsIgnoreCase(String name);

    List<Doctor> findBySpecialty(Specialty specialty);

    Optional<Doctor> findByEmail(String email);
}

package com.hospital.Hospital_Management_System.repository;

import com.hospital.Hospital_Management_System.entity.MedicalRecord;
import com.hospital.Hospital_Management_System.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    List<MedicalRecord> findByPatientId(Patient patient);
    List<MedicalRecord> findByDoctorId(Long doctorId);

    boolean existsByPatientEmailAndVisitDate(String email, Date visitDate);
}

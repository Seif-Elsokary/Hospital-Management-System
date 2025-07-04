package com.hospital.Hospital_Management_System.repository;

import com.hospital.Hospital_Management_System.entity.Appointment;
import com.hospital.Hospital_Management_System.entity.Doctor;
import com.hospital.Hospital_Management_System.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment , Long> {
    List<Appointment> findByDoctorId(Doctor doctor);

    List<Appointment> findByPatientId(Patient patient);
}

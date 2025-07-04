package com.hospital.Hospital_Management_System.service.PrescriptionService;

import com.hospital.Hospital_Management_System.dto.PrescriptionDto;
import com.hospital.Hospital_Management_System.request.PrescriptionRequest.CreatePrescriptionRequest;
import com.hospital.Hospital_Management_System.request.PrescriptionRequest.UpdatePrescriptionRequest;

import java.util.List;

public interface IPrescriptionService {

    PrescriptionDto createPrescription(CreatePrescriptionRequest request);

    PrescriptionDto updatePrescription(Long id, UpdatePrescriptionRequest request);

    void deletePrescription(Long id);

    PrescriptionDto getPrescriptionById(Long id);

    List<PrescriptionDto> getAllPrescriptions();

    List<PrescriptionDto> getPrescriptionsByPatientId(Long patientId);

    List<PrescriptionDto> getPrescriptionsByDoctorId(Long doctorId);
}

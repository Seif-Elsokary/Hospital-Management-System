package com.hospital.Hospital_Management_System.service.MedicalRecordService;

import com.hospital.Hospital_Management_System.dto.MedicalRecordDto;
import com.hospital.Hospital_Management_System.request.MedicalRecordRequest.CreateMedicalRecordRequest;
import com.hospital.Hospital_Management_System.request.MedicalRecordRequest.UpdateMedicalRecordRequest;

import java.util.List;

public interface IMedicalRecordService {

    MedicalRecordDto createMedicalRecord(CreateMedicalRecordRequest request);

    MedicalRecordDto updateMedicalRecord(Long id, UpdateMedicalRecordRequest request);

    void deleteMedicalRecord(Long id);

    MedicalRecordDto getMedicalRecordById(Long id);

    List<MedicalRecordDto> getAllMedicalRecords();

    List<MedicalRecordDto> getRecordsByPatientId(Long patientId);

    List<MedicalRecordDto> getRecordsByDoctorId(Long doctorId);


}

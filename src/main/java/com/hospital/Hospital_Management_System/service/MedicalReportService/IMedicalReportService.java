package com.hospital.Hospital_Management_System.service.MedicalReportService;

import com.hospital.Hospital_Management_System.dto.MedicalReportDto;
import com.hospital.Hospital_Management_System.request.MedicalReportRequest.CreateMedicalReportRequest;
import com.hospital.Hospital_Management_System.request.MedicalReportRequest.UpdateMedicalReportRequest;

import java.util.List;

public interface IMedicalReportService {

    MedicalReportDto createReport(CreateMedicalReportRequest request);

    MedicalReportDto updateReport(Long id, UpdateMedicalReportRequest request);

    void deleteReport(Long id);

    MedicalReportDto getReportById(Long id);

    List<MedicalReportDto> getAllReports();

    List<MedicalReportDto> getReportsByPatientId(Long patientId);

    List<MedicalReportDto> getReportsByDoctorId(Long doctorId);

    List<MedicalReportDto> getReportsByMedicalRecordId(Long recordId);
}

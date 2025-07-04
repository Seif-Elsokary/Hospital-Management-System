package com.hospital.Hospital_Management_System.service.PatientService;

import com.hospital.Hospital_Management_System.dto.AppointmentDto;
import com.hospital.Hospital_Management_System.dto.PatientDto;
import com.hospital.Hospital_Management_System.entity.Appointment;
import com.hospital.Hospital_Management_System.entity.Patient;
import com.hospital.Hospital_Management_System.enums.Disease;
import com.hospital.Hospital_Management_System.enums.Gender;
import com.hospital.Hospital_Management_System.enums.Role;
import com.hospital.Hospital_Management_System.exception.AlreadyExistsException;
import com.hospital.Hospital_Management_System.exception.PatientNotFoundException;
import com.hospital.Hospital_Management_System.repository.PatientRepository;
import com.hospital.Hospital_Management_System.request.PatientRequest.CreatePatientRequest;
import com.hospital.Hospital_Management_System.request.PatientRequest.UpdatePatientRequest;
import com.hospital.Hospital_Management_System.CriteriaQuery.PatientSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class PatientService implements IPatientService {

    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public PatientDto getPatientById(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException("Patient with ID: " + patientId + " not found"));
        return convertToPatientDto(patient);
    }

    @Override
    public PatientDto createPatient(CreatePatientRequest request) {
        if (request.getDisease() == null || !isValidEnumValue(Disease.class, request.getDisease().name())) {
            String available = getEnumValues(Disease.class);
            throw new IllegalArgumentException("Disease '" + request.getDisease() + "' isn't available. Available diseases: " + available);
        }

        if (request.getEmail() == null || request.getPhone() == null) {
            throw new IllegalArgumentException("Email and phone are required");
        }

        if (patientRepository.existsByEmail(request.getEmail()) || patientRepository.existsByPhone(request.getPhone())) {
            throw new AlreadyExistsException("Patient with email or phone already exists");
        }

        Patient newPatient = addNewPatientInfo(request);
        newPatient.setDisease(Disease.valueOf(request.getDisease().name().toUpperCase()));

        newPatient = patientRepository.save(newPatient);
        log.info("New patient created with ID: {}", newPatient.getId());

        return convertToPatientDto(newPatient);
    }

    @Override
    public PatientDto updatePatient(UpdatePatientRequest request, Long patientId) {
        Patient existingPatient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException("Patient with ID: " + patientId + " not found"));

        if (request.getDisease() != null && !isValidEnumValue(Disease.class, request.getDisease().name())) {
            String available = getEnumValues(Disease.class);
            throw new IllegalArgumentException("Disease '" + request.getDisease() + "' isn't available. Available diseases: " + available);
        }

        updatePatientInformation(request, existingPatient);

        if (request.getDisease() != null) {
            existingPatient.setDisease(Disease.valueOf(request.getDisease().name().toUpperCase()));
        }

        Patient updatedPatient = patientRepository.save(existingPatient);
        return convertToPatientDto(updatedPatient);
    }

    @Override
    public void deletePatientById(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException("Patient with ID: " + patientId + " not found"));
        patientRepository.delete(patient);
    }

    @Override
    public List<PatientDto> getAllPatients() {
        return convertToPatientDtoList(patientRepository.findAll());
    }

    @Override
    public List<PatientDto> getPatientsByName(String name) {
        return convertToPatientDtoList(patientRepository.findByNameContainingIgnoreCase(name));
    }

    @Override
    public List<PatientDto> getPatientByNameAndDisease(String name, Disease disease) {
        return convertToPatientDtoList(
                patientRepository.findByNameContainingIgnoreCaseAndDisease(name, disease));
    }

    @Override
    public List<PatientDto> getPatientsByDisease(Disease disease) {
        return convertToPatientDtoList(patientRepository.findByDisease(disease));
    }

    @Override
    public List<PatientDto> getPatientsByDiseaseAndGender(Disease disease, Gender gender) {
        return convertToPatientDtoList(patientRepository.findByDiseaseAndGender(disease, gender));
    }

    @Override
    public List<PatientDto> getPatientsByDiseaseAndGenderAndBloodType(Disease disease, Gender gender, String bloodType) {
        return convertToPatientDtoList(
                patientRepository.findByDiseaseAndGenderAndBloodType(disease, gender, bloodType));
    }

    @Override
    public List<PatientDto> getPatientByDateOfRegistration(Date dateOfRegistration) {
        return convertToPatientDtoList(patientRepository.findByDateOfRegistration(dateOfRegistration));
    }

    @Override
    public List<PatientDto> getPatientByDateOfRegistrationAndDisease(Date dateOfRegistration, Disease disease) {
        return convertToPatientDtoList(
                patientRepository.findByDateOfRegistrationAndDisease(dateOfRegistration, disease));
    }

    @Override
    public List<PatientDto> searchPatients(String name, Disease disease, Gender gender, String bloodType, Date dateOfRegistration) {
        Specification<Patient> spec = Specification.allOf();

        if (name != null && !name.isBlank())
            spec = spec.and(PatientSpecification.hasName(name));

        if (disease != null)
            spec = spec.and(PatientSpecification.hasDisease(disease));

        if (gender != null)
            spec = spec.and(PatientSpecification.hasGender(gender));

        if (bloodType != null && !bloodType.isBlank())
            spec = spec.and(PatientSpecification.hasBloodType(bloodType));

        if (dateOfRegistration != null)
            spec = spec.and(PatientSpecification.hasDateOfRegistration(dateOfRegistration));

        return convertToPatientDtoList(patientRepository.findAll(spec));
    }

    @Override
    public boolean existsByEmail(String email) {
        return patientRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhone(String phone) {
        return patientRepository.existsByPhone(phone);
    }

    // ======= Helpers =======

    private PatientDto convertToPatientDto(Patient patient) {
        return PatientDto.builder()
                .id(patient.getId())
                .name(patient.getName()).email(patient.getEmail())
                .phone(patient.getPhone()).address(patient.getAddress())
                .age(patient.getAge()).bloodType(patient.getBloodType())
                .disease(patient.getDisease()).gender(patient.getGender())
                .dateOfRegistration(patient.getDateOfRegistration())
                .appointments(convertAppointmentToDtoList(patient.getAppointments()))
                .build();
    }

    private List<AppointmentDto> convertAppointmentToDtoList(List<Appointment> appointments) {
        if (appointments == null) return List.of();

        return appointments.stream()
                .map(appointment -> AppointmentDto.builder()
                        .id(appointment.getId())
                        .date(appointment.getDate())
                        .reason(appointment.getReason())
                        .status(appointment.getStatus())
                        .doctorId(appointment.getDoctor().getId())
                        .doctorName(appointment.getDoctor().getName())
                        .patientId(appointment.getPatient().getId())
                        .patientName(appointment.getPatient().getName())
                        .disease(String.valueOf(appointment.getPatient().getDisease()))
                        .build())
                .toList();
    }


    private List<PatientDto> convertToPatientDtoList(List<Patient> patients) {
        return patients.stream()
                .map(this::convertToPatientDto)
                .toList();
    }

    private Patient addNewPatientInfo(CreatePatientRequest patient) {
        return Patient.builder()
                .name(patient.getName()).email(patient.getEmail())
                .phone(patient.getPhone()).address(patient.getAddress())
                .age(patient.getAge()).bloodType(patient.getBloodType())
                .disease(patient.getDisease()).gender(patient.getGender())
                .dateOfRegistration(patient.getDateOfRegistration())
                .password(passwordEncoder.encode(patient.getPassword()))
                .role(Role.USER)
                .build();
    }

    private Patient updatePatientInformation(UpdatePatientRequest request, Patient existingPatient) {
        if (request.getName() != null) existingPatient.setName(request.getName());
        if (request.getEmail() != null) existingPatient.setEmail(request.getEmail());
        if (request.getPhone() != null) existingPatient.setPhone(request.getPhone());
        if (request.getAddress() != null) existingPatient.setAddress(request.getAddress());
        if (request.getAge() != 0) existingPatient.setAge(request.getAge());
        if (request.getBloodType() != null) existingPatient.setBloodType(request.getBloodType());
        if (request.getGender() != null) existingPatient.setGender(request.getGender());
        if (request.getDateOfRegistration() != null) existingPatient.setDateOfRegistration(request.getDateOfRegistration());
        existingPatient.setPassword(passwordEncoder.encode(request.getPassword()));
        return existingPatient;
    }

    private <E extends Enum<E>> boolean isValidEnumValue(Class<E> enumClass, String value) {
        return Arrays.stream(enumClass.getEnumConstants())
                .anyMatch(e -> e.name().equalsIgnoreCase(value));
    }

    private <E extends Enum<E>> String getEnumValues(Class<E> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .map(Enum::name)
                .toList()
                .toString();
    }
}

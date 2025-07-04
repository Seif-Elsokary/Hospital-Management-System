package com.hospital.Hospital_Management_System.service.DoctorService;

import com.hospital.Hospital_Management_System.dto.AppointmentDto;
import com.hospital.Hospital_Management_System.dto.DoctorDto;
import com.hospital.Hospital_Management_System.entity.Appointment;
import com.hospital.Hospital_Management_System.entity.Doctor;
import com.hospital.Hospital_Management_System.enums.Gender;
import com.hospital.Hospital_Management_System.enums.Role;
import com.hospital.Hospital_Management_System.enums.Specialty;
import com.hospital.Hospital_Management_System.exception.AlreadyExistsException;
import com.hospital.Hospital_Management_System.exception.DoctorNotFoundException;
import com.hospital.Hospital_Management_System.repository.DoctorRepository;
import com.hospital.Hospital_Management_System.request.DoctorRequest.CreateDoctorRequest;
import com.hospital.Hospital_Management_System.request.DoctorRequest.UpdateDoctorRequest;
import com.hospital.Hospital_Management_System.CriteriaQuery.DoctorSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class DoctorService implements IDoctorService {

    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public DoctorDto createDoctor(CreateDoctorRequest request) {
        if (request.getSpecialty() == null || !isValidEnumValue(Specialty.class, request.getSpecialty().name())) {
            String availableSpecialties = getEnumValues(Specialty.class);
            throw new IllegalArgumentException("Specialty '" + request.getSpecialty() + "' isn't available. Available specialties: " + availableSpecialties);
        }

        if (request.getEmail() == null || request.getPhone() == null) {
            throw new IllegalArgumentException("Email and phone are required");
        }

        if (doctorRepository.existsByEmail(request.getEmail()) || doctorRepository.existsByPhone(request.getPhone())) {
            throw new AlreadyExistsException("Doctor with email or phone already exists");
        }

        Doctor newDoctor = addNewDoctor(request);
        newDoctor = doctorRepository.save(newDoctor);

        log.info("New Doctor created with ID: {}", newDoctor.getId());
        return convertToDoctorDto(newDoctor);
    }

    @Override
    public DoctorDto updateDoctor(UpdateDoctorRequest request, Long id) {
        Doctor existingDoctor = doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor with id: " + id + " Not Found!"));

        if (request.getSpecialty() != null && !isValidEnumValue(Specialty.class, request.getSpecialty().name())) {
            String availableSpecialties = getEnumValues(Specialty.class);
            throw new IllegalArgumentException("Specialty '" + request.getSpecialty() + "' isn't available. Available specialties: " + availableSpecialties);
        }

        updateDoctorInfo(request, existingDoctor);
        Doctor updatedDoctor = doctorRepository.save(existingDoctor);

        return convertToDoctorDto(updatedDoctor);
    }

    @Override
    public DoctorDto getDoctorById(Long id) {
        Doctor getById = doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor with id: " + id + " Not Found!"));

        return convertToDoctorDto(getById);
    }

    @Override
    public void deleteDoctorById(Long id) {
        Doctor deleteById = doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor with id: " + id + " Not Found!"));

        doctorRepository.delete(deleteById);
    }

    @Override
    public List<DoctorDto> getAllDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();
        return convertDoctorToDtoList(doctors);
    }

    @Override
    public List<DoctorDto> getDoctorsByName(String name) {
        List<Doctor> doctorsByName = doctorRepository.findByNameEqualsIgnoreCase(name);
        return convertDoctorToDtoList(doctorsByName);
    }

    @Override
    public List<DoctorDto> getDoctorsBySpecialty(Specialty specialty) {
        List<Doctor> doctorsBySpecialty = doctorRepository.findBySpecialty(specialty);
        return convertDoctorToDtoList(doctorsBySpecialty);
    }

    @Override
    public List<DoctorDto> searchDoctors(String name, Specialty specialty, Gender gender, Integer minExperience) {
        Specification<Doctor> spec = Specification.allOf();

        if (name != null) spec = spec.and(DoctorSpecification.hasName(name));
        if (specialty != null) spec = spec.and(DoctorSpecification.hasSpecialty(specialty));
        if (gender != null) spec = spec.and(DoctorSpecification.hasGender(gender));
        if (minExperience != null) spec = spec.and(DoctorSpecification.hasMinExperience(minExperience));

        List<Doctor> doctors = doctorRepository.findAll(spec);
        return convertDoctorToDtoList(doctors);
    }

    @Override
    public boolean existsByEmail(String email) {
        return doctorRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhone(String phone) {
        return doctorRepository.existsByPhone(phone);
    }

    // ======== Helper Methods ========

    private DoctorDto convertToDoctorDto(Doctor doctor) {
        return DoctorDto.builder()
                .id(doctor.getId())
                .email(doctor.getEmail())
                .phone(doctor.getPhone())
                .address(doctor.getAddress())
                .age(doctor.getAge())
                .gender(doctor.getGender())
                .name(doctor.getName())
                .specialty(String.valueOf(doctor.getSpecialty()).toUpperCase())
                .yearOfExperience(doctor.getYearOfExperience())
                .appointments(convertAppointmentToDtoList(doctor.getAppointments()))
                .build();
    }

    private List<DoctorDto> convertDoctorToDtoList(List<Doctor> doctors) {
        return doctors.stream()
                .map(this::convertToDoctorDto)
                .toList();
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




    private Doctor addNewDoctor(CreateDoctorRequest request) {
        return Doctor.builder()
                .name(request.getName())
                .email(request.getEmail())
                .specialty(Specialty.valueOf(request.getSpecialty().name().toUpperCase()))
                .age(request.getAge())
                .yearOfExperience(request.getYearOfExperience())
                .gender(request.getGender())
                .phone(request.getPhone())
                .address(request.getAddress())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ADMIN)
                .build();
    }

    private Doctor updateDoctorInfo(UpdateDoctorRequest request, Doctor existingDoctor) {
        if (request.getName() != null) existingDoctor.setName(request.getName());
        if (request.getEmail() != null) existingDoctor.setEmail(request.getEmail());
        if (request.getSpecialty() != null) existingDoctor.setSpecialty(Specialty.valueOf(request.getSpecialty().name().toUpperCase()));
        if (request.getPhone() != null) existingDoctor.setPhone(request.getPhone());
        if (request.getGender() != null) existingDoctor.setGender(request.getGender());
        if (request.getAddress() != null) existingDoctor.setAddress(request.getAddress());
        existingDoctor.setPassword(passwordEncoder.encode(request.getPassword()));
        existingDoctor.setAge(request.getAge());
        existingDoctor.setYearOfExperience(request.getYearOfExperience());
        return existingDoctor;
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

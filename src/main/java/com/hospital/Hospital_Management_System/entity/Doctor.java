package com.hospital.Hospital_Management_System.entity;

import com.hospital.Hospital_Management_System.enums.Gender;
import com.hospital.Hospital_Management_System.enums.Role;
import com.hospital.Hospital_Management_System.enums.Specialty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "doctors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    private String password;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^\\d{10,15}$", message = "Phone must be between 10 and 15 digits")
    private String phone;

    @Min(value = 25, message = "Doctor must be at least 25 years old")
    @Max(value = 80, message = "Doctor's age must be less than or equal to 80")
    private int age;

    @Min(value = 0, message = "Years of experience must be positive")
    private int yearOfExperience;

    @NotBlank(message = "Specialty is required")
    @Enumerated(EnumType.STRING)
    private Specialty specialty;

    @NotBlank(message = "Address is required")
    private String address;

    @NotNull(message = "Gender is required")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @OneToMany(mappedBy = "doctor")
    private List<Appointment> appointments;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicalRecord> medicalRecords;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    private List<Prescription> prescriptions;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    private List<MedicalReport> medicalReports;

    @Enumerated(EnumType.STRING)
    private Role role;

}
